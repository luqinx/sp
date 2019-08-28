package chao.android.tools.service_pools.event;

import chao.android.tools.service_pools.Printer;
import chao.java.tools.servicepool.ServicePool;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2019-08-27
 */
public class EventSample implements MyEvent {

    private int event;

    @Service
    private Printer printer;

    public EventSample() {
        this(0);
    }

    public EventSample(int event) {
        this.event = event;
        ServicePool.registerEventService(this);
    }

    public void handleEvent() {
        printer.print();
    }

    @Override
    public void postEvent() {
        System.out.println("this is event sample.");
    }
}
