package chao.android.tools.servicepool.rpc;

import android.os.Build;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author luqin
 * @since 2020-07-23
 */
public class RemoteUtil {

    public static int methodHashCode(Method method) {
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
        for (Type type: parameterTypeNames) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                hashCode ^= type.getTypeName().hashCode();
            } else if (type instanceof Class) {
                hashCode ^= ((Class) type).getName().hashCode();
            }
        }
        return hashCode;
    }

}
