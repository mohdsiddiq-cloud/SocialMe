package com.example.android.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.MainActivity;
import com.example.android.socialme.R;
import com.example.android.fragmentReplaceActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateAccountFragment extends Fragment {

    private EditText name,email,password1,password2;
    private TextView login;
    private Button signup;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    public static final String EMAIL_REGEX="^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";

    CreateAccountFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        clickListener();
    }

    private void clickListener() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((fragmentReplaceActivity) getActivity()).setFragment(new LoginFragment());
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textName= name.getText().toString();
                String textEmail=email.getText().toString();
                String textpassword=password1.getText().toString();
                String textpassword2=password2.getText().toString();
                if(textName.isEmpty() || textName.equals(" ")){
                    name.setError("please input valid String");
                    return;
                }
                if(textEmail.isEmpty() || textEmail.matches(EMAIL_REGEX)){
                    email.setError("please input valid Email");
                    return;
                }
                if(textpassword.isEmpty() || textpassword.length()<6){
                    password1.setError("please input valid Password");
                    return;
                }
                if(!textpassword.equals(textpassword2)){
                    password2.setError("Password not match");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                createAccount(textName,textEmail,textpassword);
            }

            private void createAccount(String textName, String textEmail, String textpassword) {
                auth.createUserWithEmailAndPassword(textEmail,textpassword).addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    FirebaseUser user= auth.getCurrentUser();
                                    UserProfileChangeRequest.Builder request= new UserProfileChangeRequest.Builder();
                                    request.setDisplayName(textName);
                                    user.updateProfile(request.build());
                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(getContext(), "Email Verification Link Send", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    uploadUser(user,textName,textEmail);
                                }
                                else{
                                    progressBar.setVisibility(View.GONE);
                                    String exception= task.getException().getMessage();
                                    Toast.makeText(getContext(),"Error: "+ exception,Toast.LENGTH_LONG).show();
                                }
                            }

                            private void uploadUser(FirebaseUser user, String textName, String textEmail) {
                                List<String> list=new ArrayList<>();
                                List<String> list1=new ArrayList<>();

                                Map<String,Object> map=new HashMap<String, Object>();
                                map.put("name",textName);
                                map.put("email",textEmail);
                                map.put("profileImage"," ");
                                map.put("uid",auth.getUid());
                                map.put("following",list1);
                                map.put("follower",list);
                                map.put("status"," ");
                                map.put("search",textName.toLowerCase());




                                FirebaseFirestore.getInstance().collection("Users").document(user.getUid()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            assert getActivity()!=null;
                                            progressBar.setVisibility(View.GONE);
                                            startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                                            getActivity().finish();
                                        }
                                        else{
                                            progressBar.setVisibility(View.GONE);
                                            String exception= task.getException().getMessage();
                                            Toast.makeText(getContext(),"Error: "+exception,Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }
                );
            }
        });
    }

    private void init(View view) {
        name=view.findViewById(R.id.nameca);
        email=view.findViewById(R.id.emailca);
        password1=view.findViewById(R.id.passwordca);
        password2=view.findViewById(R.id.password2ca);
        login=view.findViewById(R.id.loginca);
        signup=view.findViewById(R.id.signupca);
        progressBar=view.findViewById(R.id.progressBar);
        auth=FirebaseAuth.getInstance();
    }
}