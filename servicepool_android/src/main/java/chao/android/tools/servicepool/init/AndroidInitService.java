package chao.android.tools.servicepool.init;

import android.content.Context;

import chao.android.tools.servicepool.Spa;
import chao.java.tools.servicepool.IInitService;

/**
 * @author luqin
 * @since 2020/8/21
 */
public abstract class AndroidInitService implements IInitService {

    @Override
    public void onInit() {
        onInit(Spa.getContext());
    }

    protected abstract void onInit(Context applicationContext);
}
