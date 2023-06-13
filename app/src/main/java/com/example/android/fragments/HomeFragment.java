package com.example.android.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.TintInfo;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.Model.HomeModel;
import com.example.android.adapter.HomeAdapter;
import com.example.android.socialme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    HomeAdapter homeAdapter;
    private List<HomeModel> list;
    private FirebaseUser user;
    DocumentReference reference;
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
        reference = FirebaseFirestore.getInstance().collection("Posts").document(user.getUid());

        list=new ArrayList<>();
        homeAdapter=new HomeAdapter(list,getContext());
        recyclerView.setAdapter(homeAdapter);
        loadDataFromFirestore();

    }

    private void loadDataFromFirestore() {
        list.add(new HomeModel("Mohd Siddiq","6/13/2023","","","123456",23));
        list.add(new HomeModel("Wasim Raja","6/13/2023","","","123457",32));
        list.add(new HomeModel("Hamza khan","6/13/2023","","","123456",23));
        list.add(new HomeModel("Aditya Raj","6/13/2023","","","123456",89));
        homeAdapter.notifyDataSetChanged();
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseAuth auth= FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }
}