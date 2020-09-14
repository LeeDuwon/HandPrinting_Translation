package com.example.csr.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import androidx.exifinterface.media.ExifInterface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {

    /* 이미지 파일 사이즈 조절 */
    public static Bitmap resizeImage(String filePath, int size) {
        Bitmap bmp;

        // Bitmap 정보를 가져온다.
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, bmOptions);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = size;

        bmp = BitmapFactory.decodeFile(filePath, bmOptions);

        return bmp;
    }

    /* Bitmap => InputStream 변환 */
    public static InputStream bitmapToInputStream(Bitmap bitmap, String fileExtension) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (fileExtension.equals("png")) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        } else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        }

        return new ByteArrayInputStream(baos.toByteArray());
    }

    /* 회전 각도 구하기 */
    public static int getExifOrientation(String filePath) {
        ExifInterface exif;

        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            return 0;
        }

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        int degree = 0;
        if (orientation != -1) {
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        }

        return degree;
    }

    /* 이미지 회전하기 */
    public static Bitmap getRotatedBitmap(Bitmap bitmap, int degree) {
        if (degree != 0 && bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.setRotate(degree, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

            try {
                Bitmap tmpBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                if (bitmap != tmpBitmap) {
                    bitmap.recycle();
                    bitmap = tmpBitmap;
                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }
}
