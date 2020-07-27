package chao.app.remoteexample.service;

import java.util.List;

import chao.java.tools.servicepool.IService;

/**
 * @author luqin
 * @since 2020-07-23
 */
public interface IExampleService extends IService {
    int getInt();

    String getString();

    void function();

    int withInt(int i);

    int withII(int i1, int i2);

    int withList(int i, String s, List<String> sl);

    void withString(String s);

}
