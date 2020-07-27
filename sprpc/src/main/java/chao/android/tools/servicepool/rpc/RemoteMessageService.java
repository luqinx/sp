package chao.android.tools.servicepool.rpc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author luqin
 * @since 2020-07-22
 */
public class RemoteMessageService extends Service implements RemoteServer.OnServerListener {

    private RemoteServer remoteServer;

    @Override
    public IBinder onBind(Intent intent) {
        if (remoteServer == null) {
            remoteServer = new RemoteServer();
            remoteServer.setOnServerListener(this);
        }

        return remoteServer.getReceiveMessage().getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onServerDisconnected() {
        stopSelf();
    }

    @Override
    public void onHandleException(Throwable e) {
        e.printStackTrace();
    }
}
