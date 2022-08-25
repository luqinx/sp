//package chao.java.tools.servicepool;
//
//import net.bytebuddy.implementation.bind.annotation.AllArguments;
//import net.bytebuddy.implementation.bind.annotation.Origin;
//import net.bytebuddy.implementation.bind.annotation.RuntimeType;
//
//import java.lang.reflect.Method;
//
///**
// * @author qinchao
// * @since 2019/5/1
// */
//public class NoOpInterceptor {
//
//    @RuntimeType
//    public static Object noOp(@Origin Class clazz, @Origin Method method, @AllArguments Object[] args) {
//        if ("toString".equals(method.getName())) {
//            return String.format("NoOp Instance by NoOpInterceptor, Origin class is %s.", clazz.getName());
//        }
//        return ReflectUtil.getDefaultValue(method.getReturnType());
//    }
//
//
//}
