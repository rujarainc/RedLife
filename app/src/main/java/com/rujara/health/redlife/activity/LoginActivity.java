package com.rujara.health.redlife.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.rujara.health.redlife.R;

public class LoginActivity extends AppCompatActivity {

    private EditText _emailText = null;
    private EditText _passwordText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void goToSignup(View view) {
        Intent signup = new Intent(this, SignupActivity.class);
        startActivity(signup);
    }

    public void onLogin(View view) {
       /* final ProgressDialog ringProgressDialog = ProgressDialog.show(LoginActivity.this, null,	"Authenticating ...", true);
        ringProgressDialog.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {

                }
                ringProgressDialog.dismiss();
            }
        }).start(); */
        _emailText = (EditText) findViewById(R.id.input_email);
        _emailText.setError("Invalid Email!");
    }
}
