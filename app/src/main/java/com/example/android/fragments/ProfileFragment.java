package com.example.android.fragments;

import static android.app.Activity.RESULT_OK;

import static com.example.android.MainActivity.User_ID;
import static com.example.android.MainActivity.isSearchUser;
import static com.example.android.utils.Constants.PREF_DIRECTORY;
import static com.example.android.utils.Constants.PREF_NAME;
import static com.example.android.utils.Constants.PREF_STORED;
import static com.example.android.utils.Constants.PREF_URL;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.MainActivity;
import com.example.android.Model.PostImageModel;
import com.example.android.chat.ChatActivity;
import com.example.android.socialme.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment{

    private TextView name,toolbarName,status,followingCount,followersCount,postCount;
    private CircleImageView profileImage;
    private Button followBtn,startChatBtn;
    private RecyclerView recyclerView;
    private FirebaseUser user;
    boolean isMyProfile = true;
    private LinearLayout countLayout;
    private String userUID;
    private FirestoreRecyclerAdapter<PostImageModel, PostImageHolder> adapter;
    private ImageButton editProfileButton;
    boolean isFollowed;
    DocumentReference userRef,myRef;
    List<Object> followerList,followingList,followingList_2;
    int count;

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

        myRef= FirebaseFirestore.getInstance().collection("Users").document(user.getUid());

        if(isSearchUser){
            userUID = User_ID;
            isMyProfile= false;
            loadData();
        }
        else{
            isMyProfile= true;
            userUID= user.getUid();
        }
        if(isMyProfile){
            editProfileButton.setVisibility(View.VISIBLE);
            followBtn.setVisibility(View.GONE);
            countLayout.setVisibility(View.VISIBLE);
            startChatBtn.setVisibility(View.GONE);
        }
        else{
            editProfileButton.setVisibility(View.GONE);
            followBtn.setVisibility(View.VISIBLE);
//            countLayout.setVisibility(View.GONE);

        }


        userRef= FirebaseFirestore.getInstance().collection("Users").document(userUID);


        loadBasicData();
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        loadPostImages();
        recyclerView.setAdapter(adapter);

        clickListner();
    }

    private void loadData() {
        myRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Log.e("Tag_b",error.getMessage() );
                    return;
                }
                if(value==null  || !value.exists()){
                    return;
                }
                followingList_2 = (List<Object>) value.get("following");

            }
        });
    }

    private void clickListner() {

        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFollowed){
                    followerList.remove(user.getUid());
                    followingList_2.remove(userUID);

                    final Map<String,Object> map_2=new HashMap<>();
                    map_2.put("following",followingList_2);

                    Map<String,Object> map=new HashMap<>();
                    map.put("follower",followerList);

                    userRef.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                followBtn.setText("Follow");
                                myRef.update(map_2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getContext(), "UnFollowed", Toast.LENGTH_SHORT).show();
                                            isFollowed=false;
                                        }
                                        else{
                                            Log.e("Tag_3", task.getException().getMessage() );
                                        }
                                    }
                                });
                            }
                            else{
                                Log.e("Tag",""+task.getException().getMessage());
                            }
                        }
                    });

                }
                else{

                    createNotification();
                    followerList.add(user.getUid());
                    followingList_2.add(userUID);

                    Map<String,Object> map_2=new HashMap<>();
                    map_2.put("following",followingList_2);

                    Map<String,Object> map=new HashMap<>();
                    map.put("follower",followerList);
                    userRef.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                followBtn.setText("UnFollow");
                                myRef.update(map_2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getContext(), "Followed", Toast.LENGTH_SHORT).show();
                                            isFollowed=true;
                                        }
                                        else{
                                            Log.e("Tag_4", task.getException().getMessage() );
                                        }
                                    }
                                });
                            }
                            else{
                                Log.e("Tag",""+task.getException().getMessage());
                            }
                        }
                    });

                }
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(getContext(),ProfileFragment.this);
            }
        });

        startChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryChat();
            }
        });
    }
    void queryChat() {

        assert getContext() != null;


        CollectionReference reference = FirebaseFirestore.getInstance().collection("Messages");
        reference.whereArrayContains("uid", userUID)
                .get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                QuerySnapshot snapshot = task.getResult();

                if (snapshot.isEmpty()) {
                    startChat();
                } else {
                    //get chatId and pass

                    for (DocumentSnapshot snapshotChat : snapshot) {

                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("uid", userUID);
                        intent.putExtra("id", snapshotChat.getId()); //return doc id
                        startActivity(intent);
                    }


                }

            }


        });

    }
    void startChat() {


        CollectionReference reference = FirebaseFirestore.getInstance().collection("Messages");

        List<String> list = new ArrayList<>();

        list.add(0, user.getUid());
        list.add(1, userUID);

        String pushID = reference.document().getId();

        Map<String, Object> map = new HashMap<>();
        map.put("id", pushID);
        map.put("lastMessage", "Hi");
        map.put("time", FieldValue.serverTimestamp());
        map.put("uid", list);

        reference.document(pushID).update(map).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                reference.document(pushID).set(map);
            }
        });

        CollectionReference messageRef = FirebaseFirestore.getInstance()
                .collection("Messages")
                .document(pushID)
                .collection("Messages");

        String messageID = messageRef.document().getId();

        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("id", messageID);
        messageMap.put("message", "Hi");
        messageMap.put("senderID", user.getUid());
        messageMap.put("time", FieldValue.serverTimestamp());

        messageRef.document(messageID).set(messageMap);

        new Handler().postDelayed(() -> {

            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("uid", userUID);
            intent.putExtra("id", pushID);
            startActivity(intent);

        }, 3000);

    }

    private void loadBasicData() {

        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.e("tag_0",error.getMessage() );
                    return;
                }
                assert value!=null;
                if(value.exists()){
                    String textname=value.getString("name");
                    String textstatus=value.getString("status");
                    String textprofileURL=value.getString("profileImage");

                    name.setText(textname);
                    toolbarName.setText(textname);
                    status.setText(textstatus);

                     followerList = (List<Object>) value.get("follower");
                     followingList = (List<Object>) value.get("following");


                    followersCount.setText(""+followerList.size());
                    followingCount.setText(""+followingList.size());

                    Glide.with(getContext().getApplicationContext())
                            .load(textprofileURL)
                            .placeholder(R.drawable.ic_baseline_account_circle_24)
                            .circleCrop()
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                                    storeProfileImage(bitmap,textprofileURL);
                                    return false;
                                }
                            })
                            .timeout(6500)
                            .into(profileImage);
                    if(followerList.contains(user.getUid())){
                        followBtn.setText("UnFollow");
                        isFollowed=true;
                        startChatBtn.setVisibility(View.VISIBLE);

                    }
                    else{
                        isFollowed=false;
                        followBtn.setText("Follow");
                        startChatBtn.setVisibility(View.GONE);
                    }
                }

            }
        });

    }

    private void storeProfileImage(Bitmap bitmap, String url){

        SharedPreferences preferences = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isStored = preferences.getBoolean(PREF_STORED,false);

        String urlString = preferences.getString(PREF_URL,"");
        SharedPreferences.Editor editor= preferences.edit();
        if(!isStored && urlString.equals(url))
            return;
        if(isSearchUser)
            return;
        ContextWrapper contextWrapper = new ContextWrapper(getContext().getApplicationContext());
        File directory = contextWrapper.getDir("image_data", Context.MODE_PRIVATE);

        if(!directory.exists()){
            directory.mkdir();
        }
        File path = new File(directory, "profile.png");
        FileOutputStream fileOutputStream=null;
        try {
            fileOutputStream = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                fileOutputStream.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        editor.putBoolean(PREF_STORED,true);
        editor.putString(PREF_URL,url);
        editor.putString(PREF_DIRECTORY, directory.getAbsolutePath());
        editor.apply();
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
        countLayout= view.findViewById(R.id.followlayout);
        editProfileButton= view.findViewById(R.id.editProfileImage);
        startChatBtn= view.findViewById(R.id.startChatBtn);
        FirebaseAuth auth= FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
    }

    private void loadPostImages() {

        DocumentReference reference = FirebaseFirestore.getInstance().collection("Users").document(userUID);
        Query query = reference.collection("Post Images");
        FirestoreRecyclerOptions<PostImageModel> options = new FirestoreRecyclerOptions.Builder<PostImageModel>()
                .setQuery(query, PostImageModel.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<PostImageModel, PostImageHolder>(options) {

            @NonNull
            @Override
            public PostImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_image_item, parent, false);
                return new PostImageHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PostImageHolder holder, int position, @NonNull PostImageModel model) {
                Glide.with(holder.itemView.getContext().getApplicationContext())
                        .load(model.getImageUrl())
                        .timeout(6500)
                        .into(holder.imageView);
                count= getItemCount();
                postCount.setText(""+ count);
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();

            }
        };




    }




    private class PostImageHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        public PostImageHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.ImageView1);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        isSearchUser=false;
        adapter.stopListening();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri uri=result.getUri();
            uploadImage(uri);
        }
    }

    private void uploadImage(Uri uri) {
        StorageReference reference = FirebaseStorage.getInstance().getReference().child("Profile Images");
        reference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl= uri.toString();
                            UserProfileChangeRequest.Builder request= new UserProfileChangeRequest.Builder();
                            request.setPhotoUri(uri);
                            user.updateProfile(request.build());
                            Map<String,Object> map = new HashMap<>();
                            map.put("profileImage",imageUrl);

                            FirebaseFirestore.getInstance().collection("Users")
                                    .document(user.getUid())
                                    .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        Toast.makeText(getContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(getContext(), "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
                else{
                    Toast.makeText(getContext(), "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void createNotification() {

        CollectionReference reference = FirebaseFirestore.getInstance().collection("Notifications");

        String id = reference.document().getId();
        Map<String, Object> map = new HashMap<>();
        map.put("time", FieldValue.serverTimestamp());
        map.put("notification", user.getDisplayName() + " followed you.");
        map.put("id", id);
        map.put("uid", userUID);


        reference.document(id).set(map);

    }
}