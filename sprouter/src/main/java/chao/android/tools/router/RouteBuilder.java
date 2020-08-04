package chao.android.tools.router;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;

import chao.java.tools.servicepool.IService;
import chao.java.tools.servicepool.ServicePool;
import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2019-07-25
 */
public class RouteBuilder {

    private static final int ROUTE_DEFAULT_REQUEST_CODE = 0xf000;

    private RouteManager routeManager;

    Context context;

    public String action;

    public final String path;

    public Bundle extras;

    public Uri uri;

    public String type;

    public int flags;

    public long interceptorTimeout = 100L; // 拦截超时, 单位: 秒

    int enterAnim = -1;

    int exitAnim = -1;

    int requestCode = ROUTE_DEFAULT_REQUEST_CODE;


    public RouteBuilder(String path) {
        this.extras = new Bundle();
        this.path = path;
        if (TextUtils.isEmpty(this.path)) {
            throw new IllegalArgumentException("route path should not be empty.");
        }
        routeManager = ServicePool.getService(RouteManager.class);
    }

    public void navigation() {
        navigation(null);
    }
    
    public void navigation(RouteNavigationCallback callback) {
        routeManager.navigation(this, callback);
    }

    public void navigation(int requestCode, RouteNavigationCallback callback) {
        if (requestCode == ROUTE_DEFAULT_REQUEST_CODE) {
            throw new IllegalArgumentException(ROUTE_DEFAULT_REQUEST_CODE + " is route inner request code.");
        }
        this.requestCode = requestCode;
        routeManager.navigation(this, callback);
    }


    
    public RouteBuilder with(Bundle bundle) {
        if (bundle != null) {
            extras = bundle;
        }
        return this;
    }

    public RouteBuilder withAll(Bundle bundle) {
        if (bundle != null) {
            extras.putAll(bundle);
        }
        return this;
    }

    public RouteBuilder withContext(Context context) {
        this.context = context;
        return this;
    }

    
    public RouteBuilder withBundle(String key, Bundle bundle) {
        extras.putBundle(key, bundle);
        return this;
    }

    public RouteBuilder withData(Uri uri) {
        this.uri = uri;
        return this;
    }

    public RouteBuilder withType(String type) {
        this.type = type;
        return this;
    }

    
    public RouteBuilder withCharSequence(String key, CharSequence cArg) {
        extras.putCharSequence(key, cArg);
        return this;
    }

    
    public RouteBuilder withCharSequenceArray(String key, CharSequence[] cArg) {
        extras.putCharSequenceArray(key, cArg);
        return this;
    }

    
    public RouteBuilder withString(String key, String sArg) {
        extras.putString(key, sArg);
        return this;
    }

    
    public RouteBuilder withStringArray(String key, String[] sArg) {
        extras.putStringArray(key, sArg);
        return this;
    }

    
    public RouteBuilder withStringList(String key, ArrayList<String> sArg) {
        extras.putStringArrayList(key, sArg);
        return this;
    }



    
    public RouteBuilder withBoolean(String key, boolean bArg) {
        extras.putBoolean(key, bArg);
        return this;
    }

    
    public RouteBuilder withBooleanArray(String key, boolean[] bArg) {
        extras.putBooleanArray(key, bArg);
        return this;
    }

    
    public RouteBuilder withChar(String key, char cArg) {
        extras.putChar(key, cArg);
        return this;
    }

    
    public RouteBuilder withCharArray(String key, char[] cArg) {
        extras.putCharArray(key, cArg);
        return this;
    }

    
    public RouteBuilder withInt(String key, int iArg) {
        extras.putInt(key, iArg);
        return this;
    }

    
    public RouteBuilder withIntArray(String key, int[] iArg) {
        extras.putIntArray(key, iArg);
        return this;
    }

    
    public RouteBuilder withIntList(String key, ArrayList<Integer> iArg) {
        extras.putIntegerArrayList(key, iArg);
        return this;
    }

    
    public RouteBuilder withShort(String key, short shortArg) {
        extras.putShort(key, shortArg);
        return this;
    }

    
    public RouteBuilder withShortArray(String key, short[] shortArg) {
        extras.putShortArray(key, shortArg);
        return this;
    }

    
    public RouteBuilder withByte(String key, byte bArg) {
        extras.putByte(key, bArg);
        return this;
    }

    
    public RouteBuilder withByteArray(String key, byte[] bArg) {
        extras.putByteArray(key, bArg);
        return this;
    }

    
    public RouteBuilder withLong(String key, long lArg) {
        extras.putLong(key, lArg);
        return this;
    }

    
    public RouteBuilder withLongArray(String key, long[] longArg) {
        extras.putLongArray(key, longArg);
        return this;
    }

    
    public RouteBuilder withFloat(String key, float fArg) {
        extras.putFloat(key, fArg);
        return this;
    }

    
    public RouteBuilder withFloatArray(String key, float[] floatArg) {
        extras.putFloatArray(key, floatArg);
        return this;
    }

    
    public RouteBuilder withDouble(String key, double dArg) {
        extras.putDouble(key, dArg);
        return this;
    }

    
    public RouteBuilder withDoubleArray(String key, double[] dArg) {
        extras.putDoubleArray(key, dArg);
        return this;
    }

    
    public RouteBuilder withSerializable(String key, Serializable serialize) {
        extras.putSerializable(key, serialize);
        return this;
    }

    public RouteBuilder withParcelable(String key, Parcelable parcelable) {
        extras.putParcelable(key, parcelable);
        return this;
    }

    public RouteBuilder withParcelableArray(String key, Parcelable[] parcelableArray) {
        extras.putParcelableArray(key, parcelableArray);
        return this;
    }

    
    public RouteBuilder withFlag(int flags) {
        this.flags |= flags;
        return this;
    }

    
    public RouteBuilder withTransition(int enterAnim, int exitAnim) {
        this.enterAnim = enterAnim;
        this.exitAnim = exitAnim;
        return this;
    }

    public RouteBuilder interceptorTimeout(long timeout) {
        this.interceptorTimeout = timeout;
        return this;
    }

    public RouteBuilder withAction(String action) {
        this.action = action;
        return this;
    }

    @Override
    public String toString() {
        return "RouteBuilder{" +
                "path='" + path + '\'' +
                ", uri=" + uri +
                '}';
    }

    public void withCharSequenceList(String key, ArrayList<CharSequence> arg) {
        extras.putCharSequenceArrayList(key, arg);
    }

    public void withIntegerList(String key, ArrayList<Integer> arg) {
        extras.putIntegerArrayList(key, arg);
    }
}
