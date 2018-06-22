```
$ adb push C:\Users\hp\ASMine\adb-j\app-toggle-adbtcp\build\outputs\apk\debug\app-toggle-adbtcp-debug.apk /data/local/tmp/com.woodyhi.adb
$ adb shell pm install -t -r "/data/local/tmp/com.woodyhi.adb"
	pkg: /data/local/tmp/com.woodyhi.adb
Success

$ adb shell am start -n "com.woodyhi.adb/com.woodyhi.adb.MainActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
```

微鲸电视  Error: Unknown option: -g

```
// 安装
shell:am start -a android.intent.action.VIEW -t application/vnd.android.package-archive -d file:///sdcard/tmp/test.apk

// 网页
am start -a android.intent.action.VIEW -d  http://www.google.cn/
```