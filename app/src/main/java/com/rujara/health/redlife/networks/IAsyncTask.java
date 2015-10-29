package com.rujara.health.redlife.networks;

import org.json.JSONObject;

/**
 * Created by deep.patel on 10/29/15.
 */
public interface IAsyncTask {
    public void onPreExecute(int taskId);

    public void onPostExecute(int taskId, JSONObject jsonObject);
}
