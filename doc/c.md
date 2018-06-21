```
$ adb push C:\Users\hp\ASMine\adb-j\app-toggle-adbtcp\build\outputs\apk\debug\app-toggle-adbtcp-debug.apk /data/local/tmp/com.woodyhi.adb
$ adb shell pm install -t -r "/data/local/tmp/com.woodyhi.adb"
	pkg: /data/local/tmp/com.woodyhi.adb
Success

$ adb shell am start -n "com.woodyhi.adb/com.woodyhi.adb.MainActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
```