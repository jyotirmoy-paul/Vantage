package com.jyotirmoy.mr_paul.vantage;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import static java.lang.Math.min;

public class LoginActivity extends AppCompatActivity {

    // declaration of important variables
    FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;
    String emailId;
    String userPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("userInformation",MODE_PRIVATE);

        // this runs only for the first time
        Boolean showAppIntro = sharedPreferences.getBoolean("showAppIntro",true);
        if(showAppIntro){
            // show the app intro ----> until skip/done is not clicked in the intro page
            startActivity(new Intent(LoginActivity.this,AppIntroductionActivity.class));
            LoginActivity.this.finish();
        }

        Boolean isRegisteredButNotVerified = sharedPreferences.getBoolean("isNotVerified",false);
        if(isRegisteredButNotVerified){
            // call intent to the verification activity
            startActivity(new Intent(LoginActivity.this,UserVerificationActivity.class));
            LoginActivity.this.finish();
        }

        Boolean isFirstTime = sharedPreferences.getBoolean("isFirstTimeUser",true);
        if(!isFirstTime){
            // directly call intent to the LogoDisplayActivity and finish this activity
            startActivity(new Intent(LoginActivity.this,LogoDisplayActivity.class));
            LoginActivity.this.finish();
        }


        mAuth = FirebaseAuth.getInstance();

        // referencing to the views
        final EditText userName = findViewById(R.id.user_name);
        final EditText userEmail = findViewById(R.id.user_email);
        final Button verifyEmail = findViewById(R.id.verify_email);
        final EditText userPinInput = findViewById(R.id.user_pin);
        TextView alreadyAuser = findViewById(R.id.already_a_user);

        alreadyAuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisteredUserLogin.class));
                LoginActivity.this.finish();
            }
        });


        // button to send the email link ---> for verification of user
        verifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check if this user is present, and if present delete the user (coz first
                // time it might be a mistake due to poor internet connectivity )


                String domain = "@iiitkalyani.ac.in";


                // check the email provided, to match "@iiitkalyani.ac.in" domain
                emailId = userEmail.getText().toString().trim();

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(emailId);
                stringBuilder = stringBuilder.reverse();
                String domainPart = stringBuilder.substring(0,min(domain.length(),emailId.length()));

                stringBuilder = new StringBuilder();
                stringBuilder.append(domainPart);
                stringBuilder = stringBuilder.reverse();



                final String name_of_user = userName.getText().toString().trim();
                if(name_of_user.length() < 3 || name_of_user.length() > 15){
                    userName.setError("4 - 15 characters only!");
                    userName.requestFocus();
                    return;
                }

                if(emailId.length() == 0){
                    userEmail.setError("Field can't be empty");
                    userEmail.requestFocus();
                    return;
                }

                if(!domain.equals(stringBuilder.toString())){
                    userEmail.setError("use '@iiitkalyani.ac.in' domain");
                    userEmail.requestFocus();
                    return;
                }

                // check for the pin
                userPin = userPinInput.getText().toString().trim();
                if(userPin.length() ==0 ){
                    userPinInput.setError("Can't be empty!");
                    userPinInput.requestFocus();
                    return;
                }


                // email and name of the user is checked for no error
                // now verify the credentials

                // show an progress dialog
                final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                pd.setMessage("Creating Account");
                pd.show();


                mAuth.createUserWithEmailAndPassword(emailId,userPin)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    // to verify the user, send an email
                                    final FirebaseUser firebaseUser = FirebaseAuth
                                            .getInstance().getCurrentUser();

                                    // update the name of the user, before sending verification email
                                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest
                                            .Builder().setDisplayName(name_of_user).build();
                                    firebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            // finally send an verification email
                                            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    // make the progressBar invisible
                                                    pd.cancel();

                                                    // store the user name
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putBoolean("isNotVerified",true);
                                                    editor.putString("userName",name_of_user);
                                                    editor.putString("userEmail",emailId);
                                                    editor.putString("password",userPin);
                                                    editor.apply();

                                                    // call an intent to the user verification activity
                                                    startActivity(new Intent(LoginActivity.this,
                                                            UserVerificationActivity.class));
                                                    // finish this activity, so that user can't return
                                                    LoginActivity.this.finish();
                                                }
                                            });

                                        }
                                    });



                                }
                                else{

                                    pd.cancel();

                                    // show collision error text if email id entered by user is already registered!
                                    try{
                                        throw task.getException();
                                    }
                                    catch(FirebaseAuthUserCollisionException e){
                                        emailCollisionError();
                                    }
                                    catch(FirebaseAuthWeakPasswordException e){
                                        userPinInput.setError("Too weak password!");
                                        userPinInput.requestFocus();
                                    }
                                    catch(Exception e){
                                        Toast.makeText(LoginActivity.this,
                                                "Something unexpected happened! Try again later.", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        });
            }
        });

        // external link for the privacy policy
        TextView privacyPolicyView = findViewById(R.id.privacy_policy);
        privacyPolicyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://vantage-19-10-2018.firebaseapp.com/"));
                startActivity(intent);

            }
        });

    }

    public void emailCollisionError(){

        // open an activity for user to login using their Pin and email Address
        String[] options = {"Proceed with Login","Stay Here"};
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                .setTitle("You are a registered user, try logging in!")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){

                            startActivity(new Intent(LoginActivity.this, RegisteredUserLogin.class));
                            LoginActivity.this.finish();

                        }
                    }
                });
        builder.show();

    }

}
