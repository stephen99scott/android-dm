package com.example.directmessaging;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText usr_box = (EditText) findViewById(R.id.username);
        EditText ps_box = (EditText) findViewById(R.id.password);

        // we don't want the edit texts to be the focus when the app starts
        usr_box.clearFocus();
        ps_box.clearFocus();

        Button login_button = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });

        // the password box will listen for an action, ie, hitting enter on the keyboard
        ps_box.setOnEditorActionListener(editorListener);

    }

    private TextView.OnEditorActionListener editorListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
            EditText ps_box = (EditText) findViewById(R.id.password);
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                    closeKeyboard();
                    ps_box.clearFocus();
            }
            return false;
        }
    };

    public void eraseUsrText(View view) {
        EditText usr_box = (EditText) findViewById(R.id.username);
        usr_box.getText().clear();
        usr_box.requestFocus();
    }
    public void erasePsText(View view) {
        EditText ps_box = (EditText) findViewById(R.id.password);
        ps_box.getText().clear();
        ps_box.requestFocus();
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void checkLogin() {

        boolean success = false;

        EditText usr_box = (EditText)findViewById(R.id.username);
        EditText ps_box = (EditText)findViewById(R.id.password);
        usr_box.clearFocus();
        ps_box.clearFocus();

        String username = String.valueOf(usr_box.getText());
        String password = String.valueOf(ps_box.getText());

        System.out.println("username: " + username + " password: " + password);

        success = true;
        if (success) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } //else {
            //invalidLogin();
        //}
    }

    private void invalidLogin() {
        /* TODO check for invalid login, if no username/password combo exists in database then
            make a message pop up that tells them */
        EditText usr_box = (EditText)findViewById(R.id.username);
        EditText ps_box = (EditText)findViewById(R.id.password);

        usr_box.getText().clear();
        ps_box.getText().clear();

        // move cursor back to username
        usr_box.requestFocus();

    }
}