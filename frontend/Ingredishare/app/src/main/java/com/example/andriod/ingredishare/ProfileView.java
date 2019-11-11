package com.example.andriod.ingredishare;

import java.util.HashMap;

interface ProfileView  {
    void updateUI(HashMap<String,String> data);
    void displaySavedToast();
    void displayInputAllFieldsToast();
    void hideBackButton();
    void displayBackButton();
    void setIngrediListActivityIntent();
}
