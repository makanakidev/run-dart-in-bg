import 'dart:async';
import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:initiate_calls_to_dart_in_bg/callbackDispatcher.dart';

class InitiateCalls {
  static const MethodChannel _channel = const MethodChannel('main_channel');

  static Future<void> initialize() async {
    final callback = PluginUtilities.getCallbackHandle(callbackDispatcher);

    if (callback != null) {
      debugPrint('\ninitializing BgService...\n'.toUpperCase());
      await _channel
          .invokeMethod('initialize', <dynamic>[callback.toRawHandle()]);
    } else
      debugPrint('\ninvalid callback dispatcher!\n'.toUpperCase());
  }

  static void run(void Function(String s, List p) callback,
      [List<String> params = const []]) async {
    final action = PluginUtilities.getCallbackHandle(callback);

    if (action != null) {
      debugPrint('\nsending dart function to platform...\n'.toUpperCase());

      final List<dynamic> args = <dynamic>[action.toRawHandle(), params];

      await _channel.invokeMethod('run', args);
    } else
      debugPrint('\ninvalid dart function!\n'.toUpperCase());
  }
}
