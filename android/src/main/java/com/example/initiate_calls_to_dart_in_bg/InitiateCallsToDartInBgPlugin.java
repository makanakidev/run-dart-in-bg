package com.example.initiate_calls_to_dart_in_bg;

import android.content.Context;
import android.content.Intent;
import io.flutter.embedding.engine.FlutterEngine;
import java.util.ArrayList;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.PluginRegistrantCallback;
import io.flutter.embedding.engine.plugins.shim.ShimPluginRegistry;

public class InitiateCallsToDartInBgPlugin implements FlutterPlugin, MethodCallHandler, PluginRegistrantCallback {

    public static final String CALLBACK_HANDLE_KEY = "callback_handle_key";
    public static final String CALLBACK_DISPATCHER_HANDLE_KEY = "callback_dispatcher_handle_key";
    public static final String CALLBACK_PARAMS = "callback_params";

    private MethodChannel channel;
    private Context mContext;
    private long mCallbackDispatcherHandle;
    private static PluginRegistrantCallback pluginRegistrantCallback;
    private FlutterEngine backgroundFlutterEngine;

    @Override
    public void onAttachedToEngine(FlutterPluginBinding binding) {
        channel = new MethodChannel(binding.getBinaryMessenger(), "main_channel");
        channel.setMethodCallHandler(this);
        mContext = binding.getApplicationContext();
        backgroundFlutterEngine = new FlutterEngine(mContext);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pluginRegistrantCallback = this;
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {

        if (call.method.equals("initialize")) {
            ArrayList args = call.arguments();
            long callBackHandle = (long) args.get(0);
            mCallbackDispatcherHandle = callBackHandle;

            // The pluginRegistrantCallback should only be set in the V1 embedding as
            // plugin registration is done via reflection in the V2 embedding.
            if (pluginRegistrantCallback != null) {
                pluginRegistrantCallback.registerWith(new ShimPluginRegistry(backgroundFlutterEngine));
            }

            result.success(null);
            return;
        } else if (call.method.equals("run")) {
            ArrayList args = call.arguments();
            long callbackHandle = (long) args.get(0);
            ArrayList<String> params = (ArrayList<String>) args.get(1); // parameters sent back to dart function

            Intent i = new Intent(mContext, MyService.class);

            i.putExtra(CALLBACK_HANDLE_KEY, callbackHandle);
            i.putExtra(CALLBACK_DISPATCHER_HANDLE_KEY, mCallbackDispatcherHandle);
            i.putStringArrayListExtra(CALLBACK_PARAMS, params);

            mContext.startService(i);

            result.success(null);
            return;
        }
        result.notImplemented();
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }
}