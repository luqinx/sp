package chao.android.tools.servicepool.init;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import chao.java.tools.servicepool.Constant;
import chao.java.tools.servicepool.DependencyManager;
import chao.java.tools.servicepool.IInitService;
import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.InitProxy;
import chao.java.tools.servicepool.NoOpInstance;
import chao.java.tools.servicepool.ServicePool;
import chao.java.tools.servicepool.ServiceProxy;
import chao.java.tools.servicepool.ServiceThreadFactory;
import chao.java.tools.servicepool.annotation.Service;

/**
 *
 * 懒加载
 *
 * @author qinchao
 * @since 2019/4/30
 */
@Service(scope = IService.Scope.global)
public class DefaultDependencyManager implements DependencyManager, Handler.Callback {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));

    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

    private static final int KEEP_ALIVE_SECONDS = 30;


    private static final int MESSAGE_DISPATCH = 1;

    private Handler mHandler;

    private Map<IInitService, InitProxy> initServices = new ConcurrentHashMap<>();

    private ThreadPoolExecutor threadExecutor;

    private final Object initProxyLock = new Object();

    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(128);


    public DefaultDependencyManager() {
        threadExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
            sPoolWorkQueue, new ServiceThreadFactory());

        mHandler = new Handler(Looper.getMainLooper(), this);
    }

//    @Override
//    public void addService(IInitService service) {
//        initServices.put(service, new InitProxy(service));
//    }

//    @Override
//    public void servicesInit() {
//        //如果
//        for (InitProxy service: initServices.values()) {
//            for (IInitService depend: service.dependencies()) {
//                InitProxy dependWrapper = initServices.get(depend);
//                dependWrapper.dependents.add(service);
//            }
//        }
//        for (InitProxy service: initServices.values()) {
//            tryInit(service);
//        }
//    }

    @Override
    public void tryInitService(IInitService service, List<Class<? extends IInitService>> dependencies, boolean async) {
        InitProxy initProxy = initServices.get(service);
        if (initProxy == null) {
            synchronized (initProxyLock) {
                initProxy = initServices.get(service);
                if (initProxy == null) {
                    initProxy = new InitProxy(service, dependencies, async);
                    System.out.println("new init proxy: " + initProxy + ", " + service);
                    initServices.put(service, initProxy);
                }
            }
        }
        tryInit(initProxy);
    }

    private void tryInit(final InitProxy initProxy) {
        if (!initProxy.initState.compareAndSet(Constant.initState.UNINIT, Constant.initState.TRYING)) {
            return;
        }
        boolean canInit = true;
        for (Class<? extends IInitService> dependClazz: initProxy.dependencies()) {
            ServiceProxy dependProxy = ServicePool.getProxy(dependClazz);
            if (dependProxy == null) {
                continue;
            }
            IService dependService = dependProxy.getService();
            if (dependService instanceof NoOpInstance) {
                continue;
            }
            IInitService dependInitService;
            if (!(dependService instanceof IInitService)) {
                continue;
            }
            dependInitService = (IInitService) dependService;

            InitProxy dependWrapper = initServices.get(dependInitService);
            if (dependWrapper == null) {
                dependWrapper = new InitProxy(dependInitService, dependProxy.dependencies(), dependProxy.async());
                initServices.put(dependInitService, dependWrapper);
            }
            dependWrapper.getDependents().add(initProxy);
            if (dependWrapper.initState.get() != Constant.initState.INITED) {
                canInit = false;
            }
        }

        if (!canInit) {
            return;
        }
        if (initProxy.async()) {
            threadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    initProxy.onInit();//todo 等待超时
                    dispatchServiceInited(initProxy);
                }
            });
        } else {
            initProxy.onInit();
            sendDispatchMessage(initProxy);
        }
    }

    private void dispatchServiceInited(InitProxy service) {
        for (InitProxy dependent: service.getDependents()) {
            tryInit(dependent);
        }
    }

    private void sendDispatchMessage(InitProxy initProxy) {
        Message message = mHandler.obtainMessage(MESSAGE_DISPATCH);
        message.obj = initProxy;
        message.sendToTarget();
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == MESSAGE_DISPATCH) {
            dispatchServiceInited((InitProxy) msg.obj);
            return true;
        }
        return false;
    }


    private interface InitListener {
        void onServiceInited(IInitService service);
    }
}
