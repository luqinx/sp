package chao.android.tools.service_pools.router2;

import java.io.Serializable;
import java.util.ArrayList;

import chao.android.tools.router.annotation.Route;
import chao.android.tools.router.annotation.RouteParam;
import chao.android.tools.service_pools.SimpleFragment;
import chao.java.tools.servicepool.IService;

/**
 * @author luqin
 * @since 2020-07-28
 */
public interface RouteApi extends IService {

    @Route(path = "/app/testRoute")
    void startTestRouterActivity(
            @RouteParam("int") int iv,
            @RouteParam("int[]") int[] liv,
            @RouteParam("boolean") boolean bv,
            @RouteParam("float") float fValue,
            @RouteParam("double") double dValue,
            @RouteParam("Integer") Integer Iv,
            @RouteParam("string") String sValue,
            @RouteParam("serializable") Serializable s,
            @RouteParam("slist")ArrayList<String> slist,
            @RouteParam("simple") ArrayList<SimpleFragment.SimpleContainer> containers
            );
}
