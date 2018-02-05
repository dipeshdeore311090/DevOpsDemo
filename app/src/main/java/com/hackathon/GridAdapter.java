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

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.MyViewHolder> {

    private List<GridModel> gridList;
    private Context context;
    MainActivity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView gridImage;
        //public RatingBar ratingView;
        private EditText editableOption;

        public MyViewHolder(View view) {
            super(view);
            activity = (MainActivity) context;
            gridImage = view.findViewById(R.id.img_pic);
            editableOption = view.findViewById(R.id.editable_option);
            //  ratingView = (RatingBar) view.findViewById(R.id.rating_for_poll);
        }
    }


    public GridAdapter(Context activity, List<GridModel> gridList) {
        this.gridList = gridList;
        this.context = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gird_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        GridModel gridModel = gridList.get(position);

        Bitmap bm = BitmapFactory.decodeFile(gridModel.getImage());
        holder.gridImage.setImageBitmap(bm);
        holder.editableOption.setText(gridModel.getText());

        holder.gridImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.cameraClicked(position);
            }
        });
        holder.editableOption.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    activity.editableOptionListener(s.toString(),position);
                }

            }
        });

        //Picasso.with(context).load(gridModel.getImage()).placeholder(R.drawable.add_picture).into(holder.gridImage);
    }



    @Override
    public int getItemCount() {
        return gridList.size();
    }
}
