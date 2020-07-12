package chao.test.servicepool.test.noop.test.service;

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
    public void onInit() {
        try {
            Thread.sleep((long) (Math.random() * 3000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
