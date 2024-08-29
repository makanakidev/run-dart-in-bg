import 'dart:async';
import 'dart:ui';
import 'package:flutter/services.dart';
import 'package:initiate_calls_to_dart_in_bg/callbackDispatcher.dart';

class InitiateCalls {
  static const MethodChannel _channel = const MethodChannel('main_channel');

  static Future<void> initialize() async {
    final callback = PluginUtilities.getCallbackHandle(callbackDispatcher);
    if (callback != null) {
      print('\ninitializing callbackDispatcher...\n'.toUpperCase());
      await _channel
          .invokeMethod('initialize', <dynamic>[callback.toRawHandle()]);
    } else
      print('\ninvalid callbackDispatcher!\n'.toUpperCase());
  }

  static void run(void Function(String s, List p) callback,
      [List<String> params = const []]) async {
    final action = PluginUtilities.getCallbackHandle(callback);
    if (action != null) {
      print('\nsending run callback to native environment...\n'.toUpperCase());
      final List<dynamic> args = <dynamic>[action.toRawHandle(), params];
      await _channel.invokeMethod('run', args);
    } else
      print('\nrun callback is invalid!\n'.toUpperCase());
  }
}
