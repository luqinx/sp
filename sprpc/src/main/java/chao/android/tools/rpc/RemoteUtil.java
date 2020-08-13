package chao.android.tools.rpc;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import chao.android.tools.servicepool.Spa;


/**
 * @author luqin
 * @since 2020-07-23
 */
public class RemoteUtil {

    public static int checkAndHashMethod(Method method) {
        String returnTypeName;
        Type[] parameterTypeNames;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            returnTypeName = method.getGenericReturnType().getTypeName();
            parameterTypeNames = method.getGenericParameterTypes();
        } else {
            returnTypeName = method.getReturnType().getName();
            parameterTypeNames = method.getParameterTypes();
        }
        String methodName = method.getName();

        int hashCode = methodName.hashCode() ^ returnTypeName.hashCode();

        int callbackHandlerCount = 0;
        for (Type type: parameterTypeNames) {
            if (type == RemoteCallbackHandler.class) {
                callbackHandlerCount++;
                continue;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                hashCode ^= type.getTypeName().hashCode();
            } else if (type instanceof Class) {
                hashCode ^= ((Class) type).getName().hashCode();
            }
        }
        if (callbackHandlerCount > 1) {
            throw new RemoteServiceException("more than one parameter of RemoteCallbackHandler.");
        }
        if (callbackHandlerCount == 1 && parameterTypeNames[parameterTypeNames.length - 1] != RemoteCallbackHandler.class) {
            throw new RemoteServiceException("RemoteCallbackHandler should be the last parameter.");
        }
        return hashCode;
    }

    public static boolean inMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static boolean remoteExist(String packageName, String componentName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        if (packageName.equals(Spa.getContext().getPackageName())) {
            return false;
        }
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, componentName));
        PackageManager pm = Spa.getContext().getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentServices(intent, 0);
        if (resolveInfos.size() > 0) {
            return true;
        }
        return false;
    }
}
