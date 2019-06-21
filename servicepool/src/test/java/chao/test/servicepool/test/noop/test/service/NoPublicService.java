package chao.test.servicepool.test.noop.test.service;

import chao.java.tools.servicepool.IService;

/**
 * @author qinchao
 * @since 2019/5/5
 */
class NoPublicService implements IService {

//    public NoPublicService() {}

    @Override
    public String getTag() {
        return null;
    }
}
