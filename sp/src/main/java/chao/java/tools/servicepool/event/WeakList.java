package chao.java.tools.servicepool.event;

import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author luqin
 * @since 2019-08-27
 */
public class WeakList<T> extends AbstractCollection<T> implements List<T> {

    private List<WeakReference<T>> weakPreferences;

    public WeakList() {
        weakPreferences = new ArrayList<>();
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private int index = 0;

            private T t;

            @Override
            public boolean hasNext() {
                int start = index;
                for (int i = start; i < weakPreferences.size(); i++) {
                    t = weakPreferences.get(i).get();
                    if (t == null) {
                        index ++;
                    } else {
                        break;
                    }
                }
                return t != null;
            }

            @Override
            public T next() {
                index++;
                T r = t;
                t = null;
                return r;
            }
        };
    }

    /**
     * 整理WeakReference列表
     */
    public void tidy() {
        List<WeakReference<T>> tidyList = new ArrayList<>();
        for (WeakReference<T> weak: weakPreferences) {
            if (weak.get() != null) {
                tidyList.add(weak);
            }
        }
        weakPreferences = tidyList;
    }

    @Override
    public boolean addAll(int i, Collection<? extends T> collection) {
        throw new UnsupportedOperationException();
    }


    @Override
    public T get(int i) {
        return weakPreferences.get(i).get();
    }

    @Override
    public T set(int i, T t) {
        return weakPreferences.set(i, new WeakReference<>(t)).get();
    }

    @Override
    public boolean add(T t) {
        add(size(), t);
        return true;
    }

    @Override
    public void add(int i, T t) {
        weakPreferences.add(i, new WeakReference<>(t));
    }

    public void addIfAbsent(T t) {
        if (!contains(t)){
            add(t);
        }
    }

    @Override
    public T remove(int i) {
        return weakPreferences.remove(i).get();
    }

    @Override
    public int indexOf(Object o) {
        if (o == null) {
            for (int i=0; i<weakPreferences.size(); i++) {
                if (weakPreferences.get(i).get() == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < weakPreferences.size(); i++) {
                if (o.equals(weakPreferences.get(i).get())) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<T> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<T> listIterator(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> subList(int i, int i1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return weakPreferences.size();
    }

}
