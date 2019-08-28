package chao.android.tools.service_pools.xxxxx;

import chao.android.tools.service_pools.Printer;
import chao.android.tools.service_pools.event.EventSample;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2019-08-28
 */
public class ASMStaticClass {

    @Service
    private static Printer sssssssprinter;

    private static EventSample eventSample = new EventSample();

    public ASMStaticClass() {

    }


    public void printer() {
        sssssssprinter.print();
    }
}

