import 'package:flutter/material.dart';
import 'package:sample/home_screen.dart';
import 'package:sample/http_request.dart';
import 'package:sample/login_screen.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'OkMock Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      routes: {
        "/": (context) => LoginScreen(),
        "/home": (context) => HomeScreen()
      },
    );
  }
}
