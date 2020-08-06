package chao.android.tools.service_pools;

import android.os.Bundle;

import chao.android.tools.service_pools.rpc.RPCSampleFragment;
import chao.app.ami.UI;
import chao.app.ami.base.AMIActivity;

/**
 * @author luqin
 * @since 2020-07-02
 */
public class LauncherActivity extends AMIActivity {


    @Override
    public void setupView(Bundle savedInstanceState) {
        super.setupView(savedInstanceState);

        UI.show(this, SimpleFragment.class);
        finish();
    }
}
