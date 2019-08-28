package chao.android.gradle.servicepool.compiler;

import org.objectweb.asm.Opcodes;

/**
 * @author luqin
 * @since  2019-07-13
 */
public interface Constant extends Opcodes {
    String SERVICE_FULL_NAME = "chao.java.tools.servicepool.IService";

    String SERVICE_ASM_NAME = SERVICE_FULL_NAME.replaceAll("\\.", "/");

    String SERVICE_FACTORIES_ASM_NAME = "chao/java/tools/servicepool/ServiceFactories";

    String SERVICE_FACTORY_ASM_NAME = "chao/java/tools/servicepool/IServiceFactory";


    String GENERATE_SERVICE_PACKAGE_NAME = "chao/java/tools/servicepool/gen/";

    String GENERATE_SERVICE_FACTORIES_NAME = "ServiceFactoriesInstance";

    String GENERATE_SERVICE_FACTORIES_INSTANCE_ASM_NAME = GENERATE_SERVICE_PACKAGE_NAME + GENERATE_SERVICE_FACTORIES_NAME;

    String GENERATE_SERVICE_SUFFIX = "_ServiceFactory";

    String GENERATE_FILE_NAME_SUFFIX = ".class";


    String METHOD_SCOPE = "scope";

    String METHOD_PRIORITY = "priority";

    String METHOD_TAG = "tag";

    String METHOD_VALUE = "value";

    String METHOD_ASYNC = "async";

    String METHOD_DEPENDENCIES = "dependencies";

    String EXTENSION_NAME = "autoservice";

    String SERVICE_DESC = "Lchao/java/tools/servicepool/annotation/Service;";

    String INIT_DESC = "Lchao/java/tools/servicepool/annotation/Init;";

    String EVENT_DESC = "Lchao/java/tools/servicepool/annotation/Event;";

}
