package chao.android.tools.service_pools;

import android.view.View;

import chao.app.ami.base.AmiSimpleFragment;
import chao.java.tools.servicepool.ServicePool;

/**
 * @author luqin
 * @since 2020-07-02
 */
public class SimpleFragment extends AmiSimpleFragment {
    @Override
    public void onClick(View v) {

        try {
            Class<?> clazz = Class.forName("chao.android.tools.service_pools.NoOpA_1", false, getActivity().getClassLoader());
            System.out.println(clazz.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        A a = ServicePool.getService(A.class);
        System.out.println(a);

        try {
            Class<?> clazz = Class.forName("chao.android.tools.service_pools.NoOpA_1", false, getActivity().getClassLoader());
            System.out.println(clazz.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
