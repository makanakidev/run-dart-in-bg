import 'dart:async';
import 'dart:ui';
import 'package:flutter/services.dart';
import 'package:initiate_calls_to_dart_in_bg/callbackDispatcher.dart';

class InitiateCalls {
  static const MethodChannel _channel = const MethodChannel('main_channel');

  static Future<void> initialize() async {
    final callback = PluginUtilities.getCallbackHandle(callbackDispatcher);
    if (callback != null) {
      print('Initializing callbackDispatcher...');
      await _channel
          .invokeMethod('initialize', <dynamic>[callback.toRawHandle()]);
    } else
      print('Invalid callbackDispatcher!');
  }

  static void run(void Function(String s, List<String> p) callback,
      [List<String> params = []]) async {
    final action = PluginUtilities.getCallbackHandle(callback);
    if (action != null) {
      print('Sending run callback to native environment...');
      final List<dynamic> args = <dynamic>[action.toRawHandle(), params];
      await _channel.invokeMethod('run', args);
    } else
      print('run callback is invalid!');
  }
}
