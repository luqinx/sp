package chao.test.servicepool.test.noop.test.service;

import chao.java.tools.servicepool.IInitService;

/**
 * @author qinchao
 * @since 2019/4/30
 */
public interface IPrinter extends IInitService {

    void setName(String name);

    void printName();
}
