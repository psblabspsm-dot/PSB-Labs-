import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:sentry_flutter/sentry_flutter.dart';

class SentryIntegration {
  static const String _defaultDsn = 'https://examplePublicKey@o0.ingest.sentry.io/0';

  /// Initializes Sentry for the Flutter application and wraps the app runner
  static Future<void> initAndRun(void Function() appRunner, {String? dsn}) async {
    final sentryDsn = dsn ?? const String.fromEnvironment('SENTRY_DSN', defaultValue: _defaultDsn);
    
    if (sentryDsn == _defaultDsn || sentryDsn.isEmpty) {
      debugPrint('[Sentry Sandbox] SENTRY_DSN is unconfigured or set to mock default. Running App without telemetry.');
      appRunner();
      return;
    }

    try {
      await SentryFlutter.init(
        (options) {
          options.dsn = sentryDsn;
          options.tracesSampleRate = 1.0;
          options.environment = kReleaseMode ? 'production' : 'development';
          options.attachScreenshot = true;
          options.attachStacktrace = true;
        },
        appRunner: () {
          // Capture Flutter specific rendering/layout thread exceptions
          FlutterError.onError = (FlutterErrorDetails details) {
            FlutterError.presentError(details);
            Sentry.captureException(
              details.exception,
              stackTrace: details.stack,
            );
          };

          // Capture asynchronous platform-level exception dispatchers
          PlatformDispatcher.instance.onError = (Object error, StackTrace stack) {
            Sentry.captureException(error, stackTrace: stack);
            return true;
          };

          appRunner();
        },
      );
      debugPrint('[Sentry] Flutter Sentry client SDK successfully initialized.');
    } catch (err) {
      debugPrint('[Sentry] Critical failure initializing Sentry SDK: $err. Running main app runner fallback.');
      appRunner();
    }
  }

  /// Manually track errors captured in standard try-catch blocks
  static Future<void> captureException(dynamic exception, {dynamic stackTrace, String? hint}) async {
    if (Sentry.isEnabled) {
      await Sentry.captureException(
        exception,
        stackTrace: stackTrace,
        hint: hint != null ? Hint.withMap({'message': hint}) : null,
      );
    } else {
      debugPrint('[Sentry DryRun] Exception: $exception\nStack: $stackTrace');
    }
  }

  /// Record analytical runtime events or network requests as Breadcrumbs
  static void addBreadcrumb(String message, {String? category, String? level}) {
    if (Sentry.isEnabled) {
      Sentry.addBreadcrumb(
        Breadcrumb(
          message: message,
          category: category,
          level: level == 'error' ? SentryLevel.error : SentryLevel.info,
        ),
      );
    } else {
      debugPrint('[Sentry Breadcrumb] [$category] $message');
    }
  }
}
