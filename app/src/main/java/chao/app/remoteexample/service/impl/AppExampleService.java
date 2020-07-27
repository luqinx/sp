package chao.app.remoteexample.service.impl;

import java.util.List;

import chao.app.remoteexample.service.IExampleService;

/**
 * @author luqin
 * @since 2020-07-23
 */
public class AppExampleService implements IExampleService {
    @Override
    public int getInt() {
        return 0;
    }

    @Override
    public String getString() {
        return null;
    }

    @Override
    public void function() {

    }

    @Override
    public int withInt(int i) {
        return 0;
    }

    @Override
    public int withII(int i1, int i2) {
        return 0;
    }

    @Override
    public void withString(String s) {
        System.out.println("with String: " + s);
    }

    @Override
    public void withList(int i, String s, List<String> sl) {

    }
}
