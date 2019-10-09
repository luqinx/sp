package chao.android.tools.service_pools.init;

import android.os.Bundle;
import android.support.annotation.Nullable;

import chao.android.tools.service_pools.R;
import chao.android.tools.service_pools.test.InitService5;
import chao.app.ami.annotations.LayoutID;
import chao.app.ami.base.AMIActivity;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2019-09-30
 */
@LayoutID(R.layout.ami_home_layout)
public class InitSampleActivity extends AMIActivity {

    @Service
    private InitService5 initService5;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
