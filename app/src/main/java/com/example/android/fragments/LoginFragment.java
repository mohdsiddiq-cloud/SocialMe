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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class LoginFragment extends Fragment {
    private EditText email,password;
    private TextView signUp,forgotPassword;
    private Button loginBtn,googleSignInBtn;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private static final int RC_SIGN_IN=1;
    private GoogleSignInClient mGoogleSignInClient;
    public static final String EMAIL_REGEX="^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        clickListner();
    }



    private void clickListner() {

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((fragmentReplaceActivity) getContext()).setFragment(new ForgetPasswordFragment());
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail=email.getText().toString();
                String textpassword=password.getText().toString();
                if(textEmail.isEmpty() || textEmail.matches(EMAIL_REGEX)){
                    email.setError("please input valid Email");
                    return;
                }
                if(textpassword.isEmpty() || textpassword.length()<6){
                    password.setError("please input valid Password");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                auth.signInWithEmailAndPassword(textEmail,textpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user= auth.getCurrentUser();
                            if(!user.isEmailVerified()){
                                Toast.makeText(getContext(), "Please Verify your Email", Toast.LENGTH_SHORT).show();
                            }

                            sendUserToMainActivity();
                        }
                        else{
                            String exception= task.getException().getMessage();
                            Toast.makeText(getContext(), "Error: "+exception, Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((fragmentReplaceActivity) getActivity()).setFragment(new CreateAccountFragment());
            }
        });
    }


    private void init(View view) {
        email=view.findViewById(R.id.emailla);
        password=view.findViewById(R.id.passwordla);
        loginBtn=view.findViewById(R.id.loginla);
        googleSignInBtn=view.findViewById(R.id.signinggl);
        signUp=view.findViewById(R.id.createla);
        forgotPassword=view.findViewById(R.id.forgetps);
        progressBar=view.findViewById(R.id.progressBar);
        auth= FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(),gso);
    }


    private void sendUserToMainActivity() {
        if(getActivity()==null)
            return;
        progressBar.setVisibility(View.GONE);
        startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
        getActivity().finish();
    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        progressBar.setVisibility(View.GONE);
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account= task.getResult(ApiException.class);
                assert account!=null;
                firebaseAuthWithGoogle(account.getIdToken());
            }catch (ApiException e){
                e.printStackTrace();
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        auth.signInWithCredential(credential).addOnCompleteListener(getActivity(),new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user=auth.getCurrentUser();
                    UpdateUi(user);
                }
                else{

                }
            }

            private void UpdateUi(FirebaseUser user) {

                GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(getActivity());

                Map<String,Object> map=new HashMap<String, Object>();
                map.put("name",account.getDisplayName());
                map.put("email",account.getEmail());
                map.put("profileImage",String.valueOf(account.getPhotoUrl()));
                map.put("uid",auth.getUid());
                map.put("following",0);
                map.put("status"," ");
                map.put("follower",0);

                FirebaseFirestore.getInstance().collection("Users").document(user.getUid()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            assert getActivity()!=null;
                            progressBar.setVisibility(View.GONE);
                            sendUserToMainActivity();
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            String exception= task.getException().getMessage();
                            Toast.makeText(getContext(),"Error: "+exception,Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}