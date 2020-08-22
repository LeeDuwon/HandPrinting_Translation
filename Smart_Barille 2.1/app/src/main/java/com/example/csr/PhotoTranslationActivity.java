package com.example.csr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.csr.util.Constants;
import com.example.csr.util.ImageUtils;
import com.example.csr.util.PackageManagerUtils;
import com.example.csr.util.Utils;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhotoTranslationActivity extends TranslationActivity {
    private static String TAG = PhotoTranslationActivity.class.getSimpleName();

    private String photoFilePath;           // 사진 파일 경로

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_translation);

        // 제목 표시
        setTitle(getString(R.string.activity_title_photo_translation));

        // 홈버튼(<-) 표시
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // 초기화
        init();

        this.imgIcon.setOnClickListener(mClickListener);
        this.btnWord.setOnClickListener(mClickListener);
        findViewById(R.id.btnTranslation).setOnClickListener(mClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.RequestCode.PICK_CAMERA) {
                // 카메라
                if (TextUtils.isEmpty(this.photoFilePath)) {
                    return;
                }

                // 갤러리에 사진추가
                Utils.addGalleryPic(this, this.photoFilePath);
                Log.d(TAG, "file: " + this.photoFilePath);

                // 이미지 파일 표시
                displayImage(this.photoFilePath);

                this.imgIcon.setEnabled(false);
                this.layWord.setVisibility(View.VISIBLE);

                this.result = "";
                this.translated = "";
                this.txtWord.setText("Connecting...");
                this.btnWord.setText("");

                // 분석할때 true 이면 한국어로 false 이면 영어로
                this.startKo = this.rd1.isChecked();

                // 로딩 레이아웃 보여주기
                this.layLoading.setVisibility(View.VISIBLE);

                // 구글 클라우드 비전 사용하기
                callCloudVision(this.photoFilePath);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* 이미지 파일 표시 */
    private void  displayImage(String filePath) {
        // 이미지 표시 하기
        Glide.with(this)
                .load("file://" + filePath)
                .error(R.drawable.ic_alert_circle_96_gray)
                .apply(new RequestOptions().fitCenter())
                .transition(new DrawableTransitionOptions().crossFade())
                .into(this.imgIcon);
    }

    /* 구글 클라우드 비전 사용하기 */
    private void callCloudVision(String filePath) {
        // 이미지 회전 각도 구하기
        int degree = ImageUtils.getExifOrientation(filePath);
        // 파일에서 Bitmap 얻기
        Bitmap bitmap = ImageUtils.resizeImage(filePath, 1);
        if (degree != 0) {
            // 회전 하기
            bitmap = ImageUtils.getRotatedBitmap(bitmap, degree);
        }

        // Switch text to loading
        this.txtWord.setText("loading");

        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new LabelDetectionTask(prepareAnnotationRequest(bitmap));
            labelDetectionTask.execute();
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
            e.getMessage());
        }
    }

    private Vision.Images.Annotate prepareAnnotationRequest(final Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(Constants.GoogleCloudVisionApi.API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(Constants.GoogleCloudVisionApi.ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(Constants.GoogleCloudVisionApi.ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature labelDetection = new Feature();
                labelDetection.setType("TEXT_DETECTION");
                labelDetection.setMaxResults(Constants.GoogleCloudVisionApi.MAX_LABEL_RESULTS);
                add(labelDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message;

        List<EntityAnnotation> labels = response.getResponses().get(0).getTextAnnotations();
        if (labels != null) {
            message = labels.get(0).getDescription();
        } else {
            message = "";
        }

        return message;
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.imgIcon:
                    // 사진촬영 후 텍스트 추출
                    photoFilePath = Utils.goCamera(PhotoTranslationActivity.this, Constants.RequestCode.PICK_CAMERA);
                    Log.d(TAG, "path: " + photoFilePath);
                    if (TextUtils.isEmpty(photoFilePath)) {
                        Toast.makeText(PhotoTranslationActivity.this, getString(R.string.msg_camera_app_empty), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btnWord:
                    // 단어를 음성으로 듣기
                    speechWord(btnWord.getText().toString());
                    break;
                case R.id.btnTranslation:
                    // 점자 변환하기 (저장 후 점자로 변환)
                    final String word = btnWord.getText().toString();

                    if (TextUtils.isEmpty(word)) {
                        Toast.makeText(PhotoTranslationActivity.this, getString(R.string.msg_word_empty), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    layLoading.setVisibility(View.VISIBLE);
                    // 로딩 레이아웃을 표시하기 위해 딜레이 적용
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // (저장 후 점자로 변환)
                            saveWord(word);
                        }
                    }, Constants.LoadingDelay.SHORT);
                    break;
            }
        }
    };

    private class LabelDetectionTask extends AsyncTask<Object, Void, String> {
        private Vision.Images.Annotate mRequest;

        private LabelDetectionTask(Vision.Images.Annotate annotate) {
            this.mRequest = annotate;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = this.mRequest.execute();

                return convertResponseToString(response);
            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
//                return e.getContent();
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " +
                        e.getMessage());
//                return e.getMessage();
            }

            return "";
        }

        // 구글 비전 API 요청 결과
        protected void onPostExecute(String data) {
            Log.e(TAG, "구글비전 API 요청결과" + data);

            imgIcon.setEnabled(true);
            layLoading.setVisibility(View.GONE);

            if (TextUtils.isEmpty(data)) {
                Toast.makeText(PhotoTranslationActivity.this, getString(R.string.msg_google_cloud_vision_error), Toast.LENGTH_SHORT).show();
                txtWord.setText("인식 실패");
                return;
            }

            result = data.trim();
            txtWord.setText(result);

            // 한글 <-> 영어 번역
            new BackgroundTask(result).execute();
        }
    }
}