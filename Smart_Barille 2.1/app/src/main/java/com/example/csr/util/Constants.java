package com.example.csr.util;

public class Constants {

    // 파일 프로바이드
    public static final String FILE_PROVIDER_AUTHORITY = "com.example.csr.fileprovider";

    /* 네이버 음성인식 관련 상수 */
    public static class NaverSpeechApi {
        public static final String CLIENT_ID = "udvt44fz7r";
    }

    /* 구글 클라우드 비전 api 관련 상수 */
    public static class GoogleCloudVisionApi {
        public static final String API_KEY = "AIzaSyBElDblp3QPf6-nobH_gcLnV5_pJP6yRT8";
        public static final String ANDROID_CERT_HEADER = "X-Android-Cert";
        public static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
        public static final int MAX_LABEL_RESULTS = 10;
    }

    /* 네이버 번역 관련 상수 */
    public static class NaverTranslationApi {
        public static final String ADDRESS = "https://openapi.naver.com/v1/papago/n2mt";
        public static final String CLIENT_ID = "wVCRXR41BQ3xx0VDCW65";
        public static final String CLIENT_SECRET = "m7mEqSecUi";
    }

    /* SharedPreferences 관련 상수 */
    public static class SharedPreferencesName {
        // 사용자 Fire store Document ID
        public static final String USER_DOCUMENT_ID = "user_document_id";
    }

    /* Activity 요청 코드 */
    public static class RequestCode {
        public static final int JOIN = 0;                   // 회원가입
        public static final int PICK_CAMERA = 100;          // 카메라
    }

    /* Fire store Collection 이름 */
    public static class FirestoreCollectionName {
        public static final String USER = "users";          // 사용자
        public static final String WORD = "words";          // 단어
    }

    /* 단어 language 종류 */
    public static class WordLanguageKind {
        public static final String KOREAN = "KOR";          // 한글
        public static final String ENGLISH = "ENG";         // 영어
    }

    /* 폴더 경로 */
    public static class FolderPath {
        public static final String IMAGE = "/Braille/Image";
    }

    /* 파일 prefix 이름 */
    public static class FilePrefixName {
        public static final String PHOTO = "PHOTO_";
    }

    /* 파일 확장자 이름 */
    public static class FileSuffixName {
        public static final String IMAGE = ".jpg";
    }

    /* 로딩 딜레이 */
    public static class LoadingDelay {
        public static final int SHORT = 300;
        public static final int LONG = 1000;
    }
}
