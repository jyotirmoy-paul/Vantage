# Vantage 
An android app, via which students of our institute (Indian Institute of Information Technology, Kalyani) can make a sale, rent or buy used products of other students. This app is developed keeping our institute in mind, thus one having institute alloted email id's can only sign up and have a vantage!
## Getting Started
The following instuctions will get you a copy of the project up and running on your local machine for development and testing purpose.
### Prerequisites
What things you need to install the software
```
1. Latest version of android studio should be set-up and running on your system.
2. Add Firebase to your project. (https://firebase.google.com/docs/android/setup)
3. And finally, an android device (or emulator) running on Jelly Bean or higher.
```
### Setting up Firebase - You have to add your own 'google-services.json' file
By now, you would have already downloaded the "google-services.json" file and connected your app to Firebase Server. Finally, to set the server side Authentication, Realtime Database and Storage provided by Google Firebase, follow the steps below:
1. Check for the following dependencies in app-level gradle file
     ```
     com.google.firebase:firebase-core:16.0.4 : Firebase Core
    com.google.firebase:firebase-database:16.0.3 : Realtime Database
    com.google.firebase:firebase-storage:16.0.3 : Storage
    com.google.firebase:firebase-auth:16.0.4 : Authentication
    ```
2. Add the following database rules, in Firebase console:
    ```
    {
    "rules": {
        ".read": "auth !== null",
        ".write": "auth !== null"
        }
    }
    ```
3. Add the following storage rules, in Firebase console:
    ```
    service firebase.storage {
        match /b/{bucket}/o {
            match /{allPaths=**} {
                allow read, write: if true;
                 }
            }
        }
    ```
4. For Authentication, "Email/Password"  and "Email link (passwordless sign-in)", in Firebase should be enabled.
### Installing the App
Import the app to Android Studio, build the project and finally deploy it in a device (or emulator).
## Features
* You can sell/buy varies ranges of products within our IIIT Kalayani Campus, all you need is an official '..@iiitkalyani.ac.in' email to register.
* Posting an Ad is pretty simple and straight forward.
* News Feed area in the app shows recently posted Ads by any fellow college mates.
* One to one interaction between seller and buyer or owner and renter through the built in chat fucntionality.
* Non-editable Ads, that would ensure the buyer's safety.
* And the most important part is, the app has an one time-based registration signup.
##  Build With
* [Google Firebase](https://firebase.google.com/)
* [AndroidStaggeredGrid Library](https://github.com/etsy/AndroidStaggeredGrid)
* [Glide](https://github.com/bumptech/glide)
## Authors
* **Jyotirmoy Paul** - Initial work - [jyotirmoy-paul](https://github.com/jyotirmoy-paul)
