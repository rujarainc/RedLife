package com.rujara.health.redlife.utils;

import com.rujara.health.redlife.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by deep.patel on 9/22/15.
 */
public class AppUtils {
    public JSONObject convertInputStreamToJson(InputStream is) {
        JSONObject object = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            System.out.println(sb);
            object = new JSONObject(sb.toString());
            System.out.println(object);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return object;
    }

    public int getDrawableIconForBloodGroup(String bloodGroup) {
        System.out.println(
                bloodGroup
        );
        System.out.println(bloodGroup.equalsIgnoreCase("a+ve"));
        if (bloodGroup.equalsIgnoreCase("a+ve"))
            return R.drawable.a_plus;
        else if (bloodGroup.equalsIgnoreCase("a-ve"))
            return R.drawable.a_minus;
        else if (bloodGroup.equalsIgnoreCase("a-ve"))
            return R.drawable.a_minus;
        else if (bloodGroup.equalsIgnoreCase("b+ve"))
            return R.drawable.b_plus;
        else if (bloodGroup.equalsIgnoreCase("b-ve"))
            return R.drawable.b_minus;
        else if (bloodGroup.equalsIgnoreCase("ab+ve"))
            return R.drawable.ab_plus;
        else if (bloodGroup.equalsIgnoreCase("ab-ve"))
            return R.drawable.ab_minus;
        else if (bloodGroup.equalsIgnoreCase("o+ve"))
            return R.drawable.o_plus;
        else if (bloodGroup.equalsIgnoreCase("o-ve"))
            return R.drawable.o_minus;

        return 0;
    }

    public String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

}

