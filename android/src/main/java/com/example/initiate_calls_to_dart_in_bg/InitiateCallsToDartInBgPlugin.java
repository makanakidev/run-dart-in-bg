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
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.embedding.engine.plugins.shim.ShimPluginRegistry;

public class InitiateCallsToDartInBgPlugin implements FlutterPlugin, MethodCallHandler {

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


    // This static function is optional and equivalent to onAttachedToEngine. It supports the old
    // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
    // plugin registration via this function while apps migrate to use the new Android APIs
    // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
    //
    // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
    // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
    // depending on the user's project. onAttachedToEngine or registerWith must both be defined
    // in the same class. 
    public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "main_channel");
    channel.setMethodCallHandler(new InitiateCallsToDartInBgPlugin());
     // The pluginRegistrantCallback should only be set in the V1 embedding as
     // plugin registration is done via reflection in the V2 embedding.
        if (pluginRegistrantCallback != null) {
             pluginRegistrantCallback.registerWith(new ShimPluginRegistry(backgroundFlutterEngine));
          }
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {

        if (call.method.equals("initialize")) {
            ArrayList args = call.arguments();
            long callBackHandle = (long) args.get(0);
            mCallbackDispatcherHandle = callBackHandle;

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
