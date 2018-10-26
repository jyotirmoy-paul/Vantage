package com.example.mr_paul.vantage;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.Strings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import static android.content.Context.MODE_PRIVATE;

public class Profile extends Fragment {

    // widgets variable
    TextView userNameTextView;
    TextView userEmailTextView;
    TextView userDisplayChar;
    TextView noOfDealsView;
    TextView noOfAdsPosted;
    LinearLayout myPostedAd;
    LinearLayout showAllChats;
    LinearLayout giveFeedback;
    LinearLayout deleteMyAccount;
    LinearLayout audioSettings;
    LinearLayout reportBug;

    DatabaseReference adDataRef;
    DatabaseReference chattingInfoRef;
    DatabaseReference userDataRef;
    String UID;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // reference to the views
        userNameTextView = view.findViewById(R.id.user_name);
        userEmailTextView = view.findViewById(R.id.user_email);
        userDisplayChar = view.findViewById(R.id.user_display_char);
        noOfDealsView = view.findViewById(R.id.no_of_deals);
        noOfAdsPosted = view.findViewById(R.id.no_of_ads_posted);
        myPostedAd = view.findViewById(R.id.my_posted_ad);
        showAllChats = view.findViewById(R.id.show_all_chats);
        giveFeedback = view.findViewById(R.id.give_feedback);
        deleteMyAccount = view.findViewById(R.id.delete_my_account);
        audioSettings = view.findViewById(R.id.settings);
        reportBug = view.findViewById(R.id.report_bug);


        reportBug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),ReportBugActivity.class));
            }
        });

        audioSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // toggle the audio setting
                String[] options = {"Mute me!","I love the notification sound"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle("Change audio settings")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){
                                    if(getContext().getSharedPreferences("settings",MODE_PRIVATE)
                                            .getBoolean("playsound",true)){
                                        // put the audio settings to false if not false already!
                                        getContext().getSharedPreferences("settings",MODE_PRIVATE)
                                                .edit().putBoolean("playsound",false).apply();
                                        Toast.makeText(getContext(),
                                                "We turned off the tones!", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(getContext(),
                                                "It's already muted!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                else {
                                    if (!getContext().getSharedPreferences("settings", MODE_PRIVATE)
                                            .getBoolean("playsound", true)) {
                                        // turn on the notification sound if not already
                                        getContext().getSharedPreferences("settings", MODE_PRIVATE)
                                                .edit().putBoolean("playsound", true).apply();
                                        Toast.makeText(getContext(),
                                                "We turned on the tones!", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(getContext(),
                                                "You are already enjoying the tones!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });

                builder.show();
            }
        });

        SharedPreferences sharedPreferences = getActivity()
                .getSharedPreferences("userInformation",MODE_PRIVATE);

        // writing the user name and email to the views
        userDisplayChar.setText(sharedPreferences.getString("userName","-")
                .toUpperCase().substring(0,1)); // the first char of the name
        userNameTextView.setText(sharedPreferences.getString("userName",""));
        userEmailTextView.setText(sharedPreferences.getString("userEmail",""));

        noOfDealsView.setText(sharedPreferences.getInt("numberOfConfirmedDeals",0) + "");
        noOfAdsPosted.setText(sharedPreferences.getInt("numberOfAdsPosted",0) + "");


        // call an intent to MyPostedAds
        myPostedAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),MyPostedAdActivity.class));
            }
        });

        // call an intent to ShowAllChats
        showAllChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),ShowAllChatsActivity.class));
            }
        });

        // call an intent to DeleteMyAccount
        deleteMyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] options = {"Yes","No"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle("Confirm Deletion of your account?")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){
                                    // delete all user data from server and reset the app
                                    deleteAccount();
                                }
                                else{
                                    Toast.makeText(getContext(),
                                            "That was a good decision!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                builder.show();
            }
        });

        // call an intent to email
        giveFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // show toast showing appreciation
                Toast.makeText(getContext(),
                        "Thanks for your valuable time!", Toast.LENGTH_SHORT).show();

                // call an intent to Gmail app
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "jyotirmoy@iiitkalyani.ac.in"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Vantage Feedback");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Chooser Title"));
            }
        });
    }

    public void deleteAccount(){

        // show a progress dialog
        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Deleting Your Account");
        pd.show();

        // this method deletes all user related data from server and app
        UID = getContext().getSharedPreferences("userInformation",MODE_PRIVATE)
                .getString("userUID","");

        // deleting the user related information
        userDataRef = FirebaseDatabase.getInstance().getReference().child("User Data").child(UID);
        userDataRef.removeValue();

        // deleting the user chatting information
        chattingInfoRef = FirebaseDatabase.getInstance().getReference().child("Chatting Information")
                .child(UID);
        chattingInfoRef.removeValue();

        // deleting the advertisement data
        adDataRef = FirebaseDatabase.getInstance().getReference().child("Advertisement Data");
        adDataRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                ServerData serverData = dataSnapshot.getValue(ServerData.class);
                if(serverData.getUserUID().equals(UID)){

                    // delete from the firebase storage
                    FirebaseStorage.getInstance().getReferenceFromUrl(serverData.getPhoto_01()).delete();
                    if(serverData.getPhoto_02().length() != 0){
                        FirebaseStorage.getInstance().getReferenceFromUrl(serverData.getPhoto_02()).delete();
                    }
                    if(serverData.getPhoto_03().length() != 0){
                        FirebaseStorage.getInstance().getReferenceFromUrl(serverData.getPhoto_03()).delete();
                    }

                    // finally deleting from the real-time database
                    adDataRef.child(serverData.getAdKey()).removeValue();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        // delete all data from shared preference
        final SharedPreferences sharedPreferences = getContext().getSharedPreferences("userInformation",
                MODE_PRIVATE);

        // store the deleted user in database
        FirebaseDatabase.getInstance()
                .getReference().child("Deleted Accounts").push().setValue(
                new DeleteData(UID,sharedPreferences.getString("userName","")
                        ,sharedPreferences.getString("userEmail",""))
        );

        // remove the user from firebase user

        AuthCredential credential = EmailAuthProvider
                .getCredential(sharedPreferences.getString("userEmail",""),
                        sharedPreferences.getString("password",""));
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(getContext(),
                                "Account deleted!", Toast.LENGTH_SHORT).show();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        SharedPreferences sharedPreference = getContext().getSharedPreferences("settings",
                                MODE_PRIVATE);
                        editor = sharedPreference.edit();
                        editor.clear();
                        editor.apply();


                        // and finally call an intent to the login activity and finish this activity
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    }
                });
            }
        });
    }

    public class DeleteData{
        public String uid;
        public String name;
        public String email;

        public DeleteData(){
            // default constructor
        }

        public DeleteData(String uid, String name, String email){
            this.uid = uid;
            this.name = name;
            this.email = email;
        }
    }


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Profile() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
