package com.example.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.Model.HomeModel;
import com.example.android.fragmentReplaceActivity;
import com.example.android.socialme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeHolder>{

    private final List<HomeModel> list;
    Activity context;
    OnPressed onPressed;


    public HomeAdapter(List<HomeModel> list, Activity context) {
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
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

        holder.userName.setText(list.get(position).getName());
        holder.time.setText(""+list.get(position).getTimeStamp());
        List<String> likeList= list.get(position).getLikes();
        int count=likeList.size();
        if(count==0){
            holder.likeCount.setText("");
        }
        else if(count==1){
            holder.likeCount.setText(count+" like");
        }
        else{
            holder.likeCount.setText(count+" likes");
        }
        assert user!=null;
        holder.likeCheckBox.setChecked(likeList.contains(user.getUid()));

        holder.description.setText(list.get(position).getDescription());

        Random random= new Random();
        int color= Color.argb(255,random.nextInt(256),random.nextInt(256),random.nextInt(256));

        Glide.with(context.getApplicationContext())
                .load(list.get(position).getProfileImage())
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .timeout(6500)
                .into(holder.profileImage);
        Glide.with(context.getApplicationContext())
                .load(list.get(position).getImageUrl())
                .placeholder(new ColorDrawable(color))
                .timeout(7000)
                .into(holder.imageView);
        holder.clickListner(position,
                list.get(position).getId(),
                list.get(position).getName(),
                list.get(position).getUid(),
                list.get(position).getLikes(),
                list.get(position).getImageUrl()
        );

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class HomeHolder extends RecyclerView.ViewHolder{

        private final CircleImageView profileImage;
        private final TextView userName,time,likeCount,description;
        private final CheckBox likeCheckBox;
        private final ImageView imageView;
        private final ImageButton shareButton,commentButton;
        TextView commentTv;


        public HomeHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImg5);
            userName = itemView.findViewById(R.id.namehi);
            time = itemView.findViewById(R.id.timehi);
            likeCount = itemView.findViewById(R.id.likecount);
            imageView = itemView.findViewById(R.id.imageView);
            likeCheckBox= itemView.findViewById(R.id.likebtn);
            shareButton = itemView.findViewById(R.id.sharebtn);
            commentButton = itemView.findViewById(R.id.commentbtn);
            description = itemView.findViewById(R.id.deschi);
            commentTv=itemView.findViewById(R.id.commentTv);

            onPressed.setCommentCount(commentTv);

        }

        public void clickListner(int position, String id, String name,final String uid,final List<String> likes,String imageUrl) {

            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent= new Intent(context, fragmentReplaceActivity.class);
                    intent.putExtra("id",id);
                    intent.putExtra("uid",uid);
                    intent.putExtra("isComment",true);
                    context.startActivity(intent);
                }
            });

            likeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    onPressed.onLiked(position,id,uid,likes,b);
                }
            });

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent= new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT,imageUrl);
                    context.startActivity(Intent.createChooser(intent,"Share link using..."));
                }
            });



        }
    }
    public interface OnPressed{
        void onLiked(int position,String id,String uid,List<String> likeList,boolean isChecked);
//        void onComment(int position, String id, String uid, String comment, LinearLayout commentLayout, EditText editText);
        void setCommentCount(TextView textView);
    }
    public void OnPressed(OnPressed onPressed){
        this.onPressed= onPressed;
    }


}
