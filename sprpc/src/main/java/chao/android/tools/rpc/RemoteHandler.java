package chao.android.tools.rpc;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * @author luqin
 * @since 2020-07-26
 */
public class RemoteHandler extends Handler {

    private static HandlerThread handlerThread = new HandlerThread("Remote-Client-Handler-Thread");

    static {
        handlerThread.start();
    }

    public RemoteHandler() {
        super(handlerThread.getLooper());
    }

    public RemoteHandler(Callback callback) {
        super(handlerThread.getLooper(), callback);
    }
}
