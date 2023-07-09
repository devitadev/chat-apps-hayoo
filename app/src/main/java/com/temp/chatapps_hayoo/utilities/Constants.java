package com.temp.chatapps_hayoo.utilities;

import java.util.HashMap;

public class Constants {
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE_NUMBER = "phoneNumber";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PREFERENCE_NAME = "hayooPreference";
    public static final String KEY_IS_LOGIN = "isLogin";
    public static final String KEY_USER_ID = "userID";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_USER = "user";
    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_SENDER_ID = "senderID";
    public static final String KEY_RECEIVER_ID = "receiverID";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_COLLECTION_CONVERSATIONS = "conversations";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_RECEIVER_NAME = "receiverName";
    public static final String KEY_SENDER_IMAGE = "senderImage";
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";
    public static final String KEY_SENDER_EMAIL = "senderEmail";
    public static final String KEY_RECEIVER_EMAIL = "receiverEmail";
    public static final String KEY_SENDER_LATITUDE = "senderLatitude";
    public static final String KEY_RECEIVER_LATITUDE = "receiverLatitude";
    public static final String KEY_SENDER_LONGITUDE = "senderLongitude";
    public static final String KEY_RECEIVER_LONGITUDE = "receiverLongitude";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
    public static final String KEY_AVAILABILITY = "availability";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static HashMap<String, String> remoteMsgHeaders = null;
    public static HashMap<String, String> getRemoteMsgHeaders() {
        if (remoteMsgHeaders == null) {
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAA_KRGlM0:APA91bHdo8u8FDDBfPoJib-VMtwpHn1FDeOiR8nuR4ltsIEZxKwM0G-Sa5slaONTCG003ysWCUggqUbfeDF0DqUJfpddpncFijEKnRXjVivPF3-Fm1BGXidMCgjvw67YHAVLuBI8AM01"
            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }

}
