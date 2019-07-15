package chao.java.tools.servicepool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author qinchao
 * @since 2019/4/30
 */
public class InitService extends DefaultService implements IInitService {

    private IInitService delegate;

    volatile AtomicInteger initState;

    private boolean forceSync; // for android

    List<IInitService> dependents;


    public InitService(IInitService service) {
        delegate = service;
        initState = new AtomicInteger(Constant.initState.UNINIT);
        dependents = new ArrayList<>();
    }


    @Override
    public String tag() {
        return null;
    }

    @Override
    public void onInit() {
        initState.set(Constant.initState.INITING);
        delegate.onInit();
    }

    @Override
    public boolean async() {
        if (forceSync) {
            return false;
        }
        return delegate.async();
    }

    void forceSync() {
        forceSync = true;
    }

    @Override
    public List<IInitService> dependencies() {
        List<IInitService> dependencies = delegate.dependencies();
        if (dependencies != null) {
            return Collections.unmodifiableList(dependencies);
        }
        return new ArrayList<>();
    }

    IInitService getDelegate() {
        return delegate;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        InitService wrapper = (InitService) object;

        return delegate != null ? delegate.equals(wrapper.delegate) : wrapper.delegate == null;
    }

    @Override
    public int hashCode() {
        return delegate != null ? delegate.hashCode() : 0;
    }
}
