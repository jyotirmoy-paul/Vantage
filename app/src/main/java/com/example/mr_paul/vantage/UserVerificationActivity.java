package com.example.mr_paul.vantage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserVerificationActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    FirebaseUser firebaseUser;
    String userName, userEmail;
    DatabaseReference databaseReference;

    TextView userEmailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_verification);

        sharedPreferences = getSharedPreferences("userInformation",MODE_PRIVATE);
        userName = sharedPreferences.getString("userName","");
        userEmail = sharedPreferences.getString("userEmail","");

        userEmailView = findViewById(R.id.user_email);
        userEmailView.setText(userEmail);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser(); // this cannot be null, as only
        // user with firebase login credentials can reach this activity

        // re authenticate the user ( as may require if app is opened at a later stage)
        final AuthCredential credential = EmailAuthProvider
                .getCredential(sharedPreferences.getString("userEmail",""),
                        sharedPreferences.getString("password",""));
        firebaseUser.reauthenticate(credential);

        // creating the database references
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("User Data");

        // referencing to the views
        Button verifyEmailButton = findViewById(R.id.verify_email);
        Button cancelRegistrationButton = findViewById(R.id.cancel_registration);

        verifyEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // show an progress dialog
                final ProgressDialog pd = new ProgressDialog(UserVerificationActivity.this);
                pd.setMessage("Waiting for Verification");
                pd.show();

                // reload the firebase user and check if the email got verified or not!
                firebaseUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        pd.cancel(); // cancel the progress dialog

                        if(firebaseUser.isEmailVerified()){

                            Toast.makeText(UserVerificationActivity.this,
                                    "Congrats, your email is verified!", Toast.LENGTH_SHORT).show();

                            String uniqueUserKey = getUniqueUserKey(firebaseUser.getUid());

                            // write to the sharedPreference
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isFirstTimeUser",false);
                            editor.putBoolean("isNotVerified",false);
                            editor.putString("userUID",firebaseUser.getUid());
                            editor.putString("userUniqueKey",uniqueUserKey);
                            // user name and email is already written to the shared preference
                            editor.apply();

                            // upload the user information to the server
                            UserData userData = new UserData(userEmail,userName,
                                    firebaseUser.getUid(),uniqueUserKey);
                            databaseReference.child(firebaseUser.getUid()).setValue(userData);

                            // call an intent to the MainActivity
                            startActivity(new Intent(UserVerificationActivity.this,
                                    MainActivity.class));
                            UserVerificationActivity.this.finish();
                        }
                        else{
                            Toast.makeText(UserVerificationActivity.this,
                                    "Did you check you mail??",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });

        cancelRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete the registered user

                final ProgressDialog progressDialog = new ProgressDialog(UserVerificationActivity.this);
                progressDialog.setMessage("Removing your Credentials");
                progressDialog.show();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isNotVerified",false);
                editor.putBoolean("isFirstTime",true);
                editor.apply();

                startActivity(new Intent(UserVerificationActivity.this,
                        LoginActivity.class));

                // re authenticate the user, as may require sometimes

                firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.cancel();
                        firebaseUser.delete();
                        Toast.makeText(UserVerificationActivity.this,
                                "We are sorry to see you go!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UserVerificationActivity.this,
                                LoginActivity.class));
                        UserVerificationActivity.this.finish();
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

    // a simple class to hold userData that's being uploaded to server
    public class UserData{

        public String name;
        public String email;
        public String uid;
        public String uniqueUserKey;

        public UserData(){
            // an empty constructor is needed for Firebase
        }

        public UserData(String email, String name, String uid,String uniqueUserKey){
            this.email = email;
            this.name = name;
            this.uid = uid;
            this.uniqueUserKey = uniqueUserKey;
        }
    }

}
