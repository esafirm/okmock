import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:sample/http_client.dart';
import 'package:sample/widgets/progress_dialog.dart';

class LoginScreen extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return LoginState();
  }
}

class LoginState extends State<LoginScreen> {
  String _username;
  String _password;

  bool _isRequesting = false;

  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  final GlobalKey<ScaffoldState> _scaffoldKey = GlobalKey<ScaffoldState>();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      key: _scaffoldKey,
      backgroundColor: Colors.white,
      body: Stack(alignment: Alignment.center, children: [
        Container(
          padding: EdgeInsets.all(16),
          child: _buildForm(context),
        ),
        _isRequesting ? ProgressDialog() : Container()
      ]),
    );
  }

  Widget _buildTextField(BuildContext context,
      String key,
      String label,
      void Function(String newValue) onSaved,) {
    return TextFormField(
      key: Key(key),
      decoration: InputDecoration(labelText: label),
      onSaved: onSaved,
    );
  }

  Widget _buildButton(BuildContext context) {
    return Row(
      children: [
        Expanded(
          child: ElevatedButton(
            onPressed: () {
              _handleSubmitted(context);
            },
            child: Text("Login".toUpperCase()),
          ),
        )
      ],
    );
  }

  Form _buildForm(BuildContext context) {
    return Form(
      autovalidateMode: AutovalidateMode.onUserInteraction,
      key: _formKey,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: EdgeInsets.only(top: 32, bottom: 16),
            child: Row(crossAxisAlignment: CrossAxisAlignment.end, children: [
              Icon(Icons.adb, size: 64),
              Text("OkMock",
                  style: TextStyle(fontSize: 22, shadows: [
                    Shadow(
                      color: Colors.black.withOpacity(0.5),
                      offset: Offset(1, 1),
                      blurRadius: 2,
                    ),
                  ]))
            ]),
          ),
          _buildTextField(context, "_email", "Email", (String newValue) {
            _username = newValue;
          }),
          SizedBox(height: 16),
          _buildTextField(context, "_password", "Password", (String newValue) {
            _password = newValue;
          }),
          SizedBox(height: 16),
          _buildButton(context)
        ],
      ),
    );
  }

  void _setRequesting(bool isRequesting) {
    setState(() {
      _isRequesting = isRequesting;
    });
  }

  void _showSnackBar(String message) {
    _scaffoldKey.currentState.showSnackBar(SnackBar(content: Text(message)));
  }

  void _handleSubmitted(BuildContext context) async {
    _setRequesting(true);

    final FormState form = _formKey.currentState;
    if (!form.validate()) {
      _showSnackBar('Please fix the errors in red before submitting.');
    } else {
      form.save();

      final client = HttpClient();
      final res = await client.requestLogin(_username, _password);

      if (res.statusCode == 200) {
        Navigator.pushNamedAndRemoveUntil(
            context, "/home", ModalRoute.withName("/home"));
      } else {
        _showSnackBar("The API request is failed! Please check your data");
      }
    }

    _setRequesting(false);
  }
}
