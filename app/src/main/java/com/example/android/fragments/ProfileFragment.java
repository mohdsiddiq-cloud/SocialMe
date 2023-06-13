package com.example.android.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.socialme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.ktx.Firebase;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private TextView name,toolbarName,status,followingCount,followersCount,postCount;
    private CircleImageView profileImage;
    private Button followBtn;
    private RecyclerView recyclerView;
    private FirebaseUser user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        loadBasicData();
    }

    private void loadBasicData() {
        DocumentReference userRef= FirebaseFirestore.getInstance().collection("Users").document(user.getUid());
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    return;
                }
                assert value!=null;
                if(value.exists()){
                    String textname=value.getString("name");
                    String textstatus=value.getString("status");
                    int textfollowers=value.getLong("follower").intValue();
                    int textfollowing=value.getLong("following").intValue();
                    String textprofileURL=value.getString("profileImage");

                    name.setText(textname);
                    toolbarName.setText(textname);
                    status.setText(textstatus);
                    followersCount.setText(String.valueOf(textfollowers));
                    followingCount.setText(String.valueOf(textfollowing));
                    Glide.with(getContext().getApplicationContext())
                            .load(textprofileURL)
                            .placeholder(R.drawable.ic_baseline_account_circle_24)
                            .timeout(6500)
                            .into(profileImage);
                }
            }
        });
    }

    private void init(View view) {

        Toolbar toolbar= view.findViewById(R.id.toolbarfp);
        assert getActivity() != null;
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        name= view.findViewById(R.id.namepf);
        toolbarName= view.findViewById(R.id.toolbarName);
        status= view.findViewById(R.id.aboutpf);
        followersCount=view.findViewById(R.id.followerCount);
        followingCount=view.findViewById(R.id.followingCount);
        postCount= view.findViewById(R.id.postCount);
        profileImage=view.findViewById(R.id.profileImage);
        followBtn=view.findViewById(R.id.followbtn);
        recyclerView=view.findViewById(R.id.recyclerViewpf);

        FirebaseAuth auth= FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
    }
}