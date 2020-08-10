package chao.android.tools.service_pools;

import android.view.View;

import java.util.HashMap;
import java.util.Map;

import chao.android.tools.service_pools.cache.IPoolInstance;
import chao.android.tools.service_pools.xxxxx.ASMServiceProxy;
import chao.app.ami.base.AmiSimpleFragment;
import chao.java.tools.servicepool.SP;

/**
 * @author luqin
 * @since 2020-07-02
 */
public class SimpleFragment extends AmiSimpleFragment {


    @Override
    public void onClick(View v) {
        Map<String, Class> map = new HashMap<>();
        map.put("111", ASMServiceProxy.class);
        System.out.println(map);

    }

}
