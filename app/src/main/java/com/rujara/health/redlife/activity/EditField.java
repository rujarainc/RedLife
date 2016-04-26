package com.rujara.health.redlife.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import com.rujara.health.redlife.R;
import com.rujara.health.redlife.constants.RedLifeContants;
import com.rujara.health.redlife.networks.Communicator;
import com.rujara.health.redlife.networks.IAsyncTask;
import com.rujara.health.redlife.utils.SessionManager;
import org.json.JSONObject;

public class EditField extends AppCompatActivity implements IAsyncTask {
    private EditText editField;
    private Communicator communicator;
    private String field, currentValue;
    private JSONObject jsonObject;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_field);
        progressDialog = new ProgressDialog(this);
        editField = (EditText) findViewById(R.id.editField);
        String fieldName = getIntent().getStringExtra("fieldName");
        field = getIntent().getStringExtra("field");
        currentValue = getIntent().getStringExtra("currentValue");
        editField.setText(currentValue);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit " + fieldName);
        communicator = new Communicator(this);
        if(fieldName.equalsIgnoreCase("Email")){
            editField.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }else if(fieldName.equalsIgnoreCase("Phone")) {
            editField.setInputType(InputType.TYPE_CLASS_PHONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editfield, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_done) {
            if(!currentValue.equalsIgnoreCase(editField.getText().toString())){
                try{
                    jsonObject = new JSONObject();
                    jsonObject.put("field", field);
                    jsonObject.put("value", editField.getText().toString());
                } catch(Exception e){
                    e.printStackTrace();
                }
                communicator.communicate(1, RedLifeContants.EDIT_PROFILE + "/" + new SessionManager(this).getUserDetails().get(SessionManager.SERVER_TOKEN), jsonObject);
            }else{
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                returnIntent.putExtra("value", editField.getText().toString());
                finish();
            }
            return true;
        }

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.stay, R.anim.slide_out_down);
    }

    @Override
    public void onPreExecute(int taskId) {
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating...");
        progressDialog.show();
    }

    @Override
    public void onPostExecute(int taskId, JSONObject jsonObject) {

        try{
            Intent returnIntent = new Intent();
            if(jsonObject.has("status") && jsonObject.getInt("status") == 0){
                setResult(RESULT_OK, returnIntent);
                returnIntent.putExtra("value", editField.getText().toString());
            }else{
                setResult(RESULT_CANCELED, returnIntent);
            }
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            finish();
            progressDialog.dismiss();
        }

    }
}
