package chao.android.tools.servicepool.route;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import java.io.Serializable;
import java.util.ArrayList;

import chao.android.tools.servicepool.AndroidServicePool;

/**
 * @author luqin
 * @since 2019-07-25
 */
public class Route implements IRoute {

    private Bundle args;

    private int flags;

    private int enterAnim;

    private int exitAnim;

    public Route() {
        args = new Bundle();
    }


    @Override
    public void navigation() {
        navigation(AndroidServicePool.getContext());
    }

    @Override
    public void navigation(Context context) {

    }

    @Override
    public void navigation(Activity activity, int requestCode) {

    }

    @Override
    public IRoute with(Bundle bundle) {
        if (bundle != null) {
            args = bundle;
        }
        return this;
    }

    @Override
    public IRoute withBundle(String key, Bundle bundle) {
        args.putBundle(key, bundle);
        return this;
    }

    @Override
    public IRoute withCharSequence(String key, CharSequence cArg) {
        args.putCharSequence(key, cArg);
        return this;
    }

    @Override
    public IRoute withCharSequenceArray(String key, CharSequence[] cArg) {
        args.putCharSequenceArray(key, cArg);
        return this;
    }

    @Override
    public IRoute withString(String key, String sArg) {
        args.putString(key, sArg);
        return this;
    }

    @Override
    public IRoute withStringArray(String key, String[] sArg) {
        args.putStringArray(key, sArg);
        return this;
    }

    @Override
    public IRoute withStringList(String key, ArrayList<String> sArg) {
        args.putStringArrayList(key, sArg);
        return this;
    }

    @Override
    public IRoute withBoolean(String key, boolean bArg) {
        args.putBoolean(key, bArg);
        return this;
    }

    @Override
    public IRoute withBooleanArray(String key, boolean[] bArg) {
        args.putBooleanArray(key, bArg);
        return this;
    }

    @Override
    public IRoute withChar(String key, char cArg) {
        args.putChar(key, cArg);
        return this;
    }

    @Override
    public IRoute withCharArray(String key, char[] cArg) {
        args.putCharArray(key, cArg);
        return this;
    }

    @Override
    public IRoute withInt(String key, int iArg) {
        args.putInt(key, iArg);
        return this;
    }

    @Override
    public IRoute withIntArray(String key, int[] iArg) {
        args.putIntArray(key, iArg);
        return this;
    }

    @Override
    public IRoute withIntList(String key, ArrayList<Integer> iArg) {
        args.putIntegerArrayList(key, iArg);
        return this;
    }

    @Override
    public IRoute withShort(String key, short shortArg) {
        args.putShort(key, shortArg);
        return this;
    }

    @Override
    public IRoute withShortArray(String key, short[] shortArg) {
        args.putShortArray(key, shortArg);
        return this;
    }

    @Override
    public IRoute withByte(String key, byte bArg) {
        args.putByte(key, bArg);
        return this;
    }

    @Override
    public IRoute withByteArray(String key, byte[] bArg) {
        args.putByteArray(key, bArg);
        return this;
    }

    @Override
    public IRoute withLong(String key, long lArg) {
        args.putLong(key, lArg);
        return this;
    }

    @Override
    public IRoute withLongArray(String key, long[] longArg) {
        args.putLongArray(key, longArg);
        return this;
    }

    @Override
    public IRoute withFloat(String key, float fArg) {
        args.putFloat(key, fArg);
        return this;
    }

    @Override
    public IRoute withFloatArray(String key, float[] floatArg) {
        args.putFloatArray(key, floatArg);
        return this;
    }

    @Override
    public IRoute withDouble(String key, double dArg) {
        args.putDouble(key, dArg);
        return this;
    }

    @Override
    public IRoute withDoubleArray(String key, double[] dArg) {
        args.putDoubleArray(key, dArg);
        return this;
    }

    @Override
    public IRoute withSerializable(String key, Serializable serialize) {
        args.putSerializable(key, serialize);
        return this;
    }

    @Override
    public IRoute withFlag(int flags) {
        this.flags |= flags;
        return this;
    }

    @Override
    public IRoute withTransition(int enterAnim, int exitAnim) {
        this.enterAnim = enterAnim;
        this.exitAnim = exitAnim;
        return this;
    }

}
