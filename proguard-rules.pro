# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontwarn org.hamcrest.**
-dontwarn org.junit.**
-dontwarn com.squareup.**
-dontwarn android.test.**
-dontwarn android.content.**
-dontwarn android.graphics.**
-dontwarn android.util.**
-dontwarn android.view.**
-dontwarn android.arch.lifecycle.**

-dontskipnonpubliclibraryclassmembers

-keepattributes InnerClasses

-keep class **.R
-keep class **.R$* {
    <fields>;
}

-assumenosideeffects class android.util.Log {
  public static boolean isLoggable(java.lang.String, int);
  public static int v(...);
  public static int i(...);
  public static int w(...);
  public static int d(...);
  public static int e(...);
}

# -keep class org.home.SomeClass
# {
#   *;
# }

# -keepclassmembers class org.home.SomeClass
# -keepclassmembers class org.home.OtherClass
# {
#   *;
# }
