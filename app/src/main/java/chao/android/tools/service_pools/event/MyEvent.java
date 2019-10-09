package chao.android.tools.service_pools.event;

import chao.java.tools.servicepool.annotation.Event;
import chao.java.tools.servicepool.event.EventService;

/**
 * @author luqin
 * @since 2019-08-27
 */
@Event
public interface MyEvent {
    void postEvent();
}
