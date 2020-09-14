package com.example.csr.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtils {

    /* 외장 SD 카드 */
    public static String getExternalStoragePath() {
        String ext = Environment.getExternalStorageState();
        String path;

        if (ext.equals(Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            path = Environment.MEDIA_UNMOUNTED;
        }

        return path;
    }

    /* 이미지 파일 생성 */
    public static File createImageFile(String filePrefix) throws IOException {
        // 파일명
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return createFile(Constants.FolderPath.IMAGE, filePrefix, Constants.FileSuffixName.IMAGE, timeStamp);
    }

    /* 파일 생성 (이름 지정) */
    public static File createFile(String folderPath, String filePrefix, String fileSuffix, String name) throws  IOException {
        String path = getExternalStoragePath() + folderPath;
        File dir = new File(path);

        if ( !dir.exists())  {
            dir.mkdirs();
        }

        // 파일명
        String fileName = filePrefix + name;

        return File.createTempFile(fileName, fileSuffix, dir);
    }
}
