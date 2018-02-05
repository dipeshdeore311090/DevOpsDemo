package com.hackathon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ResultActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ResultAdapter resultAdapter;
    private List<GridModel> gridList;
    private Activity activity;

    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json; charset=utf-8");
    private ProgressDialog progessDialog;
    private static final String TAG = ResultActivity.class.getSimpleName();
    private TextView txtQuestion;
    public SharedPreferences sharedPref;
    public static OkHttpClient client = new OkHttpClient();
    PollDetailsModel pollDetailsModel;
    private Handler mHandler;
    private String pollId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        activity = this;
        mHandler = new Handler(Looper.getMainLooper());

        sharedPref = getApplicationContext().getSharedPreferences("Hackathon", Context.MODE_PRIVATE);
        Log.d(TAG, "UserId ===>" + sharedPref.getString(AppConstants.UserID, ""));

        if (getIntent().getStringExtra("PollId") != null) {
            Log.d(TAG, "PollId => " + getIntent().getStringExtra("PollId"));
            pollId = getIntent().getStringExtra("PollId");
        }

        txtQuestion = findViewById(R.id.txt_question);
        recyclerView = findViewById(R.id.recycler_view);

        getResultFromPollId();
    }

    private void getResultFromPollId() {
        try {
            progessDialog = new ProgressDialog(activity);
            progessDialog.setMessage("Loading...");
            progessDialog.setCancelable(false);
            progessDialog.show();

            // TODO change url for result
            Request request = new Request.Builder()
                    .url("https://pickforme.azurewebsites.net/api/PollDetails/GetPollResult?pollId=" + pollId)
                    .header("ZUMO-API-VERSION", "2.0.0").header("Content-Type", "application/json")
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
                        final String responseStr = responseBody.string();
                        System.out.println("Response => " + responseStr);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Gson gson = new Gson();
                                pollDetailsModel = gson.fromJson(responseStr, PollDetailsModel.class);
                                txtQuestion.setText(pollDetailsModel.getQuestionText());
                                gridList = pollDetailsModel.getOptions();
                                resultAdapter = new ResultAdapter(activity, gridList);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(resultAdapter);
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("response:" + e.getMessage());
        }
    }
}
