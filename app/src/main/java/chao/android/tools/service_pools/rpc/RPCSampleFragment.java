package chao.android.tools.service_pools.rpc;

import android.view.View;

import java.util.ArrayList;

import chao.android.tools.service_pools.R;
import chao.android.tools.servicepool.rpc.SpRPC;
import chao.app.ami.annotations.LayoutID;
import chao.app.ami.base.AMISupportFragment;
import chao.app.remoteapi.IExampleService;
import chao.app.remoteapi.LocalFirstService;
import chao.app.remoteapi.RPCForceMainService;
import chao.java.tools.servicepool.ServicePoolException;

/**
 * @author luqin
 * @since 2020-07-27
 */
@LayoutID(R.layout.rpc_page)
public class RPCSampleFragment extends AMISupportFragment {
    @Override
    public void setupView(View layout) {

        findView(R.id.main_thread).setOnClickListener(v -> {
            try {
                call(SpRPC.getService(IExampleService.class));
            } catch (ServicePoolException e) {
                e.printStackTrace();
                System.out.println("");
            }
        });

        findView(R.id.main_thread_force).setOnClickListener(v -> call(SpRPC.getService(RPCForceMainService.class)));

        findView(R.id.other_thread).setOnClickListener(v -> {
            new Thread(() -> call(SpRPC.getService(IExampleService.class))).start();
        });

        findView(R.id.local_first).setOnClickListener(v -> {
            new Thread(() -> {
                LocalFirstService localFirstService = SpRPC.getService(LocalFirstService.class);
                localFirstService.function();
                System.out.println(localFirstService.getInt());
                System.out.println(localFirstService.getString());
            }).start();
        });
    }

    private void call(IExampleService exampleService) {
        System.out.println(exampleService.getString());
        System.out.println(exampleService.getInt());
        System.out.println(exampleService.withII(5, 8));
        System.out.println(exampleService.withInt(10));
        exampleService.withString("hi, caocao");
        exampleService.function();
        exampleService.withList(10, "ha ha", new ArrayList<>());

    }
}
