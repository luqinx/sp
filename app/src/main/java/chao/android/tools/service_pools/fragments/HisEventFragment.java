package chao.android.tools.service_pools.fragments;

import android.view.View;

import chao.android.tools.service_pools.R;
import chao.android.tools.service_pools.event.HisEvent;
import chao.app.ami.annotations.LayoutID;
import chao.app.ami.base.AMISupportFragment;
import chao.java.tools.servicepool.annotation.Event;

/**
 * @author luqin
 * @since 2019-08-29
 */
@LayoutID(R.layout.main)
public class HisEventFragment extends AMISupportFragment {


    @Event
    private HisEvent hisEvent;

    @Override
    public void setupView(View layout) {
        hisEvent.postHisEvent();
    }
}
