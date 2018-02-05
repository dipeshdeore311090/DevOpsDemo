package com.hackathon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GridAdapter gridAdapter;
    private List<GridModel> gridList;
    private Activity activity;
    String url;
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json; charset=utf-8");
    private ProgressDialog progessDialog;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ImageView imgAdd;
    private ImageView imgSend;
    private EditText etQuestion;
    private TextView txtMyPolls;

    private static final int RC_CODE_PICKER = 2000;
    private static final int CONTACT_PICKER_RESULT = 5000;
    private ArrayList<Image> images = new ArrayList<>();
    ImagePicker imagePicker;
    private int currentItemPosition;
    PollDetailsModel pollDetailsModel = new PollDetailsModel();
    public SharedPreferences sharedPref;
    public static OkHttpClient client = new OkHttpClient();
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        mHandler = new Handler(Looper.getMainLooper());

        sharedPref = getApplicationContext().getSharedPreferences("Hackathon", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (getIntent().getStringExtra("id") != null) {
            editor.putString(AppConstants.UserID, getIntent().getStringExtra("id"));
        }
        editor.commit();
        Log.d(TAG, "UserId => " + sharedPref.getString(AppConstants.UserID, ""));

        imagePicker = ImagePicker.create(this)
                .theme(R.style.ImagePickerTheme)
                .returnAfterFirst(false) // set whether pick action or camera action should return immediate result or not. Only works in single mode for image picker
                .folderMode(false) // set folder mode (false by default)
                .folderTitle("Folder") // folder selection title
                .imageTitle("Tap to select"); // image selection title
        imagePicker.single();

        recyclerView = findViewById(R.id.recycler_view);
        imgAdd = findViewById(R.id.img_add);
        imgSend = findViewById(R.id.img_send);
        etQuestion = findViewById(R.id.txt_question);
        txtMyPolls = findViewById(R.id.txt_my_polls);
        imgAdd.setOnClickListener(new AddOptionListener());
        imgSend.setOnClickListener(new SendListener());
        txtMyPolls.setOnClickListener(new PollsListener());
        gridList = new ArrayList<>();

        //getDataFromNetwork();

        gridAdapter = new GridAdapter(activity, gridList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(gridAdapter);

        expandGridList();

    }

    private void getDataFromNetwork() {
        url = " https://newsapi.org/v1/articles?source=engadget&sortBy=top&apiKey=5ce04fd77d4f410ebe9e5e1f2252d889";

        FetchData fetchData = new FetchData();
        try {
            fetchData.run(url);
        } catch (Exception e) {
            System.out.println("response:" + e.getMessage());
        }
    }

    private class AddOptionListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            expandGridList();
        }
    }

    private class PollsListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent i = new Intent(activity, MyPollsActivity.class);
            startActivity(i);
        }
    }

    private class SendListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);

        }
    }

    private void callCreatePoll(){

        List<String> contactList = Arrays.asList("9028681606", "9975295828", "8208302029", "9579828328");
        pollDetailsModel.setUserID(sharedPref.getString(AppConstants.UserID, ""));
        pollDetailsModel.setPollType("TextAndImage");
        pollDetailsModel.setQuestionText(etQuestion.getText().toString());
        pollDetailsModel.setOptions(gridList);
        pollDetailsModel.setContacts(contactList);

        Gson gson = new Gson();
        String jsonBody = gson.toJson(pollDetailsModel);
        Log.d(TAG, "RequestBody=>" + jsonBody);

        try {
            progessDialog = new ProgressDialog(activity);
            progessDialog.setMessage("Loading...");
            progessDialog.setCancelable(false);
            progessDialog.show();

            Request request = new Request.Builder()
                    .url("https://pickforme.azurewebsites.net/api/PollDetails/CreatePoll")
                    .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, jsonBody)).header("ZUMO-API-VERSION", "2.0.0").header("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    progessDialog.dismiss();
                    System.out.println("Response => " + e.getMessage().toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    progessDialog.dismiss();
                    try (ResponseBody responseBody = response.body()) {
                        //if (!response.isSuccessful())
                        //   throw new IOException("Unexpected code " + response);
                        System.out.println("Response => " + responseBody.string());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showToastMessage();
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("response:" + e.getMessage());
        }
    }

    private void expandGridList() {
        GridModel gridModel1 = new GridModel();
        gridModel1.setImage("");
        gridModel1.setText("");
        gridList.add(gridModel1);
        gridAdapter.notifyDataSetChanged();

    }

    public class FetchData {

        OkHttpClient client = new OkHttpClient();

        Gson gson = new Gson();
        String jsonBody = gson.toJson(new GridModel());

        void run(String url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful())
                            throw new IOException("Unexpected code " + response);
                        System.out.println("Response => " + responseBody.string());

                    }
                }
            });

        }
    }

    public void cameraClicked(int position) {

        currentItemPosition = position;

        imagePicker.limit(10) // max images can be selected (99 by default)
                .showCamera(true) // show camera or not (true by default)
                .imageDirectory("Camera")   // captured image directory name ("Camera" folder by default)
                .imageFullDirectory(Environment.getExternalStorageDirectory().getPath()) // can be full path
                .origin(images) // original selected images, used in multi mode
                .start(RC_CODE_PICKER); // start image picker activity with request code


    }


    public void editableOptionListener(String option, int position) {

        currentItemPosition = position;
        gridList.get(position).setText(option);

    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {

        if (requestCode == RC_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            images = (ArrayList<Image>) ImagePicker.getImages(data);
            printImages(images);
            return;
        }else if(requestCode == CONTACT_PICKER_RESULT){
            callCreatePoll();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void printImages(List<Image> images) {
        if (images == null) return;

        //TODO put this in background

        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0, l = images.size(); i < l; i++) {
            stringBuffer.append(images.get(i).getPath());
        }

        //TODO Send Base64 of this path
        gridList.get(currentItemPosition).setImage(stringBuffer.toString().replace("file:", ""));

        gridAdapter.notifyDataSetChanged();
    }

    public void showToastMessage() {
        Toast.makeText(activity, "Poll Created successfully", Toast.LENGTH_SHORT).show();
    }


}
