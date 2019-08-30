package chao.android.tools.service_pools.event;

import chao.java.tools.servicepool.annotation.Event;

/**
 * @author luqin
 * @since 2019-08-29
 */
@Event
public interface HisEvent {
    void postHisEvent();
}
