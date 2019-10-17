https://stackoverflow.com/questions/40375555/start-android-activity-from-cordova-plugin

1、安装plugman
npm install -g plugman
2、创建plugin
plugman create --name 插件名　--plugin_id　插件id --plugin_version 插件版本
// 插件id,比如 com.digichain.xin
// 插件版本，比如 1.0.0
3、添加platform
plugman platform add --platform_name android/ios
//添加后src下会出现android
//并且初始化了java文件、js文件以及plugin.xml
4、安装
cordova create testplugin com.digichain.testplugin
cordova platform add android
cordova plugins add 插件地址
5、测试
在/www/js/index/js下添加事件进行测试，运行一下
cordova　run android

引入jar包



<config-file target="AndroidManifest.xml" parent="/manifest/application">
maven { url 'http://maven.aliyun.com/nexus/content/repositories/google' }
maven { url 'http://maven.aliyun.com/nexus/content/groups/public/' }
maven { url 'http://maven.aliyun.com/nexus/content/repositories/jcenter'}

在project.properties中添加
android.library.reference.3=zxing
然后settings.gradle中就会多加
include ":zxing"

https://www.codeproject.com/Articles/840623/Android-Character-Recognition
https://github.com/jhansireddy/AndroidScannerDemo

https://blog.csdn.net/qq_17766199/article/details/77963278
https://github.com/simplezhli/Tesseract-OCR-Scanner
https://www.cnblogs.com/asingingfish/p/6196638.html?utm_source=itdadao&utm_medium=referral

ionic build --prod --aot --release
cordova build android --prod --release