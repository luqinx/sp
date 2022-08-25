package chao.android.gradle.servicepool.hunter.asm;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by quinn on 06/09/2018
 */
public interface IWeaver {

    int NO_JAR_ID = -1;

    /**
     * Check a certain file is weavable
     */
    boolean isWeavableClass(String filePath) throws IOException;

    /**
     * Weave single class to byte array
     */
    byte[] weaveSingleClassToByteArray(int jarId, InputStream inputStream) throws IOException;


}

