## Android Build Kit 使用说明

**项目根目录的build.gradle添加**


```
apply plugin: "abkit"  // 1. 根目录直接应用abkit插件


buildscript {

    repositories {
        ...
        maven { url "http://47.99.188.223:8081/repository/maven-public/" } // 2. 仓库地址
    }
    
    dependencies {
        ...
        classpath "chao.android.gradle:abkit:0.0.2"  // 3. 引入插件jar
    }
}
```

通常插件是放到具体的Module项目下引入插件，abkit不同， 直接在根的build.gradle下引用


### Android SDK版本管理

模块化/组件化开发过程中，组件Module比较多，常常会因为组件版本不统一而导致编译出错。使用abkit可以收敛SDK版本配置方式
，在根的build.gradle中统一管理SDK版本。简化gradle配置，避免因SDK版本造成的编译困扰。

```
abkit {
    abVersion {
        minSdkVersion 15
        compileSdkVersion 28
        targetSdkVersion 28
        buildToolsVersion "28.0.3"
        force true  //强制所有Module使用这里的SDK版本, false的话，优先使用各个Module自己的SDK版本
                    //只有当Module不设置SDK版本时，才会使用这里的SDK版本
    }
}
```



### 组件上传到自己的maven私有仓库

模块化/组件化开发过程中，可能需要将Module打成aar并生成doc文档然后上传到公司/三方的/自己的Maven仓库。abkit集成了maven上传功能。
只需要简单的几步就可以配置好jar/aar上传功能。

第一步，在根目录下的gradle.properties、maven.properties或local.properties下增加如下几个属性，属性可以分散放到不同的properties文件
毕竟用户名/密码只适合放到local.properties

```
#组件打包上传时，默认会自动增加snapshot后缀，如果想打正式包，设置publishRelease为true
publishRelease=true

#普通用户的Nexus Maven仓库的用户名
nexusUserName=user
#普通用户的Nexus Maven仓库的密码
nexusPassword=user.password
#snapshot上传地址, 下面的值是插件内置的默认地址，需换成你自己的上传地址
nexusSnapshotUrl=http://47.99.188.223:8081//repository/maven-snapshots/

#是否需要管理账号才能上传正式包，可以按自己需求在Nexus自行配置，这里只提供方案，不强制。
#所以如果nexusSnapshotUrl填正式的上传地址也是可以的。


#管理账号，需要换成你自己的用户名 
adminUserName=admin
#管理密码，需要换成你自己的密码
adminPassword=admin.password 
#正式包的上传地址, 下面的值是插件内置的默认地址，需换成你自己的上传地址
nexusUrl=http://47.99.188.223:8081/repository/maven-releases/

```

第二步， 在需要上传的Module的build.gradle中，添加以下配置
```
abkit {
    maven {
        artifactId 'servicepool'    // 组件artifactId
        groupId 'chao.java.tools'   // 组件groupId
        versionName '1.0.0'         // jar/aar版本
        //这三个参数组合在一起就是 'chao.java.tools:servicepool:1.0.0'
        //这里需要注意的是，如果没有设置publishRelease为true, 实际会生成'chao.java.tools:servicepool:1.0.0.snapshot'
    }
}
```



### 组件依赖管理 (重点功能)

模块化/组件化开发过程中，一旦组件多起来以后，组件的依赖管理会变得繁琐。一个依赖的组件可能是一个本地project,也可能是一个远程aar/jar包，
我们需要在本地project和远程aar之间做切换。如果是远程包会有不同的版本，项目可能同时引入了同一个组件的不同版本而使我们在使用时产生困惑。
还有当我们需要修改一个远程包的版本时，需要去检查每一个project下是否有这个远程包依赖，即使这样还是有可能改不正确。

1. 由于组件相互依赖的关系，我们通常不能明确当前用的是组件的哪个版本，这常常使我们困惑
2. 我们常常会把项目打成aar/jar包，上传到maven仓库,
   开发时就有project和远程aar两种依赖方式。这两种方式的使用和切换过程也会有很多麻烦
3. 当一个远程aar组件被很多模块依赖的时候，当这个组件版本升级后，需要查找所有的模块是否有依赖这个组件
4. 当我只是想临时禁用掉某个组件来调试时,这个组件可能被太多的模块依赖导致操作异常繁琐
5. 如果项目中有几十个上百个组件,
   当前我只想调试某一个组件或几个组件，难道我需要把这几十个甚至上百个组件全部装载起来吗？

**带着上面的问题，下面介绍这种新的组件依赖管理模式:**

第一步，在settings.gradle中使用SettingsInject来接管settings.gradle!!!

```
import chao.android.gradle.plugin.api.SettingsInject  //会报红，编译时不报错就不影响使用
SettingsInject.inject(getSettings(), getGradle())

include ":app"

```

接管settings.gradle后，settings.gradle原有功能不受影响，还可以继续使用。只是扩展了一个新的配置脚本modules.gradle

第二步，
在settings.gradle同级目录下新增modules.gradle,modules.gradle使用如下方式来配置modules:

```
module("servicepool") //servicepool是名称， 可以自由定义
    .project(":servicepool") // ":servicepool"是project名称
    .remote("chao.java.tools:servicepool:1.0.0")  // 与模块":servicepool"对应的远程aar/jar的名称
                                                  // 无论在dependencies引用时"chao.java.tools:servicepool"的版本号是什么,最终使用的都是这里设置的版本号
    .include()      // include()表示当前使用":servicepool"而不是远程依赖"chao.java.tools:servicepool:1.0.0"。
                    // 如果不调用include()方法，会使用远程依赖"chao.java.tools:servicepool:1.0.0"同时":servicepool"不会被引入到项目
                    // .project()和.remote()至少有其中一个
    .disabled() // 禁用当前module。 这在想要临时禁用掉某个module时非常有用 

module("fastjson")
    .remote("com.alibaba:fastjson:1.1.68.android")
    .disabled()

module("servicepool_android")
    .project(":servicepool-android")
    .remote("chao.java.tools:servicepool-android:1.0.0")
//    .include()  不调用include(),将会使用远程依赖 "chao.java.tools:servicepool-android:1.0.0"
    
```

modules配置好以后就可以在build.gradle中被引用了。 

第三步， 在app或者其他module的dependencies{}中引入依赖。

```
dependencies {
    implementation servicepool          // servicepool是modules.gradle中定义module的名称，注意没有双引号
    implementation servicepool_android  
    
    api fastjson                        // 第二步中， fastjson调用了disabled(), fastjson将不会被引入到依赖，
                                        // 相当于这一条不存在，这在想要临时禁用掉fastjson时非常有用
                                        
    implementation 'chao.java.tools:servicepool:1.1.0'  // 由于modules.gradle中已经配置了servicepool的版本号为1.0.0, 最终依赖servicepool的版本将会是1.0.0，而不是1.1.0
}
```

第四步，
modules.gradle文件是默认的配置文件，也可以在gradle.properties、plugin.properties或者local.properties中通过modules.gradle.file属性手动指定具体使用哪个文件做为modules的配置文件

```
modules.gradle.file=sample.gradle
```
接上面的第5个问题，如果项目有100多个project，而此刻我只想调试project(":sample"),
而sample依赖project(":sampleLib")，我们只需要在sample.gradle中做配置

```
module("sample").project(":sample").include()

module("sampleLib").project(":sample_lib").include()

...
```

同步以后，
只会有sample和sampleLib两个project会被引入到工程。这样将能减少gradle编译时间并大大提高调试效率

**总结**

abkit的组件依赖管理模式是:
所有组件的配置都放到了modules.gradle文件中,在build.gradle的depencencies{}中引用时，直接引用modules.gradle中配置的module名称即可。如果是远程组件，
组件的版本号是modules.gradle中module配置的版本号。module可以自由的在project依赖和远程aar依赖之间切换。很容易禁用或启用某个module。



### gradle 调试方法

export GRADLE_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"

