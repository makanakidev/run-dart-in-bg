import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

@pragma('vm:entry-point')
void callbackDispatcher() {
  const MethodChannel _backgroundChannel = MethodChannel('background_channel');

  _backgroundChannel.setMethodCallHandler((MethodCall call) async {
    WidgetsFlutterBinding.ensureInitialized();
    DartPluginRegistrant.ensureInitialized();

    final List<dynamic> args = call.arguments;

    debugPrint('\nData from platform:'.toUpperCase());
    debugPrint('$args');
    debugPrint('\n');

    final function = CallbackHandle.fromRawHandle(args[0]);
    final callback = PluginUtilities.getCallbackFromHandle(function);

    assert(callback != null);

    String msg = args[1] as String;
    List params = args[2] ?? [];

    callback?.call(msg, params);
  });
}
