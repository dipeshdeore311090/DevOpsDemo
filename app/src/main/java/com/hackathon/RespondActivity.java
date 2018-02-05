package com.hackathon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
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

import com.google.gson.Gson;

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

public class RespondActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RespondAdapter respondAdapter;
    private List<GridModel> gridList;
    private Activity activity;

    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json; charset=utf-8");
    private ProgressDialog progessDialog;
    private static final String TAG = RespondActivity.class.getSimpleName();
    private ImageView imgSend;
    private TextView txtQuestion;
    private String pollId;
    public SharedPreferences sharedPref;
    public static OkHttpClient client = new OkHttpClient();
    private PollResponseModel pollResponseModel = new PollResponseModel();
    PollDetailsModel pollDetailsModel;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respond);

        activity = this;
        mHandler = new Handler(Looper.getMainLooper());

        sharedPref = getApplicationContext().getSharedPreferences("Hackathon", Context.MODE_PRIVATE);
        Log.d(TAG, "UserId ===>" + sharedPref.getString(AppConstants.UserID, ""));
        //ba6ee70e251d450ea8966aa06d544dc4

        if (getIntent().getStringExtra("PollId") != null) {
            Log.d(TAG, "PollId => " + getIntent().getStringExtra("PollId"));
            pollId = getIntent().getStringExtra("PollId");
        }
        //pollId = "986e3c781c494c619e4d21a725c00d2f";
        Log.d(TAG, "UserId => " + sharedPref.getString(AppConstants.UserID, ""));

        txtQuestion = findViewById(R.id.txt_question);
        recyclerView = findViewById(R.id.recycler_view);
        imgSend = findViewById(R.id.img_send);
        imgSend.setOnClickListener(new SendListener());
        gridList = new ArrayList<>();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                gridList.get(position).setLike(true);
                pollResponseModel.setSelectedOptionId(pollDetailsModel.getOptions().get(position).getId());
                respondAdapter.notifyDataSetChanged();
            }

        }));

        getDataFrompollId();

    }

    private class SendListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            pollResponseModel.setComment("");
            pollResponseModel.setPollId(pollId);
            pollResponseModel.setResponderId(sharedPref.getString(AppConstants.UserID, ""));
            postResponse();
        }
    }

    private void getDataFrompollId() {
        try {
            progessDialog = new ProgressDialog(activity);
            progessDialog.setMessage("Loading...");
            progessDialog.setCancelable(false);
            progessDialog.show();

            Request request = new Request.Builder()
                    .url("https://pickforme.azurewebsites.net/api/Polldetails/GetPollDetails?polId=" + pollId)
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
                                respondAdapter = new RespondAdapter(activity, gridList);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(respondAdapter);
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("response:" + e.getMessage());
        }
    }


    private void postResponse() {
        try {
            progessDialog = new ProgressDialog(activity);
            progessDialog.setMessage("Loading...");
            progessDialog.setCancelable(false);
            progessDialog.show();

            Gson g = new Gson();
            String jsonBody = g.toJson(pollResponseModel);
            Log.d(TAG,"RequestBody:"+jsonBody);

            Request request = new Request.Builder()
                    .url("https://pickforme.azurewebsites.net/api/PollResult/PostPollResult")
                    .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, jsonBody))
                    .header("ZUMO-API-VERSION", "2.0.0").header("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    progessDialog.dismiss();
                    System.out.println("onFailure => " + e.getMessage().toString());
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
                                Toast.makeText(activity,"Response posted successfully",Toast.LENGTH_SHORT).show();
                                activity.finish();
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
