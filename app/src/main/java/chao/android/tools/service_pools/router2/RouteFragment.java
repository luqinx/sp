package chao.android.tools.service_pools.router2;

import android.view.View;

import java.util.ArrayList;

import chao.android.tools.router.SpRouter;
import chao.android.tools.service_pools.SimpleFragment;
import chao.app.ami.base.AmiSimpleFragment;

/**
 * @author luqin
 * @since 2020-08-06
 */
public class RouteFragment extends AmiSimpleFragment {
    @Override
    public void onClick(View v) {
        ArrayList<String> slist = new ArrayList<>();
        slist.add("a");
        slist.add("b");
        slist.add("c");

        Integer I = 22;

        ArrayList<SimpleContainer> containers = new ArrayList<>();
        SimpleData data = new SimpleData();
        data.setI(1);
        data.setS("1s");
        SimpleContainer container = new SimpleContainer();
        container.setData(data);
        containers.add(container);

        data = new SimpleData();
        data.setI(2);
        data.setS("2s");
        container = new SimpleContainer();
        container.setData(data);
        containers.add(container);

        SpRouter.getService(RouteApi.class).startTestRouterActivity(
                1,
                new int[]{3, 5},
                true,
                2.0f,
                3,
                I,
                "hi cao",
                slist,
                slist,
                containers
        );
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
