package chao.android.tools.service_pools;

import android.view.View;

import chao.android.tools.service_pools.cache.IPoolInstance;
import chao.app.ami.base.AmiSimpleFragment;
import chao.java.tools.servicepool.SP;

/**
 * @author luqin
 * @since 2020-07-02
 */
public class SimpleFragment extends AmiSimpleFragment {


    @Override
    public void onClick(View v) {
        for (int i = 0; i < 10; i++) {
            System.out.println(SP.getService(IPoolInstance.class));
        }

    }

}
