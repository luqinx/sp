
-ignorewarnings

-dontwarn com.google.common.**

-dontwarn net.bytebuddy.**
-keep class net.bytebuddy.** {*;}

-keep class chao.android.tools.router.** {*;}
-keep class chao.android.tools.rpc.** {*;}
-keep class chao.android.tools.servicepool.** {*;}

-keep @chao.java.tools.servicepool.annotation.* class *
-keep class * implements chao.java.tools.servicepool.IService { *; }

-keep @chao.android.tools.rpc.annotation.* class ** {*;}

-keep class chao.java.tools.** {*;}

-keep class com.android.** {*;}

# 避免混淆泛型
#-keepattributes Signature
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable




