package chao.android.tools.service_pools;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;

import chao.app.ami.base.AMIActivity;
import chao.java.tools.servicepool.IService;

/**
 * @author luqin
 * @since 2019-10-09
 */

@SuppressLint("Registered")
public class BaseActivity extends AMIActivity implements IService {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
