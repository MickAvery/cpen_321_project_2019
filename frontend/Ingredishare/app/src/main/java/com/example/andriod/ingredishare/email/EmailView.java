package com.example.andriod.ingredishare.email;

import android.content.Intent;

interface EmailView {
    void toastCouldNotSend();
    void toastSuccess();
    void startNewActivity(Intent intent);
}
