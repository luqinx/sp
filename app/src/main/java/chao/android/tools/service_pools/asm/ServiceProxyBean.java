package chao.android.tools.service_pools.asm;

import java.util.List;

import chao.java.tools.servicepool.IInitService;

/**
 * @author luqin
 * @since 2019-08-28
 */
public class ServiceProxyBean {

    public List<Class<? extends IInitService>> services;
}
