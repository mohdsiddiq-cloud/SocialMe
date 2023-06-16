package com.example.android.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.Model.HomeModel;
import com.example.android.adapter.HomeAdapter;
import com.example.android.socialme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    HomeAdapter homeAdapter;
    private List<HomeModel> list;
    private FirebaseUser user;
    public static int LIST_SIZE=0;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);


        list=new ArrayList<>();
        homeAdapter=new HomeAdapter(list,getContext());
        recyclerView.setAdapter(homeAdapter);
        loadDataFromFirestore();
    }

    private void loadDataFromFirestore() {
        if (user == null) {
            Log.d("Error: ", "User is null");
            return;
        }
        CollectionReference reference=FirebaseFirestore.getInstance().collection("Users")
                .document(user.getUid())
                .collection("Post Images");

        reference.addSnapshotListener(((value, error) -> {
            if(error!=null){
                Log.e("Error: ",error.getMessage());
                return;
            }
            if(value==null)
                return;
            list.clear();
            for(QueryDocumentSnapshot snapshot: value){
                if(!snapshot.exists())
                    return;
                HomeModel model= snapshot.toObject(HomeModel.class);
                list.add(new HomeModel(
                        model.getUserName(),
                        model.getProfileImage(),
                        model.getImageUrl(),
                        model.getUid(),
                        model.getComments(),
                        model.getDescription(),
                        model.getId(),
                        model.getTimeStamp(),
                        model.getLikeCount()
                ));
            }
            homeAdapter.notifyDataSetChanged();
            LIST_SIZE=list.size();
        }));


    }

    private void init(View view) {
        Toolbar toolbar=view.findViewById(R.id.toolbar);
        if(getActivity()!=null)
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseAuth auth= FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }
}