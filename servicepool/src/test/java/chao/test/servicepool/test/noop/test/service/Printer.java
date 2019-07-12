package chao.test.servicepool.test.noop.test.service;

import chao.java.tools.servicepool.IInitService;
import java.util.List;

/**
 * @author qinchao
 * @since 2019/4/30
 */
public class Printer implements IPrinter {

    private String mName;

    @Override
    public void setName(String name) {
        mName = name;
    }

    @Override
    public void printName() {
        System.out.println("I'm " + mName);
    }

    @Override
    public String tag() {
        return null;
    }

    @Override
    public void onInit() {
        try {
            Thread.sleep((long) (Math.random() * 3000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean async() {
        return false;
    }

    @Override
    public List<IInitService> dependencies() {
        return null;
    }
}
