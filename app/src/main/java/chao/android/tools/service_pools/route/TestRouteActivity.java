package chao.android.tools.service_pools.route;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.View;

import java.io.Serializable;

import butterknife.OnClick;
import chao.android.tools.service_pools.BaseActivity;
import chao.android.tools.service_pools.R;
import chao.android.tools.servicepool.AndroidServicePool;
import chao.app.ami.annotations.LayoutID;
import chao.java.tools.servicepool.ILogger;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2019-09-03
 */
@LayoutID(R.layout.test_route_activity)
@Service(path = "/app/testRoute")
public class TestRouteActivity extends BaseActivity {

    private int iv;

    private boolean bv;

    private float fv;

    private double dv;

    private String sv;

    private Serializable serializable;

    private Parcelable pv;

    @Service
    private ILogger logger;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        iv = intent.getIntExtra("int",0);
        bv = intent.getBooleanExtra("boolean", true);
        fv = intent.getFloatExtra("float", 1.1f);
        dv = intent.getDoubleExtra("double", 0.1);
        sv = intent.getStringExtra("string");
        serializable = intent.getSerializableExtra("serializable");
        pv = intent.getParcelableExtra("parcelable");

        logger.log(iv, bv, fv, dv, sv, serializable, pv);

    }

    @OnClick({R.id.not_found, R.id.interceptor_all_pass, R.id.interceptor_interrupt, R.id.interceptor_thr_e})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.not_found:
                testNotExistPath();
                break;
            case R.id.interceptor_all_pass:
                testInterceptor(0);
                break;
            case R.id.interceptor_interrupt:
                testInterceptor(1);
                break;
            case R.id.interceptor_thr_e:
                testInterceptor(2);
                break;
        }
    }

    private void testInterceptor(int interceptor) {
        AndroidServicePool.build("/app/routeTarget")
                .withContext(this)
                .withInt("interceptor", interceptor)
                .navigation(null);
    }

    private void testNotExistPath() {
        AndroidServicePool.build("/app/notExist")
                .withContext(this)
                .navigation(null);
    }

}
