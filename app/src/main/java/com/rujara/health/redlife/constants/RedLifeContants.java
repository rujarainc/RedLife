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

    public static final String AUTHENTICATION = "http://ec2-52-24-42-225.us-west-2.compute.amazonaws.com/HealthService/rest/endpoint/authenticate";
    public static final String SIGNUP = "http://ec2-52-24-42-225.us-west-2.compute.amazonaws.com/HealthService/rest/endpoint/signup";
    public static final String NOTIFY = "http://ec2-52-24-42-225.us-west-2.compute.amazonaws.com/HealthService/rest/endpoint/notify";
    public static final String SIGNOUT = "http://ec2-52-24-42-225.us-west-2.compute.amazonaws.com/HealthService/rest/endpoint/signout";
    public static final String GET_USER = "http://ec2-52-24-42-225.us-west-2.compute.amazonaws.com/HealthService/rest/endpoint/getuser";
    public static final String NOTIFY_LOCATION = "http://ec2-52-24-42-225.us-west-2.compute.amazonaws.com/HealthService/rest/endpoint/locate";
    public static final String REQUEST = "http://ec2-52-24-42-225.us-west-2.compute.amazonaws.com/HealthService/rest/endpoint/request";
    public static final String GET_MYACTION = "http://ec2-52-24-42-225.us-west-2.compute.amazonaws.com/HealthService/rest/endpoint/getMyAction";
    public static final String RESPOND = "http://ec2-52-24-42-225.us-west-2.compute.amazonaws.com/HealthService/rest/endpoint/respond";
    public static final String MATCHED_REQUEST = "http://ec2-52-24-42-225.us-west-2.compute.amazonaws.com/HealthService/rest/endpoint/matchedRequests";
    public static final String OTHER_REQUEST = "http://ec2-52-24-42-225.us-west-2.compute.amazonaws.com/HealthService/rest/endpoint/otherRequests";
    public static final String GET_RESPONSES = "http://ec2-52-24-42-225.us-west-2.compute.amazonaws.com/HealthService/rest/endpoint/responses";
    public static final String RATE = "http://ec2-52-24-42-225.us-west-2.compute.amazonaws.com/HealthService/rest/endpoint/close";
    public static final String VERIFY = "http://ec2-52-24-42-225.us-west-2.compute.amazonaws.com/HealthService/rest/endpoint/verify";
    public static final String GET_VERIFY_CODE = "http://ec2-52-24-42-225.us-west-2.compute.amazonaws.com/HealthService/rest/endpoint/getverifycode";
    public static final String EDIT_PROFILE = "http://ec2-52-24-42-225.us-west-2.compute.amazonaws.com/HealthService/rest/endpoint/updateProfile";

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
    public static final String DETAILS = "details";
    public static final String DOB = "dob";
    public static final String RATING = "rating";
    public static final String SUGGESTION = "suggestion";
    public static final String REQUEST_ID = "requestId";
    public static final String SMS_VERIFICATION_CODE = "smsVerificationCode";
    public static final String IS_VERIFIED = "isVerified";
    public static final String RESPONDED = "responded";
    public static final String RESPOND_TIME = "respondTime";
    // Milliseconds per second
    public static final int MILLISECONDS_PER_SECOND = 1000;
    // The update interval
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // A fast interval ceiling
    public static final int FAST_CEILING_IN_SECONDS = 1;
    // Update interval in milliseconds
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * UPDATE_INTERVAL_IN_SECONDS;
    // A fast ceiling of update intervals, used when the app is visible
    public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * FAST_CEILING_IN_SECONDS;
    public static GoogleCloudMessaging GCM = null;
    public static boolean OPEN_HISTORY = false;

    public static enum COM_METHOD {
        GET, POST
    }

    public class ActivityResults{
        public static final int ON_REQUEST_CLOSED = 1;
    }
}
