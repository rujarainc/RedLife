package com.rujara.health.redlife.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import com.rujara.health.redlife.R;
import com.rujara.health.redlife.constants.RedLifeContants;
import com.rujara.health.redlife.networks.Communicator;
import com.rujara.health.redlife.networks.IAsyncTask;
import com.rujara.health.redlife.utils.SessionManager;

import org.json.JSONObject;

public class XPRating extends AppCompatActivity implements IAsyncTask {
    private RatingBar ratingBar;
    private Communicator communicator = new Communicator(this);
    private EditText suggestions;
    private SessionManager sessionManager;
    private String requestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xprating);
        requestId = getIntent().getStringExtra(RedLifeContants.REQUEST_ID);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        suggestions = (EditText) findViewById(R.id.input_suggestions);
        sessionManager = new SessionManager(this);
        Drawable progress = ratingBar.getProgressDrawable();
        DrawableCompat.setTint(progress, getResources().getColor(R.color.colorPrimary));
    }

    public void rate(View view){
        float ratings = ratingBar.getRating();
        String suggestionsText = suggestions.getText().toString();
        JSONObject data = new JSONObject();
        try{
            data.put(RedLifeContants.RATING, ratings);
            if(suggestionsText!=null && !suggestionsText.equalsIgnoreCase(""))
                data.put(RedLifeContants.SUGGESTION, suggestionsText);
            if(requestId!=null && !requestId.equalsIgnoreCase(""))
                data.put(RedLifeContants.REQUEST_ID, requestId);
        }catch(Exception e){
            e.printStackTrace();
        }
        communicator.communicate(1, RedLifeContants.RATE + "/" + sessionManager.getUserDetails().get(SessionManager.SERVER_TOKEN) + "/1", data);
    }

    @Override
    public void onPreExecute(int taskId) {
        System.out.println("Sending Rating");
    }

    @Override
    public void onPostExecute(int taskId, JSONObject jsonObject) {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
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
}
