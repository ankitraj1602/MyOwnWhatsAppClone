package com.example.myownwhatsappclone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtUsernameSignUp,
            edtEmailSignUp,edtPassSignUp,edtUsernameLogIn,edtPassLogIn;
    private Button btnSignUp,btnLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ParseUser.getCurrentUser()!=null){
            //ParseUser.getCurrentUser().logOut();
            Intent loggedInUser = new Intent(this, loggedInUser.class);
            startActivity(loggedInUser);
            finish();
        }

        edtUsernameSignUp = findViewById(R.id.edtUsernameSignup);
        edtPassSignUp = findViewById(R.id.edtPassSignUp);
        edtEmailSignUp = findViewById(R.id.edtEmailSignUp);
        edtUsernameLogIn = findViewById(R.id.edtUsernameLogIn);
        edtPassLogIn = findViewById(R.id.edtPassLogIn);



        btnLogIn = findViewById(R.id.btnLogIn);
        btnLogIn.setOnClickListener(this);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(this);

        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSignUp:
                String usSignUp = edtUsernameSignUp.getText().toString();
                String passSignUp = edtPassSignUp.getText().toString();
                String emailSignUp = edtEmailSignUp.getText().toString();

                if(emailSignUp.equals("")){
                    Toast.makeText(this, "Please specify a email!", Toast.LENGTH_SHORT).show();
                }

                else{
                    //Toast.makeText(this, "Credentials entered", Toast.LENGTH_SHORT).show();
                    ParseUser newUser = new ParseUser();
                    newUser.setPassword(passSignUp);
                    newUser.setUsername(usSignUp);
                    newUser.setEmail(emailSignUp);

                    final ProgressDialog dialog = new ProgressDialog(this);
                    dialog.setMessage("SIGNING UP....");
                    dialog.show();

                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null){
                                Toast.makeText(MainActivity.this, "Signed Up!", Toast.LENGTH_SHORT).show();
                                Intent loggedInUser = new Intent(MainActivity.this, loggedInUser.class);
                                startActivity(loggedInUser);
                                finish();
                            }
                            else{
                                Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    });


                }
                break;

            case R.id.btnLogIn:

                String usernameLogIn = edtUsernameLogIn.getText().toString();
                String passWordLogIn = edtPassLogIn.getText().toString();

                ParseUser.logInInBackground(usernameLogIn, passWordLogIn, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if(user!=null && e==null){
                            Toast.makeText(MainActivity.this, "Logged In!", Toast.LENGTH_SHORT).show();
                            Intent loggedInUser = new Intent(MainActivity.this, loggedInUser.class);
                            startActivity(loggedInUser);
                            finish();
                        }
                        else{
                            Toast.makeText(MainActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                break;
        }
    }
}
