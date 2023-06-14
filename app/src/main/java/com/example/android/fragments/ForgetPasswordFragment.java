package com.example.android.fragments;

import static com.example.android.fragments.CreateAccountFragment.EMAIL_REGEX;

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

import com.example.android.fragmentReplaceActivity;
import com.example.android.socialme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordFragment extends Fragment {

    private TextView login;
    private Button recoverBtn;
    private EditText email;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    public ForgetPasswordFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forget_password, container, false);
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

        recoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail=email.getText().toString();
                if(textEmail.isEmpty() || textEmail.matches(EMAIL_REGEX)){
                    email.setError("please input valid Email");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(textEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(),"Password reset email sent successfully",Toast.LENGTH_LONG).show();
                            email.setText("");
                        }
                        else{
                            String errMsg = task.getException().getMessage();
                            Toast.makeText(getContext(), "Error: "+errMsg, Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
                }
        });
    }



    private void init(View view) {
        login = view.findViewById(R.id.backto);
        recoverBtn= view.findViewById(R.id.recoverbtn);
        email = view.findViewById(R.id.emailfpf);
        progressBar= view.findViewById(R.id.progressBarfpf);

        auth=FirebaseAuth.getInstance();
    }
}