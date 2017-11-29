package com.uca.apps.isi.taken.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.tumblr.remember.Remember;
import com.uca.apps.isi.taken.MainActivity;
import com.uca.apps.isi.taken.R;
import com.uca.apps.isi.taken.api.Api;
import com.uca.apps.isi.taken.models.AccessToken;
import com.uca.apps.isi.taken.models.User;
import com.uca.apps.isi.taken.models.UserCreate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private EditText realm;
    private EditText username;
    private EditText email;
    private EditText password;
    private Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initViews();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * Initialize the views
     */

    private void initViews() {
        realm = (EditText) findViewById(R.id.nameSignUp);
        username = (EditText) findViewById(R.id.userNameSignUp);
        email = (EditText) findViewById(R.id.emailSignUp);
        password = (EditText) findViewById(R.id.passwordSignUp);
        signUp = (Button) findViewById(R.id.signUpButton);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signUp();
            }
        });

    }

    /**
     * Verify that the fields are not empty and create a new user.
     */

    private void signUp() {
        if (realm.getText().toString().isEmpty() || username.getText().toString().isEmpty() ||  email.getText().toString().isEmpty() || password.getText().toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(),getString(R.string.activity_sing_up_empty_fields_message), Toast.LENGTH_LONG).show();
        }
        else
        {
            // instance a user
            UserCreate user = new UserCreate();
            user.setUsername(username.getText().toString());
            user.setRealm(realm.getText().toString());
            user.setEmail(email.getText().toString());
            user.setPassword(password.getText().toString());

            // create call
            Call<User> call = Api.instance().signUp(user);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.body() != null) {
                        loginRequest(email.getText().toString(), password.getText().toString());
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });
        }
    }

    /**
     * Request the new user's access
     */

    private void loginRequest(String email, String password) {
        // instance a user
        User user = new User();

        user.setEmail(email);
        user.setPassword(password);
        user.setTtl(31556926);

        // create call
        Call<AccessToken> call = Api.instance().login(user);
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                if (response.body() != null) {

                    try {
                        Remember.putString("access_token", response.body().getId(), new Remember.Callback() {
                            @Override
                            public void apply(Boolean success) {
                                if (success) {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }catch (Exception e){
                        System.out.println(getString(R.string.error_message)+e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {

            }
        });
    }
}

