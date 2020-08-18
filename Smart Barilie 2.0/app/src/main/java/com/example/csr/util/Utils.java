package com.example.csr.util;

import android.app.Activity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

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

}
