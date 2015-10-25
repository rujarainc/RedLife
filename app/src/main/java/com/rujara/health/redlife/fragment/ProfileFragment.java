package com.rujara.health.redlife.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.rujara.health.redlife.R;
import com.rujara.health.redlife.store.UserDetails;

import java.util.Calendar;

/**
 * Created by deep.patel on 9/18/15.
 */
public class ProfileFragment extends Fragment {
    private Toolbar toolbar;
    private TextView inputName, inputEmail, inputPassword, inputBloodgroup, inputPhone;
    private TextView dob;
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

        inputName = (TextView) rootView.findViewById(R.id.input_name);
        inputEmail = (TextView) rootView.findViewById(R.id.input_email);
        inputPassword = (TextView) rootView.findViewById(R.id.input_password);
        dob = (TextView) rootView.findViewById(R.id.input_dob);
        inputBloodgroup = (TextView) rootView.findViewById(R.id.input_bloodgroup);
        inputPhone = (TextView) rootView.findViewById(R.id.input_phone);
//        btnSignUp = (Button) rootView.findViewById(R.id.btn_update);
        UserDetails userDetails = UserDetails.getInstance();
        inputName.setText(userDetails.getName());
        inputEmail.setText(userDetails.getEmailId());
        dob.setText(userDetails.getDob());
        inputPhone.setText(userDetails.getPhoneNumber());
        inputBloodgroup.setText(userDetails.getBloodGroup());
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

        // Inflate the layout for this fragment
        return rootView;
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
