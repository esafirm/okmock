import 'package:flutter_test/flutter_test.dart';
import 'package:okmock/okmock_payload.dart' as OkMock;

void main() {
  test('parsing should be correct', () {
    // create map with key and value
    final matcherJson = new Map<String, Object>();
    matcherJson["path"] = "google.com";
    matcherJson["method"] = "GET";

    final matcher = OkMock.Matcher.fromJson(matcherJson);

    assert(matcher.path.hasMatch("google.com"));
    assert(matcher.method == matcherJson["method"]);
  });
}
