https://stackoverflow.com/questions/40375555/start-android-activity-from-cordova-plugin

1、安装plugman
npm install -g plugman
2、创建plugin
plugman create --name 插件名　--plugin_id　插件id --plugin_version 插件版本
// 插件id,比如 com.digichain.xin
// 插件版本，比如 1.0.0
3、添加platform
plugman platform add --platform_name andorid/ios
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
