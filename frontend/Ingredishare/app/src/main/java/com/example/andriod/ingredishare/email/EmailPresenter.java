package com.example.andriod.ingredishare.email;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.andriod.ingredishare.MyApplication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailPresenter {

    private EmailView view;
    private Context mContext;

    public EmailPresenter(EmailView view) {
        this.view = view;
        mContext = MyApplication.getContext();
    }

    public void sendEmail(String email, String subject, String message){

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);

        emailIntent.setType("*/*");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            view.startNewActivity(emailIntent);
            Log.i("Finished sending email...", "");
            view.toastSuccess();
        } catch (android.content.ActivityNotFoundException ex) {
            view.toastCouldNotSend();
        }

    }
}
