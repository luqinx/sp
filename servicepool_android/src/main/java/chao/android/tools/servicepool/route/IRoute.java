package chao.android.tools.servicepool.route;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author luqin
 * @since 2019-07-25
 */
public interface IRoute {
    void navigation();

    void navigation(Context context);

    void navigation(Activity activity, int requestCode);

    IRoute with(Bundle bundle);

    IRoute withBundle(String key, Bundle bundle);

    IRoute withCharSequence(String key, CharSequence cArg);

    IRoute withCharSequenceArray(String key, CharSequence[] cArg);

    IRoute withString(String key, String sArg);

    IRoute withStringArray(String key, String[] sArg);

    IRoute withStringList(String key, ArrayList<String> sArg);

    IRoute withBoolean(String key, boolean bArg);

    IRoute withBooleanArray(String key, boolean[] bArg);

    IRoute withChar(String key, char cArg);

    IRoute withCharArray(String key, char[] cArg);

    IRoute withInt(String key, int iArg);

    IRoute withIntArray(String key, int[] iArg);

    IRoute withIntList(String key, ArrayList<Integer> iArg);

    IRoute withShort(String key, short shortArg);

    IRoute withShortArray(String key, short[] shortArg);

    IRoute withByte(String key, byte bArg);

    IRoute withByteArray(String key, byte[] bArg);

    IRoute withLong(String key, long lArg);

    IRoute withLongArray(String key, long[] longArg);

    IRoute withFloat(String key, float fArg);

    IRoute withFloatArray(String key, float[] floatArg);

    IRoute withDouble(String key, double dArg);

    IRoute withDoubleArray(String key, double[] dArg);

    IRoute withSerializable(String key, Serializable serialize);

    IRoute withFlag(int flags);

    IRoute withTransition(int enterAnim, int exitAnim);
}
