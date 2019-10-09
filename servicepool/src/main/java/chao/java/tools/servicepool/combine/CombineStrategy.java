package chao.java.tools.servicepool.combine;

import java.lang.reflect.Method;
import java.util.List;

import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.ServiceProxy;

/**
 * @author luqin
 * @since 2019-10-08
 */
public interface CombineStrategy extends IService {
    /**
     *
     * 过滤器
     *
     * 可以通过过滤器过滤当前策略对哪些service生效
     *
     * @param serviceClass
     * @param method
     * @param args
     */
    boolean filter(Class serviceClass, Method method, Object[] args);

    /**
     * CombineService 服务组执行方式的策略
     *
     * @param proxies   service组的代理
     * @param serviceClass  service类
     * @param method    将执行的方法
     * @param args      执行方法的参数
     *
     * @return 处理完成返回true, 否则返回false
     */
    boolean invoke(List<ServiceProxy> proxies, Class serviceClass, Method method, Object[] args);
}
