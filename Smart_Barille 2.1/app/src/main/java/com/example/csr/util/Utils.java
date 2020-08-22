package com.example.csr.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class Utils {

    /* 휴대번호 체크 */
    public static boolean isPhoneNumber(String number) {
        String regEx = "^(010)-?(\\d{4})-?(\\d{4})$";
        if (number.indexOf("010") != 0) {
            regEx = "^(01(?:1|[6-9]))-?(\\d{3})-?(\\d{4})$";
        }

        return Pattern.matches(regEx, number);
    }

    /* 휴대번호 얻기 */
    public static String getPhoneNumber(Activity activity) {
        String number = "";

        try {
            TelephonyManager tel = (TelephonyManager) activity.getSystemService(Activity.TELEPHONY_SERVICE);
            number = tel.getLine1Number();

            if (!TextUtils.isEmpty(number)) {
                // "-", "+" 제거
                number = number.replace("-", "").replace("+", "");

                if (number.indexOf("82") == 0) {
                    number = "0" + number.substring(2);
                }
            }
        } catch (SecurityException e) {
        } catch (Exception e) {
        }

        return number;
    }

    /* 카메라 앱을 활용하여 사진찍기 */
    public static String goCamera(Activity activity, int requestCode) {
        String filePath = "";

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;

        try {
            photoFile = FileUtils.createImageFile(Constants.FilePrefixName.PHOTO);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (photoFile != null) {
            // 이미지가 저장될 파일은 카메라 앱이 구동되기 전에 세팅해서 넘겨준다.
            // 가로에서 세로 또는 세로에서 가로로 전환되는 순간 넘겨받는 인자값이 사라짐 그래서 미리 파일경로 지정
            //intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

            filePath = photoFile.getAbsolutePath();

            Uri photoUri = FileProvider.getUriForFile(activity, Constants.FILE_PROVIDER_AUTHORITY, photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            activity.startActivityForResult(intent, requestCode);
        }

        return filePath;
    }

    /* 갤러리에 사진 추가 */
    public static void addGalleryPic(Context context, String filePath) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        File f = new File(filePath);
        Uri contentUri = Uri.fromFile(f);
        intent.setData(contentUri);
        context.sendBroadcast(intent);
    }
}
