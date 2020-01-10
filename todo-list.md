1. 解耦合              
2. 解决组件之间循环引用
3. 组件热插拔，组件快速替换     done
4. 组件初始化

5. 依赖注入(注解)  done

6. 组件手动销毁和自动销毁
7. 路由

## sp能力

#### Mock能力
    1. 接口Mock
    2. 环境Mock, debug, alpha, publish..
    3. 功能调试Mock等等
    
    
#### Router


#### Event


#### 模块初始化

byte-buddy Android实现       done
依赖注入
线程安全
service回收策略
service优先级

Service list支持


注意混淆

    
@Service标记内部类时，可能导致内存泄露 (不会造成内存泄露, 因为对象不是外部类创建的, 内部类不会有外部类的引用)
    如果标记的是内部类， 应该强制将缓存策略改为once

autoservice:
    解析Init注解        done.
    
    Init注解， 默认有Service注解功能
    
    Init注解, 增加按需加载属性        done.  

    @Service标记静态变量时, 赋值操作应该在静态初始化代码块中执行     doing       done
        因为<init>方法不一定会被执行

    @Service标记的类如果不是public权限，会出现访问失败            doing   done
        1. 将factory的包名和Service类一致                       done
        2. 不允许是private权限， 否则在编译器报错并停止编译过程       not need
        

CombineService

EventService    done


引入生命周期？
    1. android lifecycle
    2. field tag

支持通过path获取Service  done.

支持@Inherit ？



问题汇总:

1. android 9.0 byte-buddy适配失败       done.

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



2. autoservice在Windows电脑上编译失败 done