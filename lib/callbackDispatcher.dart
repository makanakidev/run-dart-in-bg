import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

@pragma('vm:entry-point')
void callbackDispatcher() {
  const MethodChannel _backgroundChannel = MethodChannel('background_channel');
  WidgetsFlutterBinding.ensureInitialized();

  _backgroundChannel.setMethodCallHandler((MethodCall call) async {
    final List<dynamic> args = call.arguments;
    print('\n\nMethod arguments from native: $args ...\n'.toUpperCase());
    final function = CallbackHandle.fromRawHandle(args[0]);
    final callback = PluginUtilities.getCallbackFromHandle(function);
    assert(callback != null);
    String s = args[1] as String;
    List params = args[2] ?? [];
    callback?.call(s, params);
  });
}