package chao.android.tools.service_pools.inherited;

import android.os.Bundle;
import android.os.PersistableBundle;

import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2020-07-13
 */
@Service(priority = IService.Priority.MAX_PRIORITY)
public class InheritedInheritedActivity11 extends InheritedInheritedActivity1 {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }
}
