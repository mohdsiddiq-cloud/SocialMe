package com.example.android.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.Model.NotificationModel;
import com.example.android.adapter.NotificationAdapter;
import com.example.android.socialme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class NotificationFragment extends Fragment {
    RecyclerView recyclerView;
    NotificationAdapter adapter;
    List<NotificationModel> list;
    FirebaseUser user;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }
    void init(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        list = new ArrayList<>();
        adapter = new NotificationAdapter(getContext(), list);

        recyclerView.setAdapter(adapter);

        user = FirebaseAuth.getInstance().getCurrentUser();

    }

    void loadNotification() {

        CollectionReference reference = FirebaseFirestore.getInstance().collection("Notifications");

        reference.whereEqualTo("uid", user.getUid())
                .orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {

                    if (error != null)
                        return;


                    if (value.isEmpty()) return;


                    list.clear();
                    for (QueryDocumentSnapshot snapshot : value) {

                        NotificationModel model = snapshot.toObject(NotificationModel.class);
                        list.add(model);

                    }
                    adapter.notifyDataSetChanged();


                });

    }
}
