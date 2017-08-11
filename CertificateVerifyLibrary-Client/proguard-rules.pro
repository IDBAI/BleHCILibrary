# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\AndroidSDK\SDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


# 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
#指定代码的压缩级别
-optimizationpasses 5
-allowaccessmodification
#预校验 不校验
-dontpreverify
#优化  不优化输入的类文件
#-dontoptimize

# The remainder of this file is identical to the non-optimized version
# of the Proguard configuration file (except that the other file has
# flags to turn off optimization).
#包明不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
#混淆时是否记录日志
-verbose
#保护注解
-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
#保持 native 方法不被混淆
# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}
#保持自定义控件类不被混淆
# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
#保持枚举 enum 类不被混淆
# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
#保持 Serializable 不被混淆,基本是model实体类
-keepnames class * implements java.io.Serializable
#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}
#如果引用了v4或者v7包
# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**
#忽略警告
-ignorewarnings

#淆保护自己项目的部分代码以及引用的第三方jar包
#-libraryjars /libs/volley.jar

-dontwarn org.bouncycastle.**
-dontwarn org.spongycastle.**
#不混淆包下面的类，** 通配符
-keep class org.bouncycastle.**
-keep class org.spongycastle.**

#不混淆包下的所有类和类的所有成员变量
-keep class org.bouncycastle.**{*;}
-keep class org.spongycastle.**{*;}

-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment
-keep class * extends java.lang.annotation.Annotation { *; }


#public变量不能混淆
-keepclassmembers class com.revenco.certificateverifylib.common.DeviceTypehelper{
    public <fields>;
}
-keepclassmembers class com.revenco.certificateverifylib.common.ShellTools{
    public <fields>;
}

#避免混淆
-keep class com.revenco.certificateverifylib.bean.** { *; }
-keep class com.revenco.certificateverifylib.common.** { *;}
-keep class com.revenco.certificateverifylib.core.** {*;}
