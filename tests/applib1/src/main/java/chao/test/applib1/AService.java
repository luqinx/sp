package chao.test.applib1;

import chao.app.pool.IA;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2020-01-09
 */
@Service
public class AService implements IA {

    @Override
    public String aName() {
        return "A Service";
    }
}
