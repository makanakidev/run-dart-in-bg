import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

@pragma('vm:entry-point')
void callbackDispatcher() {
  const MethodChannel _backgroundChannel = MethodChannel('background_channel');
  WidgetsFlutterBinding.ensureInitialized();
  print('Initializing callbackDispatcher...');

  _backgroundChannel.setMethodCallHandler((MethodCall call) async {
      final List<dynamic> args = call.arguments;
      final Function callbackThis = PluginUtilities.getCallbackFromHandle(
          CallbackHandle.fromRawHandle(args[0]));
      assert(callbackThis != null);
      String s = args[1] as String;
      callbackThis(s);
  });
}
