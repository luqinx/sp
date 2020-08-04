package chao.android.tools.service_pools;

import android.view.View;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import chao.android.tools.router.SpRouter;
import chao.android.tools.service_pools.router2.RouteApi;
import chao.android.tools.service_pools.rpc.TestService;
import chao.android.tools.servicepool.rpc.RemoteCallbackHandler;
import chao.app.ami.base.AmiSimpleFragment;
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



//        int i = 5;
//        Object oi = i;
//        Integer I = (Integer) oi;
//
//        int[] ints = new int[2];
//        for (i = 0; i < ints.length; i++) {
//            ints[i] = i + 1;
//        }
//        Object io = ints;
//        System.out.println(Arrays.toString((Integer[]) io));
//
//
//        Bundle bundle = new Bundle();
//        bundle.putInt("I", I);
//        bundle.putSerializable("i", i);
//
//        Integer[] integers = new Integer[2];
//        for (i = 0; i < integers.length; i++ ) {
//            integers[i] = i + 1;
//        }
//        Object o = integers;
//        System.out.println(Arrays.toString((Integer[]) o));


//        ArrayList<String> slist = new ArrayList<>();
//        slist.add("a");
//        slist.add("b");
//        slist.add("c");
//
//        Integer I = 22;
//
//        ArrayList<SimpleContainer> containers = new ArrayList<>();
//        SimpleData data = new SimpleData();
//        data.setI(1);
//        data.setS("1s");
//        SimpleContainer container = new SimpleContainer();
//        container.setData(data);
//        containers.add(container);
//
//        data = new SimpleData();
//        data.setI(2);
//        data.setS("2s");
//        container = new SimpleContainer();
//        container.setData(data);
//        containers.add(container);
//
//        SpRouter.getService(RouteApi.class).startTestRouterActivity(
//                1,
//                new int[]{3, 5},
//                true,
//                2.0f,
//                3,
//                I,
//                "hi cao",
//                slist,
//                slist,
//                containers
//                );
    }

    public class SimpleData {
        private int i;
        private String s;

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return "SimpleData{" +
                    "i=" + i +
                    ", s='" + s + '\'' +
                    '}';
        }
    }

    public class SimpleContainer {
        private SimpleData data;

        public SimpleData getData() {
            return data;
        }

        public void setData(SimpleData data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "SimpleContainer{" +
                    "data=" + data +
                    '}';
        }
    }

}
