package com.rowbotix.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.rowbotix.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Bind UI components
        TextView textViewName = binding.textViewUserName;
        TextView textViewNumber = binding.textViewUserNumber;
        TextView textViewProductName = binding.textViewProductName;
        TextView textViewPurchaseDate = binding.purchaseDate;

        // Observe LiveData and update UI accordingly
        profileViewModel.getUserName().observe(getViewLifecycleOwner(), textViewName::setText);
        profileViewModel.getUserNumber().observe(getViewLifecycleOwner(), textViewNumber::setText);
        profileViewModel.getProductName().observe(getViewLifecycleOwner(), textViewProductName::setText);
        profileViewModel.getPurchaseDate().observe(getViewLifecycleOwner(), textViewPurchaseDate::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
