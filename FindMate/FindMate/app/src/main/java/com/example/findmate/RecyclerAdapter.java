package com.example.findmate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

    final RecyclerInterface rc_interface;
    ArrayList<RecyclerModel> user;

    Context context;

    public RecyclerAdapter(ArrayList<RecyclerModel> user, Community activity,RecyclerInterface rc_interface){
        this.user = user;
        this.context = activity;
        this.rc_interface = rc_interface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.community_item,parent,false);
        return new ViewHolder(view,rc_interface);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        RecyclerModel users = user.get(position);
        holder.name.setText(users.getName());
        holder.department.setText(users.getDepartment());
        holder.situation.setText(users.getSituation());
        if(users.getPhotoUri() != null)
            Glide.with(context).load(users.getPhotoUri()).into(holder.profile);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rc_interface != null){
                    int pos = position;
                    System.out.println(pos);
                    if(pos != RecyclerView.NO_POSITION){
                        rc_interface.onItemClick(pos);
                    }
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return user.size();
    }

    public void setArrayList(ArrayList<RecyclerModel> arrayList) {
        this.user = arrayList;
    }

    public ArrayList<RecyclerModel> getArrayList() {
        return user;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView profile;
        TextView name, department,situation;
        private CardView cardView;
        public ViewHolder(@NonNull View itemView,RecyclerInterface rc_interface) {
            super(itemView);
            profile = itemView.findViewById(R.id.profiles);
            name = itemView.findViewById(R.id.text_name);
            department = itemView.findViewById(R.id.text_department);
            situation = itemView.findViewById(R.id.text_situation);
            cardView = (CardView) itemView.findViewById(R.id.card_view);

        }
    }
}
