package com.hackathon;

/**
 * Created by dipeshd on 8/17/2017.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatRatingBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.List;

import android.support.v7.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.MyViewHolder> {

    private List<GridModel> gridList;
    private Context context;
    ResultActivity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView gridImage;
        private TextView txtOptions;
        private TextView txtPercentage;
        private View viewRange;

        public MyViewHolder(View view) {
            super(view);
            activity = (ResultActivity) context;
            gridImage = view.findViewById(R.id.img_pic);
            txtOptions = view.findViewById(R.id.txt_option);
            txtPercentage = view.findViewById(R.id.txt_percentage);
            viewRange = view.findViewById(R.id.view_selected);
        }
    }


    public ResultAdapter(Context activity, List<GridModel> gridList) {
        this.gridList = gridList;
        this.context = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        GridModel gridModel = gridList.get(position);

//        Picasso.with(context).load(gridModel.getImage()).placeholder(R.drawable.add_picture).into(holder.gridImage);
        holder.txtOptions.setText(gridModel.getText());
        holder.txtPercentage.setText(""+ gridModel.getPercentage() + "%");
        holder.viewRange.getLayoutParams().width = ((int)gridModel.getPercentage()) * 3;

    }


    @Override
    public int getItemCount() {
        return gridList.size();
    }
}
