package com.hackathon;

import android.*;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.camera.CameraModule;
import com.esafirm.imagepicker.features.camera.ImmediateCameraModule;
import com.esafirm.imagepicker.features.camera.OnImageReadyListener;
import com.esafirm.imagepicker.model.Image;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private ImageView imgProfilePic;
    private TextView txtName;
    private TextView txtBase64;

    private static final int RC_CODE_PICKER = 2000;
    private static final int RC_CAMERA = 3000;
    private ArrayList<Image> images = new ArrayList<>();
    private CameraModule cameraModule;
    ImagePicker imagePicker;
    public ProgressDialog progessDialog;
    private String path;
    private Bitmap bm;
    private Button btnRegister;
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json; charset=utf-8");
    public static OkHttpClient client = new OkHttpClient();
    UserModel userModel = new UserModel();
    public SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        AppCenter.start(getApplication(), "d10e78b3-7fe3-44c6-b4bf-9257b6d056ce", Analytics.class, Crashes.class);
        sharedPref = getApplicationContext().getSharedPreferences("Hackathon",Context.MODE_PRIVATE);

        if (!sharedPref.getString(AppConstants.UserID, "").equals("")) {

            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        } else {

            imgProfilePic = findViewById(R.id.img_profile_pic);
            txtName = findViewById(R.id.txt_name);
            txtBase64 = findViewById(R.id.txt_base64);
            btnRegister = findViewById(R.id.btn_register);
            btnRegister.setOnClickListener(new RegisterBtnListener());
            imgProfilePic.setOnClickListener(new ProfilePicListener());

            imagePicker = ImagePicker.create(this)
                    .theme(R.style.ImagePickerTheme)
                    .returnAfterFirst(false) // set whether pick action or camera action should return immediate result or not. Only works in single mode for image picker
                    .folderMode(false) // set folder mode (false by default)
                    .folderTitle("Folder") // folder selection title
                    .imageTitle("Tap to select"); // image selection title
            imagePicker.single();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create channel to show notifications.
                String channelId = getString(R.string.default_notification_channel_id);
                String channelName = getString(R.string.default_notification_channel_name);
                NotificationManager notificationManager =
                        getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                        channelName, NotificationManager.IMPORTANCE_LOW));
            }

            String token = FirebaseInstanceId.getInstance().getToken();
            Log.d(TAG, "Register Token: " + token);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(AppConstants.DeviceToken, token);
            editor.commit();
            //Toast.makeText(RegisterActivity.this, "Token=>" + token, Toast.LENGTH_SHORT).show();
            userModel.setDeviceToken(token);
        }
    }


    private class RegisterBtnListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            postRegister();
        }
    }

    private void postRegister() {

        Gson gson = new Gson();
        userModel.setName(txtName.getText().toString().trim());
        userModel.setMobileNumber(txtBase64.getText().toString().trim());
        if (userModel.getName().equals("") || userModel.getMobileNumber().equals("")) {
            Toast.makeText(this, "Please enter mandatory information", Toast.LENGTH_SHORT).show();
            return;
        }
        String jsonBody = gson.toJson(userModel);
        Log.d(TAG, "requestBody:" + jsonBody);
        try {
            PostData.run("https://pickforme.azurewebsites.net/api/UserDetails/Register", jsonBody, RegisterActivity.this);
        } catch (Exception e) {
            System.out.println("response:" + e.getMessage());
        }
    }

    public static class PostData {

        static void run(String url, String postBody, final Context activityCtx) throws IOException {
            final RegisterActivity activity = (RegisterActivity) activityCtx;
            activity.progessDialog = new ProgressDialog(activityCtx);
            activity.progessDialog.setMessage("Loading...");
            activity.progessDialog.setCancelable(false);
            activity.progessDialog.show();

            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, postBody)).header("ZUMO-API-VERSION", "2.0.0").header("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    activity.progessDialog.dismiss();
// TODO comment this afterwards
                    //  Intent i = new Intent(activityCtx, MainActivity.class);
                    //  activityCtx.startActivity(i);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    activity.progessDialog.dismiss();
                    try (ResponseBody responseBody = response.body()) {
                        //if (!response.isSuccessful())
                        //   throw new IOException("Unexpected code " + response);
                        //System.out.println("Response => " + responseBody.string());
                        //Toast.makeText(activityCtx,""+responseBody.toString(),Toast.LENGTH_SHORT).show();

                        // TODO save token and register status in prefs
                        SharedPreferences.Editor editor = activity.sharedPref.edit();
                        editor.putString(AppConstants.UserID, responseBody.string().replace("\"",""));
                        editor.commit();

                        System.out.println("UserId => " + activity.sharedPref.getString(AppConstants.UserID,""));

                        Intent i = new Intent(activityCtx, MainActivity.class);
                        i.putExtra("id",activity.sharedPref.getString(AppConstants.UserID,""));
                        activityCtx.startActivity(i);
                        activity.finish();

                    }
                }
            });


        }
    }

    private class ProfilePicListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            imagePicker.limit(10) // max images can be selected (99 by default)
                    .showCamera(true) // show camera or not (true by default)
                    .imageDirectory("Camera")   // captured image directory name ("Camera" folder by default)
                    .imageFullDirectory(Environment.getExternalStorageDirectory().getPath()) // can be full path
                    .origin(images) // original selected images, used in multi mode
                    .start(RC_CODE_PICKER); // start image picker activity with request code

