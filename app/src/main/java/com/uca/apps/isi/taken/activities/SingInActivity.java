package com.uca.apps.isi.taken.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tumblr.remember.Remember;
import com.uca.apps.isi.taken.MainActivity;
import com.uca.apps.isi.taken.R;
import com.uca.apps.isi.taken.api.Api;
import com.uca.apps.isi.taken.models.AccessToken;
import com.uca.apps.isi.taken.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingInActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in);

        initViews();
    }
    private void initViews() {
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        Button signIn = (Button) findViewById(R.id.sign_in);
        Button signUp = (Button) findViewById(R.id.sign_up);
        progressBar = (ProgressBar) findViewById(R.id.signInProgressBar);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * This method is for login user
     */
    private void signIn() {
        if (email.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(),R.string.activity_sing_in_message_empty_email, Toast.LENGTH_LONG).show();
        } else if(password.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.activity_sing_in_message_empty_password, Toast.LENGTH_LONG).show();
        } else {
            loginRequest(email.getText().toString(), password.getText().toString());
        }
    }

    /**
     *
     * @param email
     * @param password
     * To make http request
     */
    private void loginRequest(String email, String password) {
        // instance a user
        progressBar.setVisibility(View.VISIBLE);
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setTtl(31556926);

        // create call
        Call<AccessToken> call = Api.instance().login(user);
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {
                if (response.body() != null) {

                    try {
                        Remember.putString("access_token", response.body().getId(), new Remember.Callback() {
                            @Override
                            public void apply(Boolean success) {
                                if (success) {
                                    progressBar.setVisibility(View.GONE);
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);

                Toast.makeText(getApplicationContext(),getString(R.string.message_without_connection),Toast.LENGTH_SHORT).show();
            }
        });
    }


}
