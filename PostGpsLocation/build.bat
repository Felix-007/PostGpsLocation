::zhanghanguo@baidu.com

::1������ BaiduNaviSDK
::����������
set PATH=%PATH%;C:\android-ndk-r8b
cd ../BaiduNaviSDK
::cd ../../../../navioffline3.1/app/naviapp/baidu-navi/android/BaiduNaviSDK
call build.bat

IF ERRORLEVEL 1 goto err1

::2������ jar ����Ӧ��Ŀ¼
copy output\BaiduNaviSDK_1.0.jar ..\BaiduNaviSDKDemo\libs\
copy output\BaiduNaviSDK_Resource_1.0.jar ..\BaiduNaviSDKDemo\assets\
copy libs\armeabi\*.so ..\BaiduNaviSDKDemo\libs\armeabi\

::���²���
::copy output\BaiduNaviSDK_1.0.jar ..\..\..\..\..\..\navisdk\android\APIDemo\BaiduNaviSdkApiDemoMain\libs\
::copy output\BaiduNaviSDK_Resource_1.0.jar ..\..\..\..\..\..\navisdk\android\APIDemo\BaiduNaviSdkApiDemoMain\assets\
::copy libs\armeabi\*.so ..\..\..\..\..\..\navisdk\android\APIDemo\BaiduNaviSdkApiDemoMain\libs\armeabi\

cd ../BaiduNaviSDKDemo
::cd ../../../../../../navisdk/android/APIDemo/BaiduNaviSdkApiDemoMain
::3������ Demo ����
call demo_build.bat

IF ERRORLEVEL 1 goto err2

copy bin\BaiduNaviSdkApiDemoMain-release.apk output\

goto :EOF

:err1
@echo ERROR=============SDK and Navi engine SO build fail!================
exit 1

:err2
@echo ERROR=============SDKDemo build fail!==============
exit 1

