package chao.java.tools.servicepool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;

import chao.java.tools.servicepool.debug.Debug;

/**
 * @author qinchao
 * @since 2019/5/3
 *
 * @see java.util.ServiceLoader
 */
public class ServiceLoader<T> implements Iterable<Class<? extends T>>{

    private static final String PREFIX = "META-INF/services/";

    private List<Class<? extends T>> services;

    public List<Class<? extends T>> getServices() {
        return services;
    }

    private Logger logger = new Logger();

    private ServiceLoader(Class<T> service, ClassLoader classLoader) {
        this.services = new ArrayList<>();
        try {
            long start = System.currentTimeMillis();
            Enumeration<URL> configs = classLoader.getResources(PREFIX + service.getName());
            long mid = System.currentTimeMillis();
            logger.log("classLoader.getResources spent:" + (mid - start));
            Debug.addError("classLoader.getResources spent:" + (mid - start));
            int configSize = 0;
            while (configs.hasMoreElements()) {
                configSize++;
                start = System.currentTimeMillis();
                List<String> names = parse(service, configs.nextElement());
                long end = System.currentTimeMillis();
                logger.log("parse spent:" + (end - start));
                Debug.addError("parse spent:" + (end - start));
                for (String name: names) {
                    try {
                        services.add(Class.forName(name, true, classLoader).asSubclass(service));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        logger.log("CNFE: " + e.getMessage());
                        Debug.addError("CNFE: " + e.getMessage());
                        Debug.addThrowable(e);
                    }
                }
            }
            logger.log("configSize = " + configSize);
            Debug.addError("configSize = " + configSize);

            if (configSize == 0) {
                logger.log(PREFIX + service.getName() + " has no configs.");
                Debug.addError(PREFIX + service.getName() + " has no configs.");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.log("IOE: " + e.getMessage());
            Debug.addError("IOE: " + e.getMessage());
            Debug.addThrowable(e);
        }
    }

    public static <T> ServiceLoader<T> load(Class<T> clazz) {
        return load(clazz, Thread.currentThread().getContextClassLoader());
    }

    public static <T> ServiceLoader<T> load(Class<T> clazz, ClassLoader classLoader) {
        return new ServiceLoader<>(clazz, classLoader);
    }

    private List<String> parse(Class<?> service, URL u)
        throws ServiceConfigurationError
    {
        InputStream in = null;
        BufferedReader r = null;
        ArrayList<String> names = new ArrayList<>();
        try {
            in = u.openStream();
            r = new BufferedReader(new InputStreamReader(in, "utf-8"));
            int lc = 1;
            while ((lc = parseLine(service, u, r, lc, names)) >= 0) {
                //do nothing
            }
        } catch (IOException x) {
            fail(service, "Error reading configuration file", x);
        } finally {
            try {
                if (r != null) r.close();
                if (in != null) in.close();
            } catch (IOException y) {
                fail(service, "Error closing configuration file", y);
            }
        }
        return names;
    }

    private int parseLine(Class<?> service, URL u, BufferedReader r, int lc,
                          List<String> names)
        throws IOException, ServiceConfigurationError
    {
        String ln = r.readLine();
        Debug.addError("parse line: " + ln);
        if (ln == null) {
            return -1;
        }
        int ci = ln.indexOf('#');
        if (ci >= 0) ln = ln.substring(0, ci);
        ln = ln.trim();
        int n = ln.length();
        if (n != 0) {
            if ((ln.indexOf(' ') >= 0) || (ln.indexOf('\t') >= 0))
                fail(service, u, lc, "Illegal configuration-file syntax");
            int cp = ln.codePointAt(0);
            if (!Character.isJavaIdentifierStart(cp))
                fail(service, u, lc, "Illegal provider-class name: " + ln);
            for (int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
                cp = ln.codePointAt(i);
                if (!Character.isJavaIdentifierPart(cp) && (cp != '.'))
                    fail(service, u, lc, "Illegal provider-class name: " + ln);
            }
            if (!names.contains(ln))
                names.add(ln);
        }
        return lc + 1;
    }

    private static void fail(Class<?> service, String msg, Throwable cause)
        throws ServiceConfigurationError
    {
        throw new ServiceConfigurationError(service.getName() + ": " + msg,
            cause);
    }

    private static void fail(Class<?> service, String msg)
        throws ServiceConfigurationError
    {
        Debug.addError("fail: " + service.getName() + ": " + msg);
        throw new ServiceConfigurationError(service.getName() + ": " + msg);
    }

    private static void fail(Class<?> service, URL u, int line, String msg)
        throws ServiceConfigurationError
    {
        fail(service, u + ":" + line + ": " + msg);
    }

    @Override
    public Iterator<Class<? extends T>> iterator() {
        return new Iterator<Class<? extends T>>() {

            Iterator<Class<? extends T>> iterator = services.iterator();

            @Override
            public boolean hasNext() {
                return iterator != null && iterator.hasNext();
            }

            @Override
            public Class<? extends T> next() {
//                String clazzName = "";
//                try {
//                    Class<? extends T> clazz = iterator.next();
//                    clazzName = clazz.getName();
//                    return clazz.newInstance();
//                } catch (Throwable e) {
//                    throw new ServicePoolException(e, "service instantiation failed: %s 实例化失败.", clazzName);
//                }
                return iterator.next();
            }
        };
    }
}
