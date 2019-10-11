@echo on
D:
cd D:
xcopy "D:\lzy\MyIonicProject2\platforms\android\app\build\outputs\apk\release\app-release-unsigned.apk" "D:\Java\jdk\bin\" /y
cd D:\Java\jdk\bin
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore my-release-key.jks -storepass 807222676 -signedjar tessorc.apk app-release-unsigned.apk my-alias
xcopy "D:\Java\jdk\bin\tessorc.apk" "D:\Android\Sdk\build-tools\28.0.3\" /y
c:
cd D:\Android\Sdk\build-tools\28.0.3\
zipalign -v 4 tessorc.apk tessorc.apk
echo "======="
xcopy "D:\Android\Sdk\build-tools\28.0.3\tessorc.apk" "D:\lzy\MyIonicProject2\" /y
pause