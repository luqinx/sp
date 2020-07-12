package chao.java.tools.servicepool;

/**
 *
 * 实现类由插件AutoService生成
 *
 * chao/java/tools/servicepool/gen/InitServiceManagerInstance
 *
 *
 * InitService默认使用懒加载，但有的场景需要启动时直接加载
 *
 * 这里加载的是非懒加载的InitService (lazy=false)
 *
 * @author luqin
 * @since 2019-09-26
 */
public interface InitServiceManager extends IService {

    void initService();

    void addInitService(Class<? extends IInitService> initService);
}
