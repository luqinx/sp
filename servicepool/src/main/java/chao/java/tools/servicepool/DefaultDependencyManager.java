package chao.java.tools.servicepool;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author qinchao
 * @since 2019/4/30
 */
class DefaultDependencyManager implements DependencyManager {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));

    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

    private static final int KEEP_ALIVE_SECONDS = 30;

    private Map<IInitService, InitService> initServices = new ConcurrentHashMap<>();

    private ThreadPoolExecutor threadExecutor;

    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(128);


    public DefaultDependencyManager() {
        threadExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
            sPoolWorkQueue, new ServiceThreadFactory());
    }

    @Override
    public void addService(IInitService service) {
        initServices.put(service, new InitService(service));
    }

    @Override
    public void servicesInit() {
        //如果
        for (InitService service: initServices.values()) {
            for (IInitService depend: service.dependencies()) {
                InitService dependWrapper = initServices.get(depend);
                dependWrapper.dependents.add(service);
            }
        }
        for (InitService service: initServices.values()) {
            tryInit(service);
        }
    }

    private void tryInit(final InitService service) {
        if (service.initState.get() != Constant.initState.UNINIT) {
            return;
        }
        boolean canInit = true;
        for (IInitService depend: service.dependencies()) {
            InitService dependWrapper = initServices.get(depend);
            if (dependWrapper.initState.get() != Constant.initState.INITED) {
                canInit = false;
            }
            if (dependWrapper.initState.get() == Constant.initState.UNINIT) {
                tryInit(dependWrapper);
            }
        }
        if (canInit) {
            threadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    service.onInit();//todo 等待超时
                    dispatchServiceInited(service);
                }
            });
        }
    }

    private void dispatchServiceInited(InitService service) {
        service.initState.set(Constant.initState.INITED);
        for (IInitService dependent: service.dependents) {
            tryInit(initServices.get(dependent));
        }
    }


    private interface InitListener {
        void onServiceInited(IInitService service);
    }
}
