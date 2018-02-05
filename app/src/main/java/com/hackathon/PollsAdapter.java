package com.hackathon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by dipeshd on 11/14/2017.
 */

public class PollsAdapter extends RecyclerView.Adapter<PollsAdapter.MyViewHolder> {

    private List<PollsModel> contactModelList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtContactName;
        public MyViewHolder(View view) {
            super(view);
            txtContactName = view.findViewById(R.id.txt_contact_name);
        }
    }


    public PollsAdapter(Context activity, List<PollsModel> contactList) {
        this.contactModelList = contactList;
        this.context = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PollsModel pollsModel = contactModelList.get(position);
        holder.txtContactName.setText(pollsModel.getQuestionText());

    }

    @Override
    public int getItemCount() {
        return contactModelList.size();
    }
}
