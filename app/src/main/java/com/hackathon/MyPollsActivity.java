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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MyPollsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PollsAdapter gridAdapter;
    private List<PollsModel> pollsList;
    private Activity activity;
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json; charset=utf-8");
    private ProgressDialog progessDialog;
    private static final String TAG = MyPollsActivity.class.getSimpleName();
    public static OkHttpClient client = new OkHttpClient();
    private Handler mHandler;
    public SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        activity = this;
        mHandler = new Handler(Looper.getMainLooper());

        sharedPref = getApplicationContext().getSharedPreferences("Hackathon", Context.MODE_PRIVATE);
        Log.d(TAG, "UserId => " + sharedPref.getString(AppConstants.UserID, ""));
        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent i = new Intent(activity, ResultActivity.class);
                i.putExtra("PollId", pollsList.get(position).getId());
                startActivity(i);
            }

        }));

        getMyPolls();
    }

    private void getMyPolls() {
        try {
            progessDialog = new ProgressDialog(activity);
            progessDialog.setMessage("Loading...");
            progessDialog.setCancelable(false);
            progessDialog.show();

            // TODO change url
            Request request = new Request.Builder()
                    .url("https://pickforme.azurewebsites.net/api/PollDetails/GetMyNotification?UserId=" + sharedPref.getString(AppConstants.UserID, ""))
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

                                GsonBuilder gsonBuilder = new GsonBuilder();
                                Type collectionType = new TypeToken<List<PollsModel>>() {
                                }.getType();
                                Gson gson = gsonBuilder.create();
                                pollsList = gson.fromJson(responseStr, collectionType);

                                gridAdapter = new PollsAdapter(activity, pollsList);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(gridAdapter);

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
