package com.rujara.health.redlife.constants;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by deep.patel on 9/20/15.
 */
public class RedLifeContants {

    public static final int LOCATION_UPDATER_TIME = 3000;

//    public static final String AUTHENTICATION = "http://healthserver-rujara.rhcloud.com/rest/endpoint/authenticate";
//    public static final String SIGNUP = "http://healthserver-rujara.rhcloud.com/rest/endpoint/signup";
//    public static final String NOTIFY = "http://healthserver-rujara.rhcloud.com/rest/endpoint/notify";
//    public static final String SIGNOUT = "http://healthserver-rujara.rhcloud.com/rest/endpoint/signout";
//    public static final String GET_USER = "http://healthserver-rujara.rhcloud.com/rest/endpoint/getuser";
//    public static final String NOTIFY_LOCATION = "http://healthserver-rujara.rhcloud.com/rest/endpoint/locate";
//    public static final String REQUEST = "http://healthserver-rujara.rhcloud.com/rest/endpoint/request";

    public static final String AUTHENTICATION = "http://ec2-52-89-24-191.us-west-2.compute.amazonaws.com:8080/HealthService/rest/endpoint/authenticate";
    public static final String SIGNUP = "http://ec2-52-89-24-191.us-west-2.compute.amazonaws.com:8080/HealthService/rest/endpoint/signup";
    public static final String NOTIFY = "http://ec2-52-89-24-191.us-west-2.compute.amazonaws.com:8080/HealthService/rest/endpoint/notify";
    public static final String SIGNOUT = "http://ec2-52-89-24-191.us-west-2.compute.amazonaws.com:8080/HealthService/rest/endpoint/signout";
    public static final String GET_USER = "http://ec2-52-89-24-191.us-west-2.compute.amazonaws.com:8080/HealthService/rest/endpoint/getuser";
    public static final String NOTIFY_LOCATION = "http://ec2-52-89-24-191.us-west-2.compute.amazonaws.com:8080/HealthService/rest/endpoint/locate";
    public static final String REQUEST = "http://ec2-52-89-24-191.us-west-2.compute.amazonaws.com:8080/HealthService/rest/endpoint/request";
    public static final String GET_MYACTION = "http://ec2-52-89-24-191.us-west-2.compute.amazonaws.com:8080/HealthService/rest/endpoint/getMyAction";

    public static final long NETWORK_INSPECTOR_THREAD_SLEEP_TIME = 2;

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String FIRSTNAME = "firstName";
    public static final String LASTNAME = "lastName";
    public static final String NAME = "name";
    public static final String EVENT_REGISTRATION_ID = "eventRegistrationId";
    public static final String EMAILID = "emailId";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String BLOOD_GROUP = "bloodGroup";
    public static final String SERVER_AUTH_TOKEN = "serverAuthToken";
    public static final String PASSWORD = "password";
    public static final String DOB = "dob";
    public static GoogleCloudMessaging GCM = null;
    public static boolean OPEN_HISTORY = false;
}
