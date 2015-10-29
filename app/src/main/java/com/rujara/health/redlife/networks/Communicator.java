package com.rujara.health.redlife.networks;

import android.os.AsyncTask;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.rujara.health.redlife.constants.RedLifeContants;
import com.rujara.health.redlife.utils.AppUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by deep.patel on 10/29/15.
 */
public class Communicator {
    private IAsyncTask asyncTaskListener;

    public Communicator(IAsyncTask asyncTaskListener) {
        this.asyncTaskListener = asyncTaskListener;

    }

    public void communicate(int taskId, String url) {
        Log.d("[rujara]", "Method [" + RedLifeContants.COM_METHOD.GET + "], URL[" + url + "]");
        new GetTask(taskId).execute(url);
    }

    public void communicate(int taskId, String url, JSONObject data) {
        Log.d("[rujara]", "Method [" + RedLifeContants.COM_METHOD.POST + "], URL[" + url + "], DATA[" + data + "]");
        new PostTask(taskId, data).execute(url);
    }

    private class GetTask extends AsyncTask<String, Void, JSONObject> {
        private int taskId;

        public GetTask(int taskId) {
            this.taskId = taskId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            asyncTaskListener.onPreExecute(taskId);
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject response = null;
            try {
                InputStream inputStream = null;
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(args[0]);
                httpGet.setHeader("Accept", "application/json");
                HttpResponse httpResponse = httpclient.execute(httpGet);
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null)
                    response = new AppUtils().convertInputStreamToJson(inputStream);
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("InputStream", e.getLocalizedMessage());
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            asyncTaskListener.onPostExecute(taskId, response);
        }
    }

    private class PostTask extends AsyncTask<String, Void, JSONObject> {
        private int taskId;
        private JSONObject data;

        public PostTask(int taskId, JSONObject data) {
            this.taskId = taskId;
            this.data = data;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            asyncTaskListener.onPreExecute(taskId);
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject response = new JSONObject();
            try {
                InputStream inputStream = null;
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(args[0]);

                String json = "";
                json = data.toString();
                StringEntity se = new StringEntity(json);
                httpPost.setEntity(se);

                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                HttpResponse httpResponse = httpclient.execute(httpPost);
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null)
                    response = new AppUtils().convertInputStreamToJson(inputStream);

            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("InputStream", e.getLocalizedMessage());
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            Log.d("[rujara]", "RESPONSE[" + response + "]");
            asyncTaskListener.onPostExecute(taskId, response);
        }
    }
}
