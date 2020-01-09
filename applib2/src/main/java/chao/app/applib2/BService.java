package chao.app.applib2;

import chao.app.pool.IB;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2020-01-09
 */
@Service
public class BService implements IB {

    @Override
    public String bName() {
        return "B Service";
    }
}
