package com.example.andriod.ingredishare.Profile;

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
