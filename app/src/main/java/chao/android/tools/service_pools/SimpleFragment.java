package chao.android.tools.service_pools;

import android.view.View;

import chao.app.ami.base.AmiSimpleFragment;
import chao.java.tools.servicepool.ServicePool;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2020-07-02
 */
public class SimpleFragment extends AmiSimpleFragment {

    @Service
    private Printer printer;

    @Override
    public void onClick(View v) {
        printer.print();

        ServicePool.getService(Printer.class).print();
    }
}
