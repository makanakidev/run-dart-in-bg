package com.dev.run_dart_in_bg;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.util.ArrayList;
import androidx.annotation.Nullable;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.view.FlutterCallbackInformation;
import io.flutter.view.FlutterMain;
import io.flutter.view.FlutterNativeView;
import io.flutter.view.FlutterRunArguments;
import io.flutter.embedding.engine.dart.DartExecutor;

import static com.dev.run_dart_in_bg.InitiateCallsToDartInBgPlugin.CALLBACK_DISPATCHER_HANDLE_KEY;
import static com.dev.run_dart_in_bg.InitiateCallsToDartInBgPlugin.CALLBACK_HANDLE_KEY;
import static com.dev.run_dart_in_bg.InitiateCallsToDartInBgPlugin.CALLBACK_PARAMS;
import static com.dev.run_dart_in_bg.InitiateCallsToDartInBgPlugin.backgroundFlutterEngine;

public class BgService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        long callbackDispatcherHandle = intent.getLongExtra(CALLBACK_DISPATCHER_HANDLE_KEY, 0);

        FlutterCallbackInformation flutterCallbackInformation = FlutterCallbackInformation
                .lookupCallbackInformation(callbackDispatcherHandle);

        // FlutterRunArguments flutterRunArguments = new FlutterRunArguments();
        // flutterRunArguments.bundlePath = FlutterMain.findAppBundlePath();
        // flutterRunArguments.entrypoint = flutterCallbackInformation.callbackName;
        // flutterRunArguments.libraryPath = flutterCallbackInformation.callbackLibraryPath;

        // FlutterNativeView isolate = new FlutterNativeView(this, true);
        // isolate.runFromBundle(flutterRunArguments);

        DartExecutor isolate = backgroundFlutterEngine.getDartExecutor();
        MethodChannel mBackgroundChannel = new MethodChannel(isolate, "background_channel");

        long callbackHandle = intent.getLongExtra(CALLBACK_HANDLE_KEY, 0);
        ArrayList<String> callbackParams = intent.getStringArrayListExtra(CALLBACK_PARAMS);

        final ArrayList<Object> l = new ArrayList<Object>();
        l.add(callbackHandle);
        l.add("Set to run dart function from platform!");
        l.add(callbackParams);

        mBackgroundChannel.invokeMethod("", l);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}