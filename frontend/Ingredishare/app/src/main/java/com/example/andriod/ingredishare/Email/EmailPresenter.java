package com.example.andriod.ingredishare.Email;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.andriod.ingredishare.Main.MainActivity;
import com.example.andriod.ingredishare.MyApplication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailPresenter {

    private EmailView view;
    private FirebaseUser mUser;
    private Context mContext;

    public EmailPresenter(EmailView view) {
        this.view = view;
        mContext = MyApplication.getContext();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void sendEmail(String email, String subject, String message){

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",email, null));

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            mContext.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            Log.i("Finished sending email...", "");
            view.toastSuccess();
        } catch (android.content.ActivityNotFoundException ex) {
            view.toastCouldNotSend();
        }

    }
}
