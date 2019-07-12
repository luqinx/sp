package chao.android.tools.service_pools;

import android.util.Log;

import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin  qinchao@mochongsoft.com
 * @project: zmjx-sp
 * @description:
 * @date 2019-07-09
 */
@Service(value = "xxx", scope = IService.Scope.once)
public class TestPluginService {
    public void print(){
        Log.e("qinchao", "I'm a test service.");
    }
}
