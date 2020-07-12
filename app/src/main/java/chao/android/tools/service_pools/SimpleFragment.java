package chao.android.tools.service_pools;

import android.util.TimeUtils;
import android.view.View;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

import chao.app.ami.Ami;
import chao.app.ami.base.AmiSimpleFragment;
import chao.java.tools.servicepool.ServicePool;

/**
 * @author luqin
 * @since 2020-07-02
 */
public class SimpleFragment extends AmiSimpleFragment {
    @Override
    public void onClick(View v) {

        IA a = ServicePool.getService(IA.class);
        try {
            System.out.println("" + a.getInt());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println(""+ a);


        Stopwatch stopwatch = Stopwatch.createStarted();

        IA a1 = ServicePool.getService(IA.class);
        int ai1 = a1.getInt();
        System.out.println(ai1);
        stopwatch.stop();

        long time1 = stopwatch.elapsed(TimeUnit.NANOSECONDS);

        stopwatch.reset();

        stopwatch.start();
        IA a2 = new A();
        int ai2 = a2.getInt();
        System.out.println(ai2);
        stopwatch.stop();

        long time2 = stopwatch.elapsed(TimeUnit.NANOSECONDS);


        Ami.log(time1 + ": " + time2);

    }
}
