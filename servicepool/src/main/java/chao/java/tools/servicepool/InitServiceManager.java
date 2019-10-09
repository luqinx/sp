package chao.java.tools.servicepool;

/**
 *
 * InitService默认使用懒加载，但有的场景需要启动时直接加载
 *
 * 这里加载的是非懒加载的InitService (lazy=false)
 *
 * @author luqin
 * @since 2019-09-26
 */
public interface InitServiceManager {

    void initService();

    void addInitService(Class<? extends IInitService> initService);
}
