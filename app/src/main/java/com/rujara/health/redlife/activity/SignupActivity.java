package com.rujara.health.redlife.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.rujara.health.redlife.R;
import com.rujara.health.redlife.constants.RedLifeContants;
import com.rujara.health.redlife.networks.Communicator;
import com.rujara.health.redlife.networks.IAsyncTask;
import com.rujara.health.redlife.networks.INetworkListener;
import com.rujara.health.redlife.networks.NetworkInspector;
import com.rujara.health.redlife.store.UserDetails;
import com.rujara.health.redlife.utils.AppUtils;
import com.rujara.health.redlife.utils.SessionManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

/**
 * Created by deep.patel on 9/18/15.
 */
public class SignupActivity extends AppCompatActivity implements INetworkListener, IAsyncTask {

    Snackbar snackbar = null;
    ProgressDialog progressDialog = null;
    JSONObject data = new JSONObject();
    SessionManager sessionManager = null;
    GoogleCloudMessaging gcm = null;
    Communicator communicator = new Communicator(this);
    private Toolbar toolbar;
    private EditText inputName, inputEmail, inputPassword, inputPhoneNo;
    private Button btnSignUp;
    private EditText dob, bg;
    private NetworkInspector networkInspector = null;
    private View myView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        myView = findViewById(R.id.signup_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        networkInspector = new NetworkInspector(this, this);
        sessionManager = new SessionManager(getApplicationContext());
        inputName = (EditText) findViewById(R.id.input_name);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        inputPhoneNo = (EditText) findViewById(R.id.input_phone);
        dob = (EditText) findViewById(R.id.input_dob);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        bg = (EditText) findViewById(R.id.input_bg);


        dob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    onSelectDate();
            }
        });
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectDate();
            }
        });

        bg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    onSelectBG();
            }
        });
        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectBG();
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

    public void onSelectDate() {
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(SignupActivity.this,
                new mDateSetListener(), mYear, mMonth, mDay);
        dialog.show();
    }

    public void onSelectBG() {
        final Dialog d = new Dialog(SignupActivity.this);
        final String[] bloodGroups = new String[]{"A+ve", "A-ve", "B+ve", "B-ve", "AB+ve", "AB-ve", "O+ve", "O-ve"};
        d.setTitle("Blood Groups");
        d.setContentView(R.layout.bloodgrouppicker);
        Button b1 = (Button) d.findViewById(R.id.button1);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMinValue(0);
        np.setMaxValue(7);
        np.setDisplayedValues(bloodGroups);
        // min value 0
        np.setFormatter(new BloodgroupFormatter());
        np.setWrapSelectorWheel(false);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bg.setText(bloodGroups[np.getValue()]); //set the value to textview
                d.dismiss();
            }
        });
        d.show();
    }

    public void goToLogin(View view) {
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
    }

    public void onSignup(View view) {

        View view2 = this.getCurrentFocus();
        if (view2 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if (!validate()) {
            return;
        }
        String name = inputName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String phoneNo = inputPhoneNo.getText().toString();
        String dobString = dob.getText().toString();
        String bloodGroup = bg.getText().toString().toLowerCase();
        try {
            data.put(RedLifeContants.NAME, name);
            data.put(RedLifeContants.EMAILID, email);
            data.put(RedLifeContants.PASSWORD, password);
            data.put(RedLifeContants.PHONE_NUMBER, phoneNo);
            data.put(RedLifeContants.DOB, dobString);
            data.put(RedLifeContants.BLOOD_GROUP, bloodGroup);
            new RegisterGcmTask().execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    public boolean validate() {
        boolean valid = true;

        String name = inputName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String phoneNo = inputPhoneNo.getText().toString();
        String dobString = dob.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Enter a valid email address");
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            inputPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            inputPassword.setError(null);
        }
        if (name.isEmpty()) {
            inputName.setError("Name cannot be empty");
            valid = false;
        } else {
            inputName.setError(null);
        }

        if (phoneNo.isEmpty()) {
            inputPhoneNo.setError("Phone no cannot be empty");
            valid = false;
        } else {
            inputPhoneNo.setError(null);
        }

        if (dobString.isEmpty()) {
            dob.setError("DOB cannot be empty");
            valid = false;
        } else {
            dob.setError(null);
        }

        return valid;
    }

    protected void onResume() {
        super.onResume();
        networkInspector.start();
    }

    protected void onPause() {
        super.onPause();
        networkInspector.stop();
    }

    @Override
    public void onPreExecute(int taskId) {

    }

    @Override
    public void onPostExecute(int taskId, JSONObject response) {
        String smsCode = null;
        try {
            if (response.has("status") && response.getInt("status") == 0) {
                smsCode = response.getString("message");
                UserDetails userDetails = UserDetails.getInstance();
                JSONObject responseData = response.getJSONObject("data");
                if (responseData.has(RedLifeContants.NAME))
                    userDetails.setName(responseData.getString(RedLifeContants.NAME));
                if (responseData.has(RedLifeContants.EMAILID))
                    userDetails.setEmailId(responseData.getString(RedLifeContants.EMAILID));
                if (responseData.has(RedLifeContants.PHONE_NUMBER))
                    userDetails.setPhoneNumber(responseData.getString(RedLifeContants.PHONE_NUMBER));
                if (responseData.has(RedLifeContants.DOB))
                    userDetails.setDob(responseData.getString(RedLifeContants.DOB));
                if (responseData.has(RedLifeContants.EVENT_REGISTRATION_ID))
                    userDetails.setEventRegistrationId(responseData.getString(RedLifeContants.EVENT_REGISTRATION_ID));
                if (responseData.has(RedLifeContants.BLOOD_GROUP))
                    userDetails.setBloodGroup(responseData.getString(RedLifeContants.BLOOD_GROUP));
                if (responseData.has(RedLifeContants.SERVER_AUTH_TOKEN))
                    userDetails.setServerAuthToken(responseData.getString(RedLifeContants.SERVER_AUTH_TOKEN));
                progressDialog.dismiss();
                sessionManager.createLoginSession(userDetails.getEmailId(), userDetails.getServerAuthToken());
                Intent smsVerification = new Intent(SignupActivity.this, SMSVerification.class);
                smsVerification.putExtra("smsCode", smsCode);
                smsVerification.putExtra(RedLifeContants.PHONE_NUMBER, userDetails.getPhoneNumber());
                startActivity(smsVerification);
                finish();
            } else if (response.has("status") && response.getInt("status") == 1) {
                Snackbar snackbar = Snackbar.make(myView, "Signup failed", Snackbar.LENGTH_LONG);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(getResources().getColor(R.color.windowBackground));
            } else {
                Snackbar snackbar = Snackbar.make(myView, "Something went wrong", Snackbar.LENGTH_LONG);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(getResources().getColor(R.color.windowBackground));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private class RegisterGcmTask extends AsyncTask<Void, Void, String> {
        String msg = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(SignupActivity.this, null, "Registering ...", true);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                msg = gcm.register("398373931836");

            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            Log.v("[rujara]", "Response: " + msg);
            try {
                data.put(RedLifeContants.EVENT_REGISTRATION_ID, msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            communicator.communicate(1, RedLifeContants.SIGNUP, data);
//            new EndpointCommunicationTask().execute(RedLifeContants.SIGNUP);
        }
    }

    /*private class EndpointCommunicationTask extends AsyncTask<String, Void, JSONObject> {
        private String eventRegId = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject response = null;
            try {
                InputStream inputStream = null;
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();

                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(args[0]);

                String json = "";
                // 4. convert JSONObject to JSON to String
                json = data.toString();
                // ** Alternative way to convert Person object to JSON string usin Jackson Lib
                // ObjectMapper mapper = new ObjectMapper();
                // json = mapper.writeValueAsString(person);
                Log.v("[rujara]", "Req: " + json);
                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json);

                // 6. set httpPost Entity
                httpPost.setEntity(se);

                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);

                // 9. receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();

                // 10. convert inputstream to string
                if (inputStream != null)
                    response = new AppUtils().convertInputStreamToJson(inputStream);

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("InputStream", e.getLocalizedMessage());
            }

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            Log.v("[rujara]", "Response: " + response);
            try {
                if (response.has("status") && response.getInt("status") == 0) {
                    UserDetails userDetails = UserDetails.getInstance();
                    JSONObject data = response.getJSONObject("data");
                    if (data.has(RedLifeContants.NAME))
                        userDetails.setName(data.getString(RedLifeContants.NAME));
                    if (data.has(RedLifeContants.EMAILID))
                        userDetails.setName(data.getString(RedLifeContants.EMAILID));
                    if (data.has(RedLifeContants.PHONE_NUMBER))
                        userDetails.setName(data.getString(RedLifeContants.PHONE_NUMBER));
                    if (data.has(RedLifeContants.DOB))
                        userDetails.setName(data.getString(RedLifeContants.DOB));
                    if (data.has(RedLifeContants.EVENT_REGISTRATION_ID))
                        userDetails.setName(data.getString(RedLifeContants.EVENT_REGISTRATION_ID));
                    if (data.has(RedLifeContants.BLOOD_GROUP))
                        userDetails.setName(data.getString(RedLifeContants.BLOOD_GROUP));
                    if (data.has(RedLifeContants.SERVER_AUTH_TOKEN))
                        userDetails.setName(data.getString(RedLifeContants.SERVER_AUTH_TOKEN));
                    progressDialog.dismiss();
                    sessionManager.createLoginSession(userDetails.getEmailId(), userDetails.getServerAuthToken());
                    Intent dashboard = new Intent(SignupActivity.this, Dashboard.class);
                    startActivity(dashboard);
                    finish();
                } else if (response.has("status") && response.getInt("status") == 1) {
                    Snackbar snackbar = Snackbar.make(myView, "Authentication failed", Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(getResources().getColor(R.color.windowBackground));
                } else {
                    Snackbar snackbar = Snackbar.make(myView, "Something went wrong", Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(getResources().getColor(R.color.windowBackground));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

    private class BloodgroupFormatter implements NumberPicker.Formatter {
        public String toString(int value) {
            switch (value) {
                case 0:
                    return "England";
                case 1:
                    return "France";
            }
            return "Unknown";
        }

        @Override
        public String format(int value) {
            switch (value) {
                case 0:
                    return "England";
                case 1:
                    return "France";
            }
            return "Unknown";
        }
    }
}
