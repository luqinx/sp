package chao.android.tools.service_pools;

import android.view.View;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import chao.android.tools.servicepool.rpc.SpRPC;
import chao.app.ami.base.AmiSimpleFragment;
import chao.app.remoteapi.IExampleService;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2020-07-02
 */
public class SimpleFragment extends AmiSimpleFragment {

    @Service
    private Printer printer;

    @Override
    public void onClick(View v) {
//        printer.print();


//        ServicePool.getService(Printer.class).print();


//        try {
//            Method genericMethod = SimpleFragment.class.getMethod("getGenericList");
//            System.out.println("genericMethod: " + genericMethod.getGenericReturnType().getTypeName());
//            System.out.println("genericMethod: " + genericMethod.getReturnType().getName());
//
//
//            Method method = SimpleFragment.class.getMethod("getList");
//            System.out.println("genericMethod: " + method.getGenericReturnType().getTypeName());
//            System.out.println("genericMethod: " + method.getReturnType().getName());
//
//
//            Method m = AmiSimpleFragment.class.getDeclaredMethod("setupView", View.class);
//            System.out.println(m.getDeclaringClass());
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }

        IExampleService exampleService = SpRPC.getService(IExampleService.class);


        new Thread(() -> {
            System.out.println(exampleService.getString());
            System.out.println(exampleService.getInt());
            System.out.println(exampleService.withII(5, 8));
            System.out.println(exampleService.withInt(10));
            exampleService.withString("hi, caocao");
            exampleService.function();
            exampleService.withList(10, "ha ha", new ArrayList<>());

        }).start();


        try {
            Method m = IExampleService.class.getMethod("withII", int.class, int.class);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    public List<String> getGenericList() {
        return null;
    }

    public String getList() {
        return null;
    }
}