/*
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setMessage("Pick Image From")
                    .setCancelable(false)
                    .setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            imagePicker.limit(10) // max images can be selected (99 by default)
                                    .showCamera(true) // show camera or not (true by default)
                                    .imageDirectory("Camera")   // captured image directory name ("Camera" folder by default)
                                    .imageFullDirectory(Environment.getExternalStorageDirectory().getPath()) // can be full path
                                    .origin(images) // original selected images, used in multi mode
                                    .start(RC_CODE_PICKER); // start image picker activity with request code
                        }
                    })
                    .setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            final String[] permissions = new String[]{android.Manifest.permission.CAMERA};
                            if (ActivityCompat.checkSelfPermission(RegisterActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(RegisterActivity.this, permissions, RC_CAMERA);
                            } else {
                                captureImage();
                            }
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
*/
        }
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (requestCode == RC_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            images = (ArrayList<Image>) ImagePicker.getImages(data);
            printImages(images);
            return;
        }

        if (requestCode == RC_CAMERA && resultCode == RESULT_OK) {
            getCameraModule().getImage(this, data, new OnImageReadyListener() {
                @Override
                public void onImageReady(List<Image> resultImages) {
                    images = (ArrayList<Image>) resultImages;
                    printImages(images);
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private ImmediateCameraModule getCameraModule() {
        if (cameraModule == null) {
            cameraModule = new ImmediateCameraModule();
        }
        return (ImmediateCameraModule) cameraModule;
    }

    private void printImages(List<Image> images) {
        if (images == null) return;

        //TODO put this in background

        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0, l = images.size(); i < l; i++) {
            stringBuffer.append(images.get(i).getPath());
        }
        //txtName.setText(stringBuffer.toString());
        path = stringBuffer.toString();
        printBase64(stringBuffer.toString().replace("file:", ""));
    }

    private void printBase64(String path) {
        callCompressTask();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_CAMERA) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void captureImage() {
        startActivityForResult(getCameraModule().getCameraIntent(RegisterActivity.this), RC_CAMERA);
    }

    private void callCompressTask() {
        Log.d(TAG, ">>callCompressTask");
        new CompressAsyncTask().execute();
    }

    class CompressAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (progessDialog == null || !progessDialog.isShowing()) {
                progessDialog = new ProgressDialog(RegisterActivity.this);
                progessDialog.setMessage("Compressing Photo...");
                progessDialog.setCancelable(false);
                progessDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... urls) {
            bm = BitmapFactory.decodeFile(path);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] byteArrayImage = baos.toByteArray();

            String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

            return encodedImage;
        }

        protected void onPostExecute(String encodedImage) {
            progessDialog.dismiss();
            //txtBase64.setText(encodedImage);
            userModel.setPhoto(encodedImage);
            imgProfilePic.setImageBitmap(bm);

        }
    }
}
