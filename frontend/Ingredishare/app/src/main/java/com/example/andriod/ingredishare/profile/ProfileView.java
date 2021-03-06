package com.example.andriod.ingredishare.profile;

import java.util.HashMap;

interface ProfileView  {
    void updateUI(HashMap<String,String> data);
    void displaySavedToast();
    void displayInputAllFieldsToast();
    void displayCouldNotFindProfileInfoToast();
    void hideBackButton();
    void displayBackButton();
    void setIngrediListActivityIntent();
    void toastError();
}
