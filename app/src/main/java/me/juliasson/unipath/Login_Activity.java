package me.juliasson.unipath;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

public class Login_Activity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button bvLogin;
    private Button bvSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);


    }
}
