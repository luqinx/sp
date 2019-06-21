package chao.java.tools.servicepool;

import java.util.List;

public interface IInitService extends IService{

    void onInit();

    boolean async();

    List<IInitService> dependencies();
}
