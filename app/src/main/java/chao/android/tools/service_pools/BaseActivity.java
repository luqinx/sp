package chao.android.tools.service_pools;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import chao.app.ami.base.AMIActivity;
import chao.java.tools.servicepool.IService;

/**
 * @author luqin
 * @since 2019-10-09
 */

@SuppressLint("Registered")
public class BaseActivity extends AMIActivity implements IService {

    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unbinder = ButterKnife.bind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
