package com.example.t_tsuchida.cameraintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.t_tsuchida.cameraintent.R;
import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiUserCallBack;

public class LoginActivity extends Activity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private TextView mUsernameField;
    private TextView mPasswordField;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mUsernameField = (EditText) findViewById(R.id.username_field);
        mPasswordField = (EditText) findViewById(R.id.password_field);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Kii.initialize(AppConstants.APP_ID, AppConstants.APP_KEY,
                AppConstants.APP_SITE);

        // Specify EditText type now, due to control font size of EditText hint.
        EditText editPassword = (EditText) this
                .findViewById(R.id.password_field);
        editPassword
                .setTransformationMethod(new PasswordTransformationMethod());
        editPassword.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);

    }

    public void onLoginButtonClicked(View v) {
        // Hide soft keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        progressBar.setVisibility(View.VISIBLE);
        Log.v(TAG, "login clicked");
        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();
        try {
            KiiUser.logIn(callback, username, password);
        } catch (Exception e) {
            progressBar.setVisibility(View.INVISIBLE);
            showToast("Error : " + e.getLocalizedMessage());
        }
    }

    public void onSignupButtonClicked(View v) {
        // Hide soft keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        progressBar.setVisibility(View.VISIBLE);

        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();
        try {
            KiiUser user = KiiUser.createWithUsername(username);
            user.register(callback, password);
        } catch (Exception e) {
            progressBar.setVisibility(View.INVISIBLE);
            showToast("Error : " + e.getLocalizedMessage());
        }
    }

    KiiUserCallBack callback = new KiiUserCallBack() {
        @Override
        public void onLoginCompleted(int token, KiiUser user, Exception e) {
            progressBar.setVisibility(View.INVISIBLE);
            if (e == null) {
                showToast("User logged-in!");
                Intent i = new Intent(LoginActivity.this,
                        MainActivity.class);
                startActivity(i);
            } else {
                showToast("Error : " + e.getLocalizedMessage());
            }
        }

        @Override
        public void onRegisterCompleted(int token, KiiUser user, Exception e) {
            progressBar.setVisibility(View.INVISIBLE);
            if (e == null) {
                showToast("User registered!");
                Intent i = new Intent(LoginActivity.this,
                        MainActivity.class);
                startActivity(i);
            } else {
                showToast("Error : " + e.getLocalizedMessage());
            }
        }
    };

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
