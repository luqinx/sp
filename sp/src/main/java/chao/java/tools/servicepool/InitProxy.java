package chao.java.tools.servicepool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import chao.java.tools.servicepool.annotation.Service;

/**
 * @author qinchao
 * @since 2019/4/30
 */
public class InitProxy extends DefaultService implements IInitService {

    private IInitService delegate;

    public volatile AtomicInteger initState;

    private boolean forceSync; // for android

    private List<InitProxy> dependents;

    private List<Class<? extends IInitService>> dependencies;

    private boolean async;

    @Service
    private ILogger logger;


    public InitProxy(IInitService service, List<Class<? extends IInitService>> dependencies, boolean async) {
        delegate = service;
        initState = new AtomicInteger(Constant.initState.UNINIT);
        dependents = new ArrayList<>();
        this.dependencies = dependencies;
        this.async = async;
    }


    @Override
    public String path() {
        return null;
    }

    @Override
    public void onInit() {
        if (!initState.compareAndSet(Constant.initState.TRYING, Constant.initState.INITING)) {
            logger.log("init state: trying -> initing failed!!!");
        }
        try {
            long timeMillis = System.currentTimeMillis();
            delegate.onInit();
            logger.log(delegate.getClass().getSimpleName() + " init spent: " + (System.currentTimeMillis() - timeMillis));
        } catch (Throwable e) {
            e.printStackTrace();
            initState.getAndSet(Constant.initState.FAILED);
        }
        if (!initState.compareAndSet(Constant.initState.INITING, Constant.initState.INITED)) {
            logger.log("init state: initing -> inited failed!!!");
        }
    }

    public boolean async() {
        if (forceSync) {
            return false;
        }
        return async;
    }

    void forceSync() {
        forceSync = true;
    }

    public List<Class<? extends IInitService>> dependencies() {
        if (dependencies != null) {
            return Collections.unmodifiableList(dependencies);
        }
        return new ArrayList<>();
    }

    public IInitService getDelegate() {
        return delegate;
    }

    public List<InitProxy> getDependents() {
        return dependents;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        InitProxy wrapper = (InitProxy) object;

        return delegate != null ? delegate.equals(wrapper.delegate) : wrapper.delegate == null;
    }

    @Override
    public int hashCode() {
        return delegate != null ? delegate.hashCode() : 0;
    }
}
