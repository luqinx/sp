package chao.android.tools.service_pools.init;

import android.view.View;

import java.util.concurrent.CountDownLatch;

import butterknife.OnClick;
import chao.android.tools.service_pools.R;
import chao.android.tools.service_pools.test.InitService1;
import chao.app.ami.Ami;
import chao.app.ami.annotations.LayoutID;
import chao.app.ami.base.AMISupportFragment;
import chao.java.tools.servicepool.ServicePool;

/**
 * @author luqin
 * @since 2019-10-24
 */
@LayoutID(R.layout.init_sync_sample)
public class InitSyncSampleFragment extends AMISupportFragment {


    @Override
    public void setupView(View layout) {

        findView(R.id.init_sync_btn).setOnClickListener(v -> {

            int count = 10;

            CountDownLatch countDownLatch = new CountDownLatch(count);

            Thread[] threads = new Thread[count];
            for (int i = 0; i < count; i++) {
                threads[i] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        InitService1 initService1 = ServicePool.getService(InitService1.class);
                        Ami.log(initService1);
                        countDownLatch.countDown();
                    }
                }, "thread" + i);
            }

            threads[count / 2].setPriority(Thread.MAX_PRIORITY);
            for (int i = 0; i < count; i++) {
                threads[i].start();
            }

            try {
                countDownLatch.await();
                System.out.println("wait done.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ServicePool.recycleService(InitService1.class);
        });


    }

//    @OnClick(R.id.init_sync_btn)
//    public void onViewClicked(View v) {
//        for (int i=0;i<10;i++) {
//            new Thread(() -> {
//                InitService1 initService1 = ServicePool.getService(InitService1.class);
//                System.out.println(initService1);
//            }, "thread-" + (i+1)).start();
//        }
//    }
}
