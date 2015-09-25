package com.rujara.health.redlife.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.rujara.health.redlife.R;
import com.rujara.health.redlife.constants.RedLifeContants;
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

public class LoginActivity extends AppCompatActivity implements INetworkListener {

    Snackbar snackbar = null;
    GoogleCloudMessaging gcm = null;
    private EditText _emailText = null;
    private EditText _passwordText = null;
    private Button _loginButton = null;
    private NetworkInspector networkInspector = null;
    private View myView = null;
    private SessionManager sessionManager = null;
    private JSONObject data = new JSONObject();
    private ProgressDialog progressDialog = null;
    private UserDetails userDetails = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myView = findViewById(R.id.login_view);
        sessionManager = new SessionManager(getApplicationContext());
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        networkInspector = new NetworkInspector(this, this);
        userDetails = UserDetails.getInstance();
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
        View view2 = this.getCurrentFocus();
        if (view2 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if (!validate())
            return;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        try {
            data.put(RedLifeContants.EMAILID, email);
            data.put(RedLifeContants.PASSWORD, password);
            new EndpointCommunicationTask().execute(RedLifeContants.AUTHENTICATION);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }


        return valid;
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

    private class EndpointCommunicationTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(LoginActivity.this, null, "Authenticating ...", true);
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject response = null;
            try {
                InputStream inputStream = null;
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(args[0]);
                String json = "";
                json = data.toString();
                Log.v("[rujara]", "Req: " + json);
                StringEntity se = new StringEntity(json);
                httpPost.setEntity(se);
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                HttpResponse httpResponse = httpclient.execute(httpPost);
                inputStream = httpResponse.getEntity().getContent();
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
                    new RegisterGcmTask().execute();
                    JSONObject data = response.getJSONObject("data");
                    if (data.has(RedLifeContants.NAME))
                        userDetails.setName(data.getString(RedLifeContants.NAME));
                    if (data.has(RedLifeContants.EMAILID))
                        userDetails.setEmailId(data.getString(RedLifeContants.EMAILID));
                    if (data.has(RedLifeContants.PHONE_NUMBER))
                        userDetails.setPhoneNumber(data.getString(RedLifeContants.PHONE_NUMBER));
                    if (data.has(RedLifeContants.DOB))
                        userDetails.setDob(data.getString(RedLifeContants.DOB));
                    if (data.has(RedLifeContants.EVENT_REGISTRATION_ID))
                        userDetails.setEventRegistrationId(data.getString(RedLifeContants.EVENT_REGISTRATION_ID));
                    if (data.has(RedLifeContants.BLOOD_GROUP))
                        userDetails.setBloodGroup(data.getString(RedLifeContants.BLOOD_GROUP));
                    if (data.has(RedLifeContants.SERVER_AUTH_TOKEN))
                        userDetails.setServerAuthToken(data.getString(RedLifeContants.SERVER_AUTH_TOKEN));
                    progressDialog.dismiss();
                    System.out.println("LOGIN TOKEN:" + userDetails.getServerAuthToken());
                    sessionManager.createLoginSession(userDetails.getEmailId(), userDetails.getServerAuthToken());
                    Intent dashboard = new Intent(LoginActivity.this, Dashboard.class);
                    startActivity(dashboard);
                } else if (response.has("status") && response.getInt("status") == 1) {
                    progressDialog.dismiss();
                    Snackbar snackbar = Snackbar.make(myView, "Authentication failed", Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(getResources().getColor(R.color.windowBackground));
                    snackbar.show();
                } else {
                    progressDialog.dismiss();
                    Snackbar snackbar = Snackbar.make(myView, "Something went wrong", Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(getResources().getColor(R.color.windowBackground));
                    snackbar.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class RegisterGcmTask extends AsyncTask<Void, Void, String> {
        String msg = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                String email = data.getString(RedLifeContants.EMAILID);
                data = new JSONObject();
                data.put(RedLifeContants.EVENT_REGISTRATION_ID, msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new NotifyEventRegIdTask().execute(RedLifeContants.NOTIFY + "/" + userDetails.getServerAuthToken());
        }
    }

    private class NotifyEventRegIdTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject response = null;
            try {
                InputStream inputStream = null;
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(args[0]);
                String json = "";
                json = data.toString();
                Log.v("[rujara]", "Req: " + json);
                StringEntity se = new StringEntity(json);
                httpPost.setEntity(se);
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                HttpResponse httpResponse = httpclient.execute(httpPost);
                inputStream = httpResponse.getEntity().getContent();
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

            finish();
        }
    }
}
