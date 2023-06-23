package com.example.android.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.Model.HomeModel;
import com.example.android.adapter.HomeAdapter;
import com.example.android.chat.ChatUserActivity;
import com.example.android.socialme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    public HomeAdapter homeAdapter;
    private List<HomeModel> list;
    private FirebaseUser user;
    private final MutableLiveData<Integer> commentCount= new MutableLiveData<>();
    RecyclerView storyRecyclerView;


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
        homeAdapter=new HomeAdapter(list,getActivity());
        recyclerView.setAdapter(homeAdapter);

        loadDataFromFirestore();

        homeAdapter.OnPressed(new HomeAdapter.OnPressed() {
            @Override
            public void onLiked(int position, String id,String uid,List<String> likeList,boolean isChecked) {
                DocumentReference reference = FirebaseFirestore.getInstance().collection("Users")
                        .document(uid).collection("Post Images").document(id);
                if(likeList.contains(user.getUid())) {
                    likeList.remove(user.getUid());
                }
                else {
                    likeList.add(user.getUid());
                }
                Map<String,Object> map= new HashMap<>();
                map.put("likes",likeList);
                reference.update(map);
            }

            @Override
            public void setCommentCount(TextView textView) {

                Activity activity = getActivity();
                assert activity != null;
                commentCount.observe((LifecycleOwner) activity, integer -> {
                    assert commentCount.getValue() != null;
                    if(commentCount.getValue()==0)
                        textView.setVisibility(View.GONE);
                    else
                        textView.setVisibility(View.VISIBLE);
                    StringBuilder builder = new StringBuilder();
                    builder.append("See all")
                            .append(commentCount.getValue())
                            .append(" comments");

                    textView.setText(builder);
                });

            }
        });
        view.findViewById(R.id.sendButton).setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), ChatUserActivity.class);
            startActivity(intent);

        });
    }



    private void loadDataFromFirestore() {
        if (user == null) {
            Log.d("Error: ", "User is null");
            return;
        }
        final DocumentReference reference=FirebaseFirestore.getInstance().collection("Users")
                .document(user.getUid());
        final CollectionReference collectionReference=FirebaseFirestore.getInstance().collection("Users");
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Log.e("Error: ",error.getMessage());
                    return;
                }
                if(value==null)
                    return;

                List<String> uidList= (List<String>) value.get("following");
                if(uidList==null || uidList.isEmpty())
                    return;

                    collectionReference.whereIn("uid", uidList).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value1, @Nullable FirebaseFirestoreException error1) {
                            if (error1 != null) {
                                Log.e("Error: ", error1.getMessage());
                                return;
                            }
                            if(value1==null)
                                return;

                            for (QueryDocumentSnapshot snapshot : value1) {
                                snapshot.getReference().collection("Post Images")
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot value2, @Nullable FirebaseFirestoreException error2) {
                                                if (error2 != null) {
                                                    Log.e("Error: ", error2.getMessage());
                                                    return;
                                                }
                                                if(value2==null)
                                                    return;
                                                list.clear();

                                                for (QueryDocumentSnapshot snapshot2 : value2) {
                                                    if (!snapshot2.exists())
                                                        return;
                                                    HomeModel model = snapshot2.toObject(HomeModel.class);
                                                    Log.e("My name", model.getUid().toString() );
                                                    list.add(new HomeModel(
                                                            model.getName(),
                                                            model.getProfileImage(),
                                                            model.getImageUrl(),
                                                            model.getUid(),
                                                            model.getDescription(),
                                                            model.getId(),
                                                            model.getTimeStamp(),
                                                            model.getLikes()
                                                    ));
                                                    snapshot2.getReference().collection("Comments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if(task.isSuccessful()){
                                                                Map<String, Object> map = new HashMap<>();
                                                                for (QueryDocumentSnapshot commentSnapshot : task
                                                                        .getResult()) {
                                                                    map = commentSnapshot.getData();
                                                                }
                                                                commentCount.setValue(map.size());
                                                            }
                                                        }
                                                    });
                                                }
                                                homeAdapter.notifyDataSetChanged();

                                            }
                                        });
                            }
                        }
                    });
                }

        });

    }


    private void init(View view) {
        Toolbar toolbar=view.findViewById(R.id.toolbar);
        if(getActivity()!=null)
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        storyRecyclerView= view.findViewById(R.id.storiesRecyclerView);
        storyRecyclerView.setHasFixedSize(true);
        storyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,true));


        FirebaseAuth auth= FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

    }
}