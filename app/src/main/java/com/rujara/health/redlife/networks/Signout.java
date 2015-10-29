package com.rujara.health.redlife.networks;

import android.os.AsyncTask;
import android.util.Log;

import com.rujara.health.redlife.constants.RedLifeContants;
import com.rujara.health.redlife.store.UserDetails;
import com.rujara.health.redlife.utils.AppUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by deep.patel on 10/29/15.
 */
public class Signout extends AsyncTask<String, Void, JSONObject> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(String... args) {
        JSONObject response = null;
        try {
            String url = RedLifeContants.SIGNOUT + "/" + args[0];
            Log.d("[rujara]", url);
            InputStream inputStream = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Accept", "application/json");
            HttpResponse httpResponse = httpclient.execute(httpGet);
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
        UserDetails.getInstance().resetUser();
    }
}
