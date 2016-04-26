package com.rujara.health.redlife.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.rujara.health.redlife.R;
import com.rujara.health.redlife.activity.EditField;
import com.rujara.health.redlife.constants.RedLifeContants;
import com.rujara.health.redlife.networks.Communicator;
import com.rujara.health.redlife.networks.IAsyncTask;
import com.rujara.health.redlife.store.UserDetails;
import com.rujara.health.redlife.utils.SessionManager;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by deep.patel on 9/18/15.
 */
public class ProfileFragment extends Fragment implements IAsyncTask{
    private Toolbar toolbar;
    private TextView inputName, inputEmail, inputPassword, inputBloodgroup, inputPhone;
    private TextView dob;
    private ImageView editNameIcon, editEmailIcon, editDOBIcon, editPhoneIcon, editBGIcon;
    private TextView tobeEdited;
    private Snackbar snackbar;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;
    private boolean updated;
    private NumberPicker np;
    private Communicator communicator;
    private String[] bloodGroups = new String[]{"A+ve", "A-ve", "B+ve", "B-ve", "AB+ve", "AB-ve", "O+ve", "O-ve"};
    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        communicator = new Communicator(this);
        editNameIcon = (ImageView) rootView.findViewById(R.id.editNameIcon);
        editEmailIcon = (ImageView) rootView.findViewById(R.id.editEmailIcon);
        editDOBIcon = (ImageView) rootView.findViewById(R.id.editDOBIcon);
        editPhoneIcon = (ImageView) rootView.findViewById(R.id.editPhoneIcon);
        editBGIcon = (ImageView) rootView.findViewById(R.id.editBloodGroupIcon);


