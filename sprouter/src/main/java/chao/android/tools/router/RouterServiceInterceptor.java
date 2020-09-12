package chao.android.tools.router;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import chao.android.tools.router.annotation.Route;
import chao.android.tools.router.annotation.RouteAction;
import chao.android.tools.router.annotation.RouteData;
import chao.android.tools.router.annotation.RouteEnterAnim;
import chao.android.tools.router.annotation.RouteExitAnim;
import chao.android.tools.router.annotation.RouteFlags;
import chao.android.tools.router.annotation.RouteParam;
import chao.android.tools.router.annotation.RouteRequestCode;
import chao.android.tools.router.annotation.RouteType;
import chao.android.tools.servicepool.Spa;
import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.IServiceInterceptor;
import chao.java.tools.servicepool.IServiceInterceptorCallback;
import chao.java.tools.servicepool.ServicePool;
import chao.java.tools.servicepool.annotation.Service;

import static chao.android.tools.router.SpRouter.TAG;
import static chao.android.tools.router.SpRouter.gson;

/**
 * @author luqin
 * @since 2020-07-28
 */
@Service(scope = ServicePool.SCOPE_GLOBAL)
public class RouterServiceInterceptor implements IServiceInterceptor {

    @Override
    public void intercept(Class<? extends IService> originClass, IService source, Method method, Object[] args, IServiceInterceptorCallback callback) {
        if (!(source instanceof RouterService)) {
            callback.onContinue(method, args);
            return;
        }
        Route routeConfig = method.getAnnotation(Route.class);
        String action = null;
        Uri dataUri = null;
        String type = null;
        int flags = 0;
        int enterAnim = 0;
        int exitAnim = 0;
        int requestCode = -1;

        Context context = null;

        RouteNavigationCallback navigationCallback = null;

        String path = null;
        if (routeConfig != null) {
            action = routeConfig.action();
            dataUri = Uri.parse(routeConfig.dataUri());
            type = routeConfig.type();
            flags = routeConfig.flags();
            path = routeConfig.path();
            requestCode = routeConfig.requestCode();
        }
        if (TextUtils.isEmpty(path)) {
            throw new RouteException("path should not be null.");
        }

        RouteBuilder routeBuilder = new RouteBuilder(path);

        Bundle bundle = null;


        Class<?>[] paramTypes = method.getParameterTypes();

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        for (int i = 0; i < parameterAnnotations.length; i++) {

            Object arg = args[i];
            if (arg == null) {
                continue;
            }

            Class<?> argType = paramTypes[i];
            if (Bundle.class.isAssignableFrom(argType)) {
                bundle = (Bundle) arg;
                continue;
            }

            if (RouteNavigationCallback.class.isAssignableFrom(arg.getClass())) {
                navigationCallback = (RouteNavigationCallback) arg;
                continue;
            }

            if (Context.class.isAssignableFrom(arg.getClass())) {
                context = (Context) arg;
                continue;
            }

            Annotation[] typeAnnotations = parameterAnnotations[i];
            if (typeAnnotations.length == 0) {
                continue;
            }
            Annotation annotation = typeAnnotations[0];



            if (annotation instanceof RouteAction) {
                if (arg instanceof String) {
                    action = String.valueOf(arg);
                } else {
                    Spa.logger.e(TAG, "error type of the parameter @RouteAction, except String, but " + arg.getClass());
                }
                continue;
            }

            if (annotation instanceof RouteData) {
                if (arg instanceof String) {
                    dataUri = Uri.parse(String.valueOf(arg));
                } else if (arg instanceof Uri) {
                    dataUri = (Uri) arg;
                } else {
                    Spa.logger.e(TAG, "error type of the parameter @RouteData, except String or Uri, but " + arg.getClass());
                }
                continue;
            }

            if (annotation instanceof RouteType) {
                if (arg instanceof String) {
                    type = String.valueOf(type);
                } else {
                    Spa.logger.e(TAG, "error type of the parameter @RouteType, except String, but " + arg.getClass());
                }
                continue;
            }

            if (annotation instanceof RouteFlags) {
                if (arg.getClass() == int.class) {
                    flags |= (int) arg;
                } else if (Integer.class.isAssignableFrom(arg.getClass())) {
                    flags |= (Integer) arg;
                } else {
                    Spa.logger.e(TAG, "error type of the parameter @RouteFlags, except int, but " + arg.getClass());
                }
                continue;
            }

            if (annotation instanceof RouteParam) {
                RouteParam paramConfig = (RouteParam) annotation;
                String paramName = paramConfig.value();
                // 基本类型
                if (int.class.isAssignableFrom(argType)) {
                    routeBuilder.withInt(paramName, (int) arg);
                } else if (short.class.isAssignableFrom(argType)) {
                    routeBuilder.withShort(paramName, (short) arg);
                } else if (byte.class.isAssignableFrom(argType)) {
                    routeBuilder.withByte(paramName, (byte) arg);
                } else if (long.class.isAssignableFrom(argType)) {
                    routeBuilder.withLong(paramName, (long) arg);
                } else if (boolean.class.isAssignableFrom(argType)) {
                    routeBuilder.withBoolean(paramName, (boolean) arg);
                } else if (float.class.isAssignableFrom(argType)) {
                    routeBuilder.withFloat(paramName, (float) arg);
                } else if (double.class.isAssignableFrom(argType)) {
                    routeBuilder.withDouble(paramName, (double) arg);
                } else if (char.class.isAssignableFrom(argType)) {
                    routeBuilder.withChar(paramName, (char) arg);
                }
                // 基本类型数组
                else if (argType.isArray()) {
                    Class<?> cType = argType.getComponentType();
                    assert cType != null;
                    if (int.class.isAssignableFrom(cType)) {
                        routeBuilder.withIntArray(paramName, (int[]) arg);
                    } else if (float.class.isAssignableFrom(cType)) {
                        routeBuilder.withFloatArray(paramName, (float[]) arg);
                    } else if (double.class.isAssignableFrom(cType)) {
                        routeBuilder.withDoubleArray(paramName, (double[]) arg);
                    } else if (short.class.isAssignableFrom(cType)) {
                        routeBuilder.withShortArray(paramName, (short[]) arg);
                    } else if (byte.class.isAssignableFrom(cType)) {
                        routeBuilder.withByteArray(paramName, (byte[]) arg);
                    } else if (long.class.isAssignableFrom(cType)) {
                        routeBuilder.withLongArray(paramName, (long[]) arg);
                    } else if (boolean.class.isAssignableFrom(cType)) {
                        routeBuilder.withBooleanArray(paramName, (boolean[]) arg);
                    } else if (String.class.isAssignableFrom(cType)) {
                        routeBuilder.withStringArray(paramName, (String[]) arg);
                    } else if (CharSequence.class.isAssignableFrom(cType)) {
                        routeBuilder.withCharSequenceArray(paramName, (CharSequence[]) arg);
                    } else if (Parcelable.class.isAssignableFrom(cType)) {
                        routeBuilder.withParcelableArray(paramName, (Parcelable[]) arg);
                    } else if (Serializable.class.isAssignableFrom(cType)) {
                        routeBuilder.withSerializable(paramName, (Serializable) arg);
                    }
                } else if (String.class.isAssignableFrom(argType)) {
                    routeBuilder.withString(paramName, (String) arg);
                } else if (CharSequence.class.isAssignableFrom(argType)) {
                    routeBuilder.withCharSequence(paramName, (CharSequence) arg);
                } else if (ArrayList.class.isAssignableFrom(argType)) {
                    List list = (List) arg;
                    if (list.size() > 0) {
                        Object object = list.get(0);
                        if (object == null || (object instanceof Serializable)) {
                            //list存在
                            routeBuilder.withSerializable(paramName, (Serializable) arg);
                        }
                    } else {
                        //
                        routeBuilder.withSerializable(paramName, (Serializable) arg);
                    }
                }
                // Serializable 包含基本类型包装类和基本类型包装类的数组, 包含ArrayList等可序列化列表
                else if (Serializable.class.isAssignableFrom(argType)) {
                    routeBuilder.withSerializable(paramName, (Serializable) arg);
                } else if (Parcelable.class.isAssignableFrom(argType)) {
                    routeBuilder.withParcelable(paramName, (Parcelable) arg);
                }
                String gsonKey = SpRouter.ROUTER_KEY_PREFIX + paramName;
                routeBuilder.withString(gsonKey, gson.toJson(arg));

            }

            RouteEnterAnim enterAnimConfig = argType.getAnnotation(RouteEnterAnim.class);
            if (enterAnimConfig != null) {
                if (arg.getClass() == int.class) {
                    enterAnim = (int) arg;
                } else if (Integer.class.isAssignableFrom(arg.getClass())){
                    enterAnim = (Integer) arg;
                } else {
                    Spa.logger.e(TAG, "error type of the parameter @RouteEnterAnim, except int, but " + arg.getClass());
                }
                continue;
            }

            RouteExitAnim exitAnimConfig = argType.getAnnotation(RouteExitAnim.class);
            if (exitAnimConfig != null) {
                if (arg.getClass() == int.class) {
                    exitAnim = (int) arg;
                } else if (Integer.class.isAssignableFrom(arg.getClass())){
                    exitAnim = (Integer) arg;
                } else {
                    Spa.logger.e(TAG, "error type of the parameter @RouteExitAnim, except int, but " + arg.getClass());
                }
                continue;
            }
            RouteRequestCode requestCodeConfig = argType.getAnnotation(RouteRequestCode.class);
            if(requestCodeConfig != null) {
                if (arg.getClass() == int.class) {
                    requestCode = (int) arg;
                } else if (Integer.class.isAssignableFrom(arg.getClass())) {
                    requestCode = (Integer) arg;
                } else {
                    Spa.logger.e(TAG, "error type of the parameter @RouteRequestCode, except int, but " + arg.getClass());
                }
            }

        }

        routeBuilder
                .withContext(context)
                .withAll(bundle)
                .withFlag(flags)
                .withType(type)
                .withData(dataUri)
                .withAction(action)
                .withTransition(enterAnim, exitAnim)
                .navigation(requestCode, navigationCallback);

    }
}
