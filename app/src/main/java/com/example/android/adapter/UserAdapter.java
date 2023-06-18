package com.example.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.Model.Users;
import com.example.android.socialme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {

    private List<Users> list;
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    OnUserClicked onUserClicked;

    public UserAdapter(List<Users> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false);
       return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {

        if(list.get(position).getUid().equals(user.getUid())){
            holder.layout.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
        }
        else{
            holder.layout.setVisibility(View.VISIBLE);
        }
        holder.name.setText(list.get(position).getName());
        holder.status.setText(list.get(position).getStatus());
        Glide.with(holder.itemView.getContext().getApplicationContext())
                .load(list.get(position).getProfileImage())
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .timeout(6500)
                .into(holder.profileImage);

        holder.clickLister(list.get(position).getUid());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class UserHolder extends RecyclerView.ViewHolder{

        private CircleImageView profileImage;
        private TextView name,status;
        private RelativeLayout layout;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            name= itemView.findViewById(R.id.name);
            status=itemView.findViewById(R.id.status);
            layout=itemView.findViewById(R.id.relativeLayout);

        }


        private void clickLister(final String uid) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onUserClicked.onClicked(uid);
                }
            });
        }
    }
    public void OnUserClicked(OnUserClicked onUserClicked){
        this.onUserClicked=onUserClicked;
    }
    public interface OnUserClicked{
        void onClicked(String uid);
    }
}
