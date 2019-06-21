package chao.java.tools.servicepool;

/**
 * @author qinchao
 * @since 2019/5/4
 *
 * @in
 */
public interface IServiceFactory {

    Iterable<Class<? extends IService>> createServices();

}
