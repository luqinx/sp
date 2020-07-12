package chao.android.gradle.servicepool.compiler;

import org.objectweb.asm.Opcodes;

/**
 * @author luqin
 * @since  2019-07-13
 */
public interface Constant extends Opcodes {
    String SERVICE_FULL_NAME = "chao.java.tools.servicepool.IService";

    String SERVICE_ASM_NAME = SERVICE_FULL_NAME.replaceAll("\\.", "/");

    String GENERATE_SERVICE_PACKAGE_NAME = "chao/java/tools/servicepool/gen";

    /** FactoriesService **/
    String SERVICE_FACTORIES_ASM_NAME = "chao/java/tools/servicepool/ServiceFactories";

    String SERVICE_FACTORY_ASM_NAME = "chao/java/tools/servicepool/IServiceFactory";


    String GENERATE_SERVICE_FACTORIES_NAME = "ServiceFactoriesInstance";

    String GENERATE_SERVICE_FACTORIES_INSTANCE_ASM_NAME = GENERATE_SERVICE_PACKAGE_NAME + "/" + GENERATE_SERVICE_FACTORIES_NAME;

    String GENERATE_SERVICE_SUFFIX = "gen_ServiceFactory";

    /** InitServices **/
    String SERVICE_INIT_SERVICES_ASM_NAME = "chao/java/tools/servicepool/AbsInitServiceManager";

    String GENERATE_INIT_SERVICES_NAME = "InitServiceManagerInstance";

    String GENERATE_INIT_SERVICE_INSTANCE_ASM_NAME = GENERATE_SERVICE_PACKAGE_NAME + "/" + GENERATE_INIT_SERVICES_NAME;

    /** PathServices **/
    String SERVICE_PATH_SERVICES_ASM_NAME = "chao/java/tools/servicepool/PathServices";

    String GENERATE_PATH_SERVICES_NAME = "PathServicesInstance";

    String GENERATE_PATH_SERVICE_INSTANCE_ASM_NAME = GENERATE_SERVICE_PACKAGE_NAME + "/" + GENERATE_PATH_SERVICES_NAME;



    String GENERATE_FILE_NAME_SUFFIX = ".class";


    String METHOD_SCOPE = "scope";

    String METHOD_PRIORITY = "priority";

    String METHOD_PATH = "path";

    String METHOD_VALUE = "value";

    String METHOD_ASYNC = "async";

    String METHOD_LAZY = "lazy";

    String METHOD_DEPENDENCIES = "dependencies";

    String EXTENSION_NAME = "autoservice";

    String SERVICE_DESC = "Lchao/java/tools/servicepool/annotation/Service;";

    String SERVICES_DESC = "Lchao/java/tools/servicepool/annotation/Services;";


    String INIT_DESC = "Lchao/java/tools/servicepool/annotation/Init;";


    String SERVICE_ANNOTATION = "chao.java.tools.servicepool.annotation.Service";

    String SERVICES_ANNOTATION = "chao.java.tools.servicepool.annotation.Services";


    String SERVICE_POOL_PACKAGE_NAME = "chao.java.tools.servicepool";


    /** Event **/

    String EVENT_DESC = "Lchao/java/tools/servicepool/annotation/Event;";


    String EVENT_ANNOTATION = "chao.java.tools.servicepool.annotation.Event";

    String EVENT_FULL_NAME = "chao.java.tools.servicepool.event.EventService";

    String EVENT_ASM_NAME = EVENT_FULL_NAME.replaceAll("\\.", "/");

}
