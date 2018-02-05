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

public class RespondAdapter extends RecyclerView.Adapter<RespondAdapter.MyViewHolder> {

    private List<GridModel> gridList;
    private Context context;
    RespondActivity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView gridImage;
        private TextView txtOptions;
        private View viewSelected;

        public MyViewHolder(View view) {
            super(view);
            activity = (RespondActivity) context;
            gridImage = view.findViewById(R.id.img_pic);
            txtOptions = view.findViewById(R.id.txt_option);
            viewSelected = view.findViewById(R.id.view_selected);
        }
    }


    public RespondAdapter(Context activity, List<GridModel> gridList) {
        this.gridList = gridList;
        this.context = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.response_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        GridModel gridModel = gridList.get(position);

        //Picasso.with(context).load("").placeholder(R.drawable.add_picture).into(holder.gridImage);
        holder.txtOptions.setText(gridModel.getText());
        if (gridModel.isLike()) {
            holder.viewSelected.setVisibility(View.VISIBLE);
        } else {
            holder.viewSelected.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return gridList.size();
    }
}
