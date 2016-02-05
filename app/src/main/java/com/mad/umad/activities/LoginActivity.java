package com.mad.umad.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.mad.umad.R;

/**
 * Created by Drew on 1/18/16.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button login;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setViewsVisibility(View.INVISIBLE);

        goToMain();

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseLogin();
            }
        });
    }

    private void parseLogin() {
        ParseUser.logInInBackground(email.getText().toString().toLowerCase(), password.getText().toString(), new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                    goToMain();
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    Toast.makeText(LoginActivity.this, "Login failed, please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setViewsVisibility(int visibility) {
        findViewById(R.id.emailWrapper).setVisibility(visibility);
        findViewById(R.id.passwordWrapper).setVisibility(visibility);
        findViewById(R.id.login_button).setVisibility(visibility);
    }

    private void goToMain() {
        if(ParseUser.getCurrentUser() != null) {
            ifAcceptedAllowToMain();
        } else {
            setViewsVisibility(View.VISIBLE);
        }
    }

    private void ifAcceptedAllowToMain() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UMAD_Application");
        query.include("pointer_field");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if(e == null) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("UMAD_Application_Status");
                    query.include("pointer_field");
                    query.whereEqualTo("application", parseObject);
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            if (e == null) {
                                try {
                                    if(parseObject.fetchIfNeeded().getString("status").equals("Confirmed")) {
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    } else {
                                        notAcceptedToMain("Your uMAD Application is pending!");
                                    }
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                if (e.getCode() == 101) {
                                    notAcceptedToMain("Login failed, you have no application for uMAD!");
                                } else {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } else {
                    if (e.getCode() == 101) {
                        notAcceptedToMain("Login failed, you have no application for uMAD!");
                    } else {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void notAcceptedToMain(String message) {
        setViewsVisibility(View.VISIBLE);
        Toast.makeText(this, "Login failed, you have no application for uMAD!", Toast.LENGTH_SHORT).show();
    }
}
