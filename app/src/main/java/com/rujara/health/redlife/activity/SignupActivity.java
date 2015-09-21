package com.rujara.health.redlife.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.rujara.health.redlife.R;
import com.rujara.health.redlife.networks.INetworkListener;
import com.rujara.health.redlife.networks.NetworkInspector;

import java.util.Calendar;

/**
 * Created by deep.patel on 9/18/15.
 */
public class SignupActivity extends AppCompatActivity implements INetworkListener {

    Snackbar snackbar = null;
    private Toolbar toolbar;
    private EditText inputName, inputEmail, inputPassword;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword;
    private Button btnSignUp;
    private EditText dob;
    private NetworkInspector networkInspector = null;
    private View myView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        myView = findViewById(R.id.signup_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        networkInspector = new NetworkInspector(this, this);
        //setSupportActionBar(toolbar);

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputName = (EditText) findViewById(R.id.input_name);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        dob = (EditText) findViewById(R.id.input_dob);
        btnSignUp = (Button) findViewById(R.id.btn_signup);

        dob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    onSelectDate();
            }
        });
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectDate();
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //submitForm();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
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

    public void onSelectDate(){
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(SignupActivity.this,
                new mDateSetListener(), mYear, mMonth, mDay);
        dialog.show();
    }

    public void goToLogin(View view) {
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
    }

    @Override
    public void onNetworkConnected() {
        if (snackbar != null)
            snackbar.dismiss();
    }

    @Override
    public void onNetWorkConnectionFail() {
        if (snackbar == null) {
            snackbar = Snackbar.make(myView, "Data Connection Lost..", Snackbar.LENGTH_INDEFINITE);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(getResources().getColor(R.color.windowBackground));
        }

        snackbar.show();
    }

    protected void onResume() {
        super.onResume();
        networkInspector.start();
    }

    protected void onPause() {
        super.onPause();
        networkInspector.stop();
    }

    class mDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            int mYear = year;
            int mMonth = monthOfYear;
            int mDay = dayOfMonth;
            dob.setText(new StringBuilder()
                    .append(mDay).append("/").append(mMonth + 1).append("/")
                    .append(mYear).append(" "));
        }
    }

}
