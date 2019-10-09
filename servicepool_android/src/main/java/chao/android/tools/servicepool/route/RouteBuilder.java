package chao.android.tools.servicepool.route;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;

import chao.java.tools.servicepool.annotation.Service;

/**
 * @author luqin
 * @since 2019-07-25
 */
public class RouteBuilder {

    private static final int ROUTE_DEFAULT_REQUEST_CODE = 0xf000;

    @Service
    private RouteManager routeManager;

    Context context;

    final String path;

    Bundle args;

    Uri uri;

    String type;

    int flags;

    long interceptorTimeout = 100L; // 拦截超时, 单位: 秒

    int enterAnim = -1;

    int exitAnim = -1;

    int requestCode = ROUTE_DEFAULT_REQUEST_CODE;


    public RouteBuilder(String path) {
        this.args = new Bundle();
        this.path = path;
        if (TextUtils.isEmpty(this.path)) {
            throw new IllegalArgumentException("route path should not be empty.");
        }
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
            args = bundle;
        }
        return this;
    }

    public RouteBuilder withContext(Context context) {
        this.context = context;
        return this;
    }

    
    public RouteBuilder withBundle(String key, Bundle bundle) {
        args.putBundle(key, bundle);
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
        args.putCharSequence(key, cArg);
        return this;
    }

    
    public RouteBuilder withCharSequenceArray(String key, CharSequence[] cArg) {
        args.putCharSequenceArray(key, cArg);
        return this;
    }

    
    public RouteBuilder withString(String key, String sArg) {
        args.putString(key, sArg);
        return this;
    }

    
    public RouteBuilder withStringArray(String key, String[] sArg) {
        args.putStringArray(key, sArg);
        return this;
    }

    
    public RouteBuilder withStringList(String key, ArrayList<String> sArg) {
        args.putStringArrayList(key, sArg);
        return this;
    }

    
    public RouteBuilder withBoolean(String key, boolean bArg) {
        args.putBoolean(key, bArg);
        return this;
    }

    
    public RouteBuilder withBooleanArray(String key, boolean[] bArg) {
        args.putBooleanArray(key, bArg);
        return this;
    }

    
    public RouteBuilder withChar(String key, char cArg) {
        args.putChar(key, cArg);
        return this;
    }

    
    public RouteBuilder withCharArray(String key, char[] cArg) {
        args.putCharArray(key, cArg);
        return this;
    }

    
    public RouteBuilder withInt(String key, int iArg) {
        args.putInt(key, iArg);
        return this;
    }

    
    public RouteBuilder withIntArray(String key, int[] iArg) {
        args.putIntArray(key, iArg);
        return this;
    }

    
    public RouteBuilder withIntList(String key, ArrayList<Integer> iArg) {
        args.putIntegerArrayList(key, iArg);
        return this;
    }

    
    public RouteBuilder withShort(String key, short shortArg) {
        args.putShort(key, shortArg);
        return this;
    }

    
    public RouteBuilder withShortArray(String key, short[] shortArg) {
        args.putShortArray(key, shortArg);
        return this;
    }

    
    public RouteBuilder withByte(String key, byte bArg) {
        args.putByte(key, bArg);
        return this;
    }

    
    public RouteBuilder withByteArray(String key, byte[] bArg) {
        args.putByteArray(key, bArg);
        return this;
    }

    
    public RouteBuilder withLong(String key, long lArg) {
        args.putLong(key, lArg);
        return this;
    }

    
    public RouteBuilder withLongArray(String key, long[] longArg) {
        args.putLongArray(key, longArg);
        return this;
    }

    
    public RouteBuilder withFloat(String key, float fArg) {
        args.putFloat(key, fArg);
        return this;
    }

    
    public RouteBuilder withFloatArray(String key, float[] floatArg) {
        args.putFloatArray(key, floatArg);
        return this;
    }

    
    public RouteBuilder withDouble(String key, double dArg) {
        args.putDouble(key, dArg);
        return this;
    }

    
    public RouteBuilder withDoubleArray(String key, double[] dArg) {
        args.putDoubleArray(key, dArg);
        return this;
    }

    
    public RouteBuilder withSerializable(String key, Serializable serialize) {
        args.putSerializable(key, serialize);
        return this;
    }

    public RouteBuilder withParcelable(String key, Parcelable parcelable) {
        args.putParcelable(key, parcelable);
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

    @Override
    public String toString() {
        return "RouteBuilder{" +
                "path='" + path + '\'' +
                ", uri=" + uri +
                '}';
    }
}
