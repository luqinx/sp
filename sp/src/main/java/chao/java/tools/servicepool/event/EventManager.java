package chao.java.tools.servicepool.event;

import chao.java.tools.servicepool.ILogger;
import chao.java.tools.servicepool.ServicePool;
import com.luqinx.interceptor.Interceptor;
import com.luqinx.interceptor.OnInvoke;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author luqin
 * @since 2019-08-27
 */
public class EventManager {

    private static final String TAG = "sp.EventManager";

    private ILogger logger;

//    private List<WeakReference<EventService>> weakServices;

    private Map<Class, WeakList<EventService>> weakServiceMap;


    public EventManager() {
        logger = ServicePool.getService(ILogger.class);
//        weakServices = new ArrayList<>();
        weakServiceMap = new HashMap<>();
    }

    public void registerEventService(EventService eventService) {
        if (eventService == null) {
            logger.w(TAG, "can't register a null object as a EventService.");
            return;
        }

        Class clazz = eventService.getClass();

        WeakList<EventService> weakServices;

//        //注册clazz
//        if (weakServices == null) {
//            weakServices = new WeakList<>();
//            weakServiceMap.put(clazz, weakServices);
//        }
//
//        weakServices.addIfAbsent(eventService);


        //注册clazz的所有接口
        for (Class inf: clazz.getInterfaces()) {
            //EventService不作为Event类型
            if (inf == EventService.class) {
                continue;
            }
            //EventService的子类接口作为Event类型
            if (!EventService.class.isAssignableFrom(inf)) {
                continue;
            }

            weakServices = weakServiceMap.get(inf);
            if (weakServices == null) {
                weakServices = new WeakList<>();
                weakServiceMap.put(inf, weakServices);
            }

            weakServices.addIfAbsent(eventService);
        }

//        //注册clazz的所有父类
//        clazz = clazz.getSuperclass();
//        while (clazz != null) {
//
//            weakServices = weakServiceMap.get(clazz);
//            if (weakServices == null) {
//                weakServices = new WeakList<>();
//                weakServiceMap.put(clazz, weakServices);
//            }
//
//            weakServices.addIfAbsent(eventService);
//            clazz = clazz.getSuperclass();
//        }


    }

    public <T extends EventService> T getEventService(final Class<T> eventClazz) {
        return Interceptor.of(null, eventClazz).intercepted(true).invoke(new OnInvoke<T>() {
            @Override
            public Object onInvoke(T source, Method method, Object[] args) {
                WeakList<EventService> list = weakServiceMap.get(eventClazz);
                if (list == null) {
                    return null;
                }
                list.tidy();
                Object result = null;
                for (EventService event: list) {
                    if (event == null) {
                        continue;
                    }
                    try {
                        result = method.invoke(event, args);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                return result;
            }
        }).newInstance();
    }
}
