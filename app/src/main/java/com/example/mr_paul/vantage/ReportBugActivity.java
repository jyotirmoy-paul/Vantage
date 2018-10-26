package com.example.mr_paul.vantage;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

public class ReportBugActivity extends AppCompatActivity {

    EditText bugTitle;
    Spinner yesNo;
    EditText bugDescription;
    EditText suggestionView;
    Button sendReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_bug);

        bugTitle = findViewById(R.id.bug_title);
        yesNo = findViewById(R.id.yes_or_no);
        bugDescription = findViewById(R.id.bug_description);
        suggestionView = findViewById(R.id.suggestion);
        sendReport = findViewById(R.id.send_report);

        sendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String bugTitleText = bugTitle.getText().toString().trim();
                if(bugTitleText.isEmpty()){
                    bugTitle.setError("Write something!");
                    bugTitle.requestFocus();
                    return;
                }

                String spinnerValue = yesNo.getSelectedItem().toString();

                String bugDescriptionText = bugDescription.getText().toString().trim();
                if(bugDescriptionText.length() < 10 || bugDescriptionText.length() > 1000){
                    bugDescription.setError("10 - 1000 chars");
                    bugDescription.requestFocus();
                    return;
                }


                String userSuggestion = suggestionView.getText().toString().trim();
                if(userSuggestion.length() > 2000){
                    suggestionView.setError("No more than 2000 chars");
                    suggestionView.requestFocus();
                    return;
                }

                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("HH:mm dd:MM:yyyy");

                BugReportClass data = new BugReportClass(getSharedPreferences("userInformation",MODE_PRIVATE)
                        .getString("userName","test uid"),bugTitleText,spinnerValue,
                        bugDescriptionText,userSuggestion,dateFormat.format(date));

                FirebaseDatabase.getInstance().getReference()
                        .child("User Feedback Messages")
                        .child(getSharedPreferences("userInformation",MODE_PRIVATE)
                                .getString("userUID","test uid")).push().setValue(data);

                Toast.makeText(ReportBugActivity.this,
                        "Thanks for your feedback!", Toast.LENGTH_SHORT).show();
                ReportBugActivity.this.finish();

            }
        });

    }

    public class BugReportClass{

        public String bugTitle;
        public String didTheAppCrash;
        public String bugDescription;
        public String userSuggestion;
        public String date;
        public String name;

        public BugReportClass(){
            // empty constructor for firebase
        }

        public BugReportClass(String name, String bugTitle,String didTheAppCrash,String bugDescription,String userSuggestion,String date){
            this.bugDescription = bugDescription;
            this.didTheAppCrash = didTheAppCrash;
            this.bugTitle = bugTitle;
            this.userSuggestion = userSuggestion;
            this.date = date;
            this.name = name;
        }

    }

}
