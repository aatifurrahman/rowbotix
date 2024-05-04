package com.rowbotix.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<String> userName;
    private final MutableLiveData<String> userNumber;
    private final MutableLiveData<String> productName;
    private final MutableLiveData<String> purchaseDate;

    public ProfileViewModel() {
        userName = new MutableLiveData<>();
        userNumber = new MutableLiveData<>();
        productName = new MutableLiveData<>();
        purchaseDate = new MutableLiveData<>();

        // Set default values (can be updated later)
        userName.setValue("John Doe");
        userNumber.setValue("99XXXXXX");
        productName.setValue("Product Name");
        purchaseDate.setValue("Jan 1, 2024");
    }

    // LiveData getters
    public LiveData<String> getUserName() {
        return userName;
    }

    public LiveData<String> getUserNumber() {
        return userNumber;
    }

    public LiveData<String> getProductName() {
        return productName;
    }

    public LiveData<String> getPurchaseDate() {
        return purchaseDate;
    }

    // Method to update user information
    public void updateUserInfo(String name, String number, String product, String date) {
        userName.setValue(name);
        userNumber.setValue(number);
        productName.setValue(product);
        purchaseDate.setValue(date);
    }
}
