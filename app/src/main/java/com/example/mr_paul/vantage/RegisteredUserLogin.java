package com.jyotirmoy.mr_paul.vantage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static java.lang.Math.min;

public class RegisteredUserLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_user_login);

        final SharedPreferences sharedPreferences = getSharedPreferences("userInformation",MODE_PRIVATE);

        // refer to the widgets
        final EditText userEmailInput = findViewById(R.id.user_email);
        final EditText userPinInput = findViewById(R.id.user_pin);
        TextView forgotPassword = findViewById(R.id.forgot_password);
        Button logMeIn = findViewById(R.id.log_me_in);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // read the user entered email and send verification link to it
                String userEmail = userEmailInput.getText().toString().trim();
                if(!isValidEmail(userEmail)){
                    // set error
                    userEmailInput.setError("Invalid Email");
                    userEmailInput.requestFocus();
                    return;
                }


                // show an progress dialog
                final ProgressDialog pd = new ProgressDialog(RegisteredUserLogin.this);
                pd.setMessage("Sending Email....");
                pd.show();


                FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                pd.cancel();

                                if(task.isSuccessful()){
                                    Toast.makeText(RegisteredUserLogin.this,
                                            "Please check your email!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });

        logMeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // show an progress dialog
                final ProgressDialog pd = new ProgressDialog(RegisteredUserLogin.this);
                pd.setMessage("Logging In....");
                pd.show();

                final String userEmail = userEmailInput.getText().toString().trim();
                final String password = userPinInput.getText().toString().trim();

                if(!isValidEmail(userEmail)){
                    userEmailInput.setError("Invalid Email");
                    userEmailInput.requestFocus();
                    return;
                }

                if(password.length() == 0){
                    userPinInput.setError("Can't be empty!");
                    userPinInput.requestFocus();
                    return;
                }


                FirebaseAuth.getInstance().signInWithEmailAndPassword(userEmail, password)
                        .addOnCompleteListener(RegisteredUserLogin.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                pd.cancel();

                                if(task.isSuccessful()){

                                    // check if user is authenticated
                                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if(user.isEmailVerified()){
                                        // call intent to main activity and save data in the shared preference

                                        // write to the sharedPreference
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("userName",user.getDisplayName());
                                        editor.putString("userEmail",userEmail);
                                        editor.putString("password",password);
                                        editor.putBoolean("isFirstTimeUser",false);
                                        editor.putBoolean("isNotVerified",false);
                                        editor.putString("userUID",user.getUid());
                                        editor.putString("userUniqueKey",getUniqueUserKey(user.getUid()));
                                        // user name and email is already written to the shared preference
                                        editor.apply();


                                        startActivity(new Intent(RegisteredUserLogin.this, MainActivity.class));
                                        RegisteredUserLogin.this.finish();

                                    }
                                    else{

                                        // show an progress dialog
                                        final ProgressDialog pd = new ProgressDialog(RegisteredUserLogin.this);
                                        pd.setMessage("User not verified, Please wait....");
                                        pd.show();

                                        // send an verification email
                                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                pd.cancel();

                                                // store the user name
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putBoolean("isNotVerified",true);
                                                editor.putString("userName",user.getDisplayName());
                                                editor.putString("userEmail",userEmail);
                                                editor.putString("password",password);
                                                editor.apply();

                                                // call an intent to the user verification activity
                                                startActivity(new Intent(RegisteredUserLogin.this,
                                                        UserVerificationActivity.class));
                                                // finish this activity, so that user can't return
                                                RegisteredUserLogin.this.finish();
                                            }
                                        });
                                    }
                                }
                                else{

                                    // show an error dialog box

                                    userEmailInput.setError("Is this email registered?");
                                    userEmailInput.requestFocus();

                                    Toast.makeText(RegisteredUserLogin.this,
                                            "Something's not right! Are you already registered?", Toast.LENGTH_LONG).show();

                                }
                            }
                        });


            }
        });

    }

    public String getUniqueUserKey(String uid){

        StringBuilder userKey = new StringBuilder();

        for(int i=0; i<uid.length(); i++){
            userKey.append((int)uid.charAt(i));
        }

        return userKey.toString();
    }

    Boolean isValidEmail(String emailId){

        String domain = "@iiitkalyani.ac.in";

        if(emailId.length() == 0){
            return false;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(emailId);
        stringBuilder = stringBuilder.reverse();
        String domainPart = stringBuilder.substring(0,min(domain.length(),emailId.length()));

        stringBuilder = new StringBuilder();
        stringBuilder.append(domainPart);
        stringBuilder = stringBuilder.reverse();

        return domain.equals(stringBuilder.toString());

    }

}
