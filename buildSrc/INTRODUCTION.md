### 统一Android版本号
   解决组件化版本不方便管理问题
   现在可以通过在项目的根目录的build.gradle中插入如下代码来统一定义Android版本

    ```
    acVersion {

       minSdkVersion  27

       compileSdkVersion  27

       targetSdkVersion  17

       buildToolsVersion  '27.0.3'

    }

    ```



### maven上传



### implementation便捷切换
