package chao.android.tools.service_pools.route;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.OnClick;
import chao.android.tools.router.RouteBuilder;
import chao.android.tools.router.RouteNavigationCallback;
import chao.android.tools.router.SpRouter;
import chao.android.tools.service_pools.BaseActivity;
import chao.android.tools.service_pools.R;
import chao.android.tools.service_pools.SimpleFragment;
import chao.android.tools.service_pools.router2.RouteFragment;
import chao.app.ami.Ami;
import chao.app.ami.annotations.LayoutID;
import chao.java.tools.servicepool.ILogger;
import chao.java.tools.servicepool.IService;
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
    private static ILogger logger;

    public TestRouteActivity() {
        System.out.println("haha ");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        iv = intent.getIntExtra("int",-1);
        bv = intent.getBooleanExtra("boolean", true);
        fv = intent.getFloatExtra("float", -1);
        dv = intent.getDoubleExtra("double", -1);
        sv = intent.getStringExtra("string");
        serializable = intent.getSerializableExtra("serializable");
        pv = intent.getParcelableExtra("parcelable");

        int[] liv = intent.getIntArrayExtra("int[]");

        ArrayList<String> slist = intent.getStringArrayListExtra("slist");

        Integer I = intent.getIntExtra("Integer", -1);

        Type type = new TypeToken<ArrayList<RouteFragment.SimpleContainer>>(){}.getType();

        ArrayList<RouteFragment.SimpleContainer> containers  = SpRouter.getExtra(this, "simple", type);


        logger.log(iv, bv, fv, dv, sv, serializable, pv, Arrays.toString(liv), slist, I, containers);

        logger.log("" + SpRouter.getExtra(this,"int", int.class));
        logger.log("" + SpRouter.getExtra(this,"boolean", boolean.class));
        logger.log("" + SpRouter.getExtra(this,"float", float.class));
        logger.log("" + SpRouter.getExtra(this,"double", double.class));
        logger.log("" + SpRouter.getExtra(this,"string", String.class));
        logger.log("" + SpRouter.getExtra(this,"slist", new TypeToken<ArrayList<String>>(){}.getType()));
        logger.log("" + SpRouter.getExtra(this,"serializable", new TypeToken<ArrayList<String>>(){}.getType()));
        logger.log("" + SpRouter.getExtra(this,"parcelable", Parcelable.class));
        logger.log("" + SpRouter.getExtra(this,"int[]", int[].class));
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
        SpRouter.build("/app/routeTarget")
                .withContext(this)
                .withInt("interceptor", interceptor)
                .navigation(new RouteNavigationCallback() {
                    @Override
                    public void onLost(RouteBuilder route) {
                        Ami.log(Thread.currentThread().getName());
                    }

                    @Override
                    public void onFound(Class<? extends IService> service, RouteBuilder builder) {
                        Ami.log(Thread.currentThread().getName());
                    }

                    @Override
                    public void onInterrupt(RouteBuilder route, Throwable e) {
                        Ami.log(Thread.currentThread().getName());
                    }

                    @Override
                    public void onArrival(RouteBuilder route) {
                        Ami.log(Thread.currentThread().getName());
                    }
                });
    }

    private void testNotExistPath() {
        SpRouter.build("/app/notExist")
                .withContext(this)
                .navigation(null);
    }


}
