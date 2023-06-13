package com.example.android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.Model.HomeModel;
import com.example.android.socialme.R;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeHolder>{

    private List<HomeModel> list;
    Context context;

    public HomeAdapter(List<HomeModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item,parent,false);
        return new HomeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeHolder holder, int position) {
        holder.userName.setText(list.get(position).getUserName());
        holder.time.setText(list.get(position).getTimeStamp());
        int count= list.get(position).getLikeCount();
        if(count==0){
            holder.likeCount.setText(View.INVISIBLE);
        }
        else if(count==1){
            holder.likeCount.setText(count+" like");
        }
        else{
            holder.likeCount.setText(count+" likes");
        }

        Random random= new Random();
        int color= Color.argb(255,random.nextInt(256),random.nextInt(256),random.nextInt(256));

        Glide.with(context.getApplicationContext())
                .load(list.get(position).getProfileImage())
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .timeout(6500)
                .into(holder.profileImage);
        Glide.with(context.getApplicationContext())
                .load(list.get(position).getPostImage())
                .placeholder(new ColorDrawable(color))
                .timeout(6500)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class HomeHolder extends RecyclerView.ViewHolder{

        private CircleImageView profileImage;
        private TextView userName,time,likeCount;
        private ImageView imageView;
        private ImageButton likeButton,shareButton,commentButton;


        public HomeHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImg);
            userName = itemView.findViewById(R.id.namehi);
            time = itemView.findViewById(R.id.timehi);
            likeCount = itemView.findViewById(R.id.likecount);
            imageView = itemView.findViewById(R.id.imageView);
            likeButton = itemView.findViewById(R.id.likebtn);
            shareButton = itemView.findViewById(R.id.sharebtn);
            commentButton = itemView.findViewById(R.id.commentbtn);
        }
    }

}