        inputName = (TextView) rootView.findViewById(R.id.tvname);
        inputEmail = (TextView) rootView.findViewById(R.id.tvemail);
        dob = (TextView) rootView.findViewById(R.id.tvdob);
        inputBloodgroup = (TextView) rootView.findViewById(R.id.tvbloodgroup);
        inputPhone = (TextView) rootView.findViewById(R.id.tvphone);
//        btnSignUp = (Button) rootView.findViewById(R.id.btn_update);
        UserDetails userDetails = UserDetails.getInstance();
        inputName.setText(userDetails.getName());
        inputEmail.setText(userDetails.getEmailId());
        dob.setText(userDetails.getDob());
        inputPhone.setText(userDetails.getPhoneNumber());
        inputBloodgroup.setText(userDetails.getBloodGroup());
        sessionManager = new SessionManager(getActivity());
        dob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    onSelectDate(rootView);
            }
        });
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectDate(rootView);
            }
        });

        editNameIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tobeEdited = inputName;
                Intent editField = new Intent(getActivity(), EditField.class);
                editField.putExtra("fieldName", "Name");
                editField.putExtra("field", RedLifeContants.NAME);
                editField.putExtra("currentValue", inputName.getText().toString());
                startActivityForResult(editField, 1);
            }
        });

        editEmailIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tobeEdited = inputEmail;
                Intent editField = new Intent(getActivity(), EditField.class);
                editField.putExtra("fieldName", "Email");
                editField.putExtra("field", RedLifeContants.EMAILID);
                editField.putExtra("currentValue", inputEmail.getText().toString());
                startActivityForResult(editField, 1);
            }
        });

        editPhoneIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tobeEdited = inputPhone;
                Intent editField = new Intent(getActivity(), EditField.class);
                editField.putExtra("fieldName", "Phone");
                editField.putExtra("field", RedLifeContants.PHONE_NUMBER);
                editField.putExtra("currentValue", inputPhone.getText().toString());
                startActivityForResult(editField, 1);
            }
        });

        editDOBIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updated = false;
                onSelectDate(rootView);
            }
        });

        editBGIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectBG();
            }
        });
        progressDialog = new ProgressDialog(getActivity());

        // Inflate the layout for this fragment
        return rootView;
    }

    public void onSelectBG() {
        final Dialog d = new Dialog(getActivity());

        d.setTitle("Blood Groups");
        d.setContentView(R.layout.bloodgrouppicker);
        Button b1 = (Button) d.findViewById(R.id.button1);
        np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMinValue(0);
        np.setMaxValue(7);
        np.setDisplayedValues(bloodGroups);
        np.setValue(Arrays.asList(bloodGroups).indexOf(inputBloodgroup.getText().toString()));
        // min value 0
        np.setFormatter(new BloodgroupFormatter());
        np.setWrapSelectorWheel(false);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("field", RedLifeContants.BLOOD_GROUP);
                    jsonObject.put("value", bloodGroups[np.getValue()].toLowerCase());
                     //set the value to textview
                    d.dismiss();
                    communicator.communicate(1, RedLifeContants.EDIT_PROFILE + "/" + sessionManager.getUserDetails().get(SessionManager.SERVER_TOKEN), jsonObject);
                } catch(Exception e){
                    e.printStackTrace();
                }

            }
        });
        d.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == getActivity().RESULT_OK){
            if (requestCode == 1) {
                tobeEdited.setText(data.getStringExtra("value"));
                snackbar = Snackbar.make(getView(), "Edit Success", Snackbar.LENGTH_SHORT);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(getResources().getColor(R.color.windowBackground));

                snackbar.show();
            }
        } else if(resultCode == getActivity().RESULT_CANCELED){
            snackbar = Snackbar.make(getView(), "Edit Failed/Cancelled", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(getResources().getColor(R.color.windowBackground));
            snackbar.show();
        } else{
            snackbar = Snackbar.make(getView(), "Edit Failed", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(getResources().getColor(R.color.windowBackground));
            snackbar.show();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void onSelectDate(View v){
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(v.getContext(),
                new mDateSetListener(), mYear, mMonth, mDay);
        dialog.show();
    }

    @Override
    public void onPreExecute(int taskId) {
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void onPostExecute(int taskId, JSONObject jsonObject) {
        progressDialog.dismiss();
        try{
            if(jsonObject.has("status") && jsonObject.getInt("status")==0){
                inputBloodgroup.setText(bloodGroups[np.getValue()]);
                UserDetails.getInstance().setBloodGroup(bloodGroups[np.getValue()]);
                snackbar = Snackbar.make(getView(), "Successfully updated", Snackbar.LENGTH_SHORT);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(getResources().getColor(R.color.windowBackground));
                snackbar.show();
            } else{
                snackbar = Snackbar.make(getView(), "Failed to update", Snackbar.LENGTH_SHORT);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(getResources().getColor(R.color.windowBackground));
                snackbar.show();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    class mDateSetListener implements DatePickerDialog.OnDateSetListener, IAsyncTask {

        private boolean updated = false;
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            int mYear = year;
            int mMonth = monthOfYear;
            int mDay = dayOfMonth;
            StringBuilder dataString = new StringBuilder()
                    .append(mDay).append("/").append(mMonth + 1).append("/")
                    .append(mYear).append(" ");
            dob.setText(dataString);
            if(!updated){
                Communicator communicator = new Communicator(this);
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("field", RedLifeContants.DOB);
                    jsonObject.put("value", dataString.toString());
                    communicator.communicate(1, RedLifeContants.EDIT_PROFILE + "/" + sessionManager.getUserDetails().get(SessionManager.SERVER_TOKEN), jsonObject);
                } catch(Exception e){
                    e.printStackTrace();
                }
                updated = true;
            }
        }

        @Override
        public void onPreExecute(int taskId) {
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Updating...");
            progressDialog.show();
        }

        @Override
        public void onPostExecute(int taskId, JSONObject jsonObject) {
            progressDialog.dismiss();
            try{
                if(jsonObject.has("status") && jsonObject.getInt("status")==0){
                    snackbar = Snackbar.make(getView(), "Successfully updated", Snackbar.LENGTH_SHORT);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(getResources().getColor(R.color.windowBackground));
                    snackbar.show();
                } else{
                    snackbar = Snackbar.make(getView(), "Failed to update", Snackbar.LENGTH_SHORT);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(getResources().getColor(R.color.windowBackground));
                    snackbar.show();
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

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