package chao.android.tools.servicepool.rpc;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.lang.reflect.Method;

import chao.java.tools.servicepool.ClassTypeAdapter;
import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.ServicePool;

/**
 * @author luqin
 * @since 2020-07-23
 */
public class RemoteServer implements Handler.Callback {

    private Handler mHandler = new Handler(Looper.getMainLooper(), this);


    private Messenger receiveMessage = new Messenger(mHandler);

    private OnServerListener onServerListener;

    private Gson gson;

    public RemoteServer() {
        gson = new GsonBuilder()
                .registerTypeAdapter(Class.class, new ClassTypeAdapter())
//                .registerTypeAdapter(Class.class, new ClassArrayTypeAdapter())
                .create();
    }

    @Override
    public boolean handleMessage(Message msg) {
        int callId = msg.what;
        Bundle bundle = msg.getData();
        int methodHash = bundle.getInt(RemoteClient.REMOTE_KEY_METHOD_HASH);
        String originClassName = bundle.getString(RemoteClient.REMOTE_KEY_ORIGIN_CLASS);
        String methodName = bundle.getString(RemoteClient.REMOTE_KEY_METHOD_NAME);
        String methodArgsJson = bundle.getString(RemoteClient.REMOTE_KEY_ARGS);
        String methodArgTypesJson = bundle.getString(RemoteClient.REMOTE_KEY_ARG_TYPES);
        String methodReturnType = bundle.getString(RemoteClient.REMOTE_KEY_RETURN);

        try {
            if (TextUtils.isEmpty(originClassName)) {
                throw new RemoteServiceException("origin class name is empty.");
            }

            if (TextUtils.isEmpty(methodName)) {
                throw new RemoteServiceException("origin method name is empty.");
            }
            assert originClassName != null;
            assert methodName != null;

            Class<? extends IService> originClass = (Class<? extends IService>) Class.forName(originClassName);


            JsonArray argTypesArray = new JsonParser().parse(methodArgTypesJson).getAsJsonArray();

            Class[] types = gson.fromJson(argTypesArray, Class[].class);

            Method m = originClass.getMethod(methodName, types);

            if (RemoteUtil.methodHashCode(m) != methodHash) {
                replay(msg, callId, methodHash, Throwable.class.getName(), new RemoteException("method hash not matches"));
                return true;
            }

            JsonElement argsElement = new JsonParser().parse(methodArgsJson);

            JsonArray argArray;
            if (argsElement instanceof JsonArray) {
                argArray = argsElement.getAsJsonArray();
            } else {
                argArray = new JsonArray();
            }
            int i = 0;
            Object[] args = new Object[argArray.size()];
            for (JsonElement argElement: argArray) {
                args[i] = gson.fromJson(argElement, types[i]);
                i = i + 1;
            }

            Object service = ServicePool.getService(originClass);
            if (service == null) {
                return false;
            }
            Object returnObject = m.invoke(service, args);

            Class returnType = m.getReturnType();

            replay(msg, callId, methodHash, returnType.getName(), returnObject);

        } catch (RemoteException e) {
            if (onServerListener != null) {
                onServerListener.onServerDisconnected();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            try {
                replay(msg, callId, methodHash, Throwable.class.getName(), e);
            } catch (RemoteException e1) {
                if (onServerListener != null) {
                    onServerListener.onServerDisconnected();
                }
            }
            if (onServerListener != null) {
                onServerListener.onHandleException(e);
            }
        }
        return false;
    }

    private void replay(Message msg, int callId, int methodHash, String returnType, Object returnObject) throws RemoteException {
        Message sendMessage = Message.obtain(mHandler, callId);
        Bundle sendData = new Bundle();
        sendData.putString(RemoteClient.REMOTE_KEY_RETURN, gson.toJson(returnObject));
        sendData.putInt(RemoteClient.REMOTE_KEY_METHOD_HASH, methodHash);
        sendData.putString(RemoteClient.REMOTE_KEY_RETURN_TYPE, returnType);
        sendMessage.setData(sendData);
        msg.replyTo.send(sendMessage);
    }

    public void setOnServerListener(OnServerListener onServerListener) {
        this.onServerListener = onServerListener;
    }

    interface OnServerListener {
        void onServerDisconnected();

        void onHandleException(Throwable e);
    }

    public Messenger getReceiveMessage() {
        return receiveMessage;
    }
}
