# ServicePool

[![GitHub license](https://img.shields.io/github/license/luqinx/sp)](https://github.com/luqinx/sp/blob/master/LICENSE)
[![build release](https://img.shields.io/badge/release-1.7.0-brightgreen)](https://search.maven.org/search?q=io.github.luqinx)
[![掘金](https://img.shields.io/badge/docs-%E6%8E%98%E9%87%91-orange)](https://juejin.cn/post/6995144994180825119)

### ServicePool接入方法

spa已迁移至mavenCentral, groupId已修正为io.github.luqinx, 包名还保持原来的包名

在项目根目录的build.gradle中引入 

```
allprojects {
    repositories {
        ...
        mavenCentral()
    }
}
```

和
 
```
buildscript {

    repositories {
        ...
        mavenCentral()
    }
    dependencies {
        ...
        classpath 'io.github.luqinx:autoservice:1.7.0'
    }
}
```

在主Module目录(一般是app目录)的build.gradle中添加 

```
apply plugin: 'sp.autoservice'

...
```

** 其他Module下按需添加依赖 **
```
dependencies {
    implementation 'io.github.luqinx:sp:1.7.0' //servicepool核心库, 是java库, 非android环境用这个就可以了
    implementation 'io.github.luqinx:spa:1.7.0' // servicepool的Android支持(spa), 只依赖spa也是可以的
    implementation 'io.github.luqinx:sprouter:1.7.0' // 基于spa的页面路由
}
```

** 混淆配置 **

```

-ignorewarnings

-dontwarn com.google.common.**

-dontwarn net.bytebuddy.**

-keep class net.bytebuddy.** {*;}
-keep class com.android.** {*;}

-keep @chao.java.tools.servicepool.annotation.* class *
-keep @chao.android.tools.rpc.annotation.* class ** {*;}

-keep class chao.android.tools.** {*;}
-keep class chao.java.tools.** {*;}

```

### ServicePool使用方式

#### 在Application的onCreate()/onAttachBaseContext()方法中初始化 
ServicePool使用懒加载思想,所有服务Service对象都是在真正使用的时刻才会去创建和初始化。这里主要是因为Android部分功能依赖Context，
初始化时可注入ApplicationContext。还有有些特定的服务有预加载的需求(如业务组件可能需要一个特定的初始化场景，而不是去依赖Application的onCreate()方法)

```
   Spa.init(this); //初始化耗时10ms左右, 设备不同可能会有些差异, 我测试的设备是小米mix3
```

Spa的更多使用细节，请参考博客[Android端简单易用的SPI框架 - SPA](https://juejin.im/post/6872335132229894158)




 
