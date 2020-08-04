package chao.android.tools.servicepool.rpc;

import chao.java.tools.servicepool.IService;

/**
 * @author luqin
 * @since 2020-07-27
 */
public interface RemoteService extends IService {
    boolean remoteExist();
}
