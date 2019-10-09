package chao.android.tools.service_pools.route;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.io.Serializable;

import chao.android.tools.service_pools.R;
import chao.app.ami.annotations.LayoutID;
import chao.app.ami.base.AMIActivity;
import chao.java.tools.servicepool.ILogger;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2019-09-03
 */
@LayoutID(R.layout.main)
@Service(path = "/app/testRoute")
public class TestRouteActivity extends AMIActivity {

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
}
