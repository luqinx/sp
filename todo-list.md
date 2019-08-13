byte-buddy Android实现       done
依赖注入
线程安全
service回收策略
service优先级

Service注解， 编译器动态插入/meta-inf/services/com.xxx.Xxx

注意混淆




问题汇总:

1. android 9.0 byte-buddy适配失败

08-12 15:37:36.649 10187 10187 E AndroidRuntime: FATAL EXCEPTION: main
08-12 15:37:36.649 10187 10187 E AndroidRuntime: Process: com.gihoo.camera, PID: 10187
08-12 15:37:36.649 10187 10187 E AndroidRuntime: java.lang.RuntimeException: Unable to create application com.gihoo.camera.common.App: java.lang.RuntimeException: NoOpInstance创建失败, Cannot invoke BaseDexClassLoader#addDexPath(String, boolean)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at android.app.ActivityThread.handleBindApplication(ActivityThread.java:6656)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at android.app.ActivityThread.access$2000(ActivityThread.java:268)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1995)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at android.os.Handler.dispatchMessage(Handler.java:109)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at android.os.Looper.loop(Looper.java:207)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at android.app.ActivityThread.main(ActivityThread.java:7539)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at java.lang.reflect.Method.invoke(Native Method)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:524)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:958)
08-12 15:37:36.649 10187 10187 E AndroidRuntime: Caused by: java.lang.RuntimeException: NoOpInstance创建失败, Cannot invoke BaseDexClassLoader#addDexPath(String, boolean)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at chao.java.tools.servicepool.NoOpInstanceFactory.newInstance(NoOpInstanceFactory.java:55)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at chao.java.tools.servicepool.DefaultServiceController.getServiceByClass(DefaultServiceController.java:133)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at chao.java.tools.servicepool.ServicePool.loadServices(ServicePool.java:32)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at chao.android.tools.servicepool.AndroidServicePool.init(AndroidServicePool.java:22)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at com.gihoo.camera.common.App.onCreate(App.java:70)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at android.app.Instrumentation.callApplicationOnCreate(Instrumentation.java:1162)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at android.app.ActivityThread.handleBindApplication(ActivityThread.java:6636)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        ... 8 more
08-12 15:37:36.649 10187 10187 E AndroidRuntime: Caused by: java.lang.IllegalStateException: Cannot invoke BaseDexClassLoader#addDexPath(String, boolean)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at net.bytebuddy.android.AndroidClassLoadingStrategy$Injecting$Dispatcher$ForAndroidPVm.loadDex(AndroidClassLoadingStrategy.java:540)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at net.bytebuddy.android.AndroidClassLoadingStrategy$Injecting.doLoad(AndroidClassLoadingStrategy.java:415)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at net.bytebuddy.android.AndroidClassLoadingStrategy.load(AndroidClassLoadingStrategy.java:142)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at net.bytebuddy.android.AndroidClassLoadingStrategy$Injecting.load(AndroidClassLoadingStrategy.java:408)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at chao.android.tools.servicepool.AndroidLazyStrategy.initialize(AndroidLazyStrategy.java:54)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at net.bytebuddy.dynamic.DynamicType$Default$Unloaded.load(DynamicType.java:5623)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at net.bytebuddy.dynamic.DynamicType$Default$Unloaded.load(DynamicType.java:5612)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at chao.android.tools.servicepool.AndroidNoOpInstantiator.make(AndroidNoOpInstantiator.java:39)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at chao.java.tools.servicepool.NoOpInstanceFactory.newInstance(NoOpInstanceFactory.java:51)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        ... 14 more
08-12 15:37:36.649 10187 10187 E AndroidRuntime: Caused by: java.lang.SecurityException: Can't exempt class, process is not debuggable.
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at dalvik.system.DexFile.setTrusted(Native Method)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at dalvik.system.DexFile.setTrusted(DexFile.java:385)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at dalvik.system.DexPathList.makeDexElements(DexPathList.java:373)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at dalvik.system.DexPathList.addDexPath(DexPathList.java:226)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at dalvik.system.BaseDexClassLoader.addDexPath(BaseDexClassLoader.java:155)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at java.lang.reflect.Method.invoke(Native Method)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        at net.bytebuddy.android.AndroidClassLoadingStrategy$Injecting$Dispatcher$ForAndroidPVm.loadDex(AndroidClassLoadingStrategy.java:531)
08-12 15:37:36.649 10187 10187 E AndroidRuntime:        ... 22 more



2. autoservice在Windows电脑上编译失败