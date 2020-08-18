package com.example.csr.util;

public class Constants {

    /* SharedPreferences 관련 상수 */
    public static class SharedPreferencesName {
        // 사용자 Fire store Document ID
        public static final String USER_DOCUMENT_ID = "user_document_id";
    }

    /* Activity 요청 코드 */
    public static class RequestCode {
        public static final int JOIN = 0;
    }

    /* Fire store Collection 이름 */
    public static class FirestoreCollectionName {
        public static final String USER = "users";          // 사용자
    }

    /* 로딩 딜레이 */
    public static class LoadingDelay {
        public static final int SHORT = 300;
        public static final int LONG = 1000;
    }

}
