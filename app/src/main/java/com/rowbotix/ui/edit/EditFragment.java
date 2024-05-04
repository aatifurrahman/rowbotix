package com.rowbotix.ui.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.rowbotix.MainActivity;
import com.rowbotix.R;
import com.rowbotix.databinding.FragmentEditBinding;

public class EditFragment extends Fragment {

    private FragmentEditBinding binding;
    private EditViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(EditViewModel.class);

        // Bind UI components
        TextInputLayout textInputWheelNo = binding.textInputWheelNo;
        TextInputLayout textInputDistance = binding.textInputDistance;
        TextInputLayout textInputFertilizer = binding.textInputFertilizer;
        TextInputLayout textInputRows = binding.textInputRows;
        TextInputLayout textInputRowDistance = binding.textInputRowDistance;

        Button saveButton = binding.save;
        Button sendButton = binding.send;

        // Set click listeners
        saveButton.setOnClickListener(v -> onSaveClicked());
        sendButton.setOnClickListener(v -> onSendClicked());
    }

    private void onSaveClicked() {
        if (isFormValid()) {
            // Get data from UI components
            String wheelNo = binding.textInputWheelNo.getEditText().getText().toString();
            String distance = binding.textInputDistance.getEditText().getText().toString();
            String fertilizer = binding.textInputFertilizer.getEditText().getText().toString();
            String rows = binding.textInputRows.getEditText().getText().toString();
            String rowDistance = binding.textInputRowDistance.getEditText().getText().toString();

            // Save data using ViewModel
            viewModel.saveData(wheelNo, distance, fertilizer, rows, rowDistance);
        } else {
            // Show error toast if form is not valid
            Toast.makeText(requireContext(), R.string.error_fill_all_fields, Toast.LENGTH_SHORT).show();

        }
    }

    private void onSendClicked() {
        // Get data from ViewModel and send it
        String dataToSend = viewModel.getFormattedData();
        // Implement sending data logic
       // MainActivity.bleDeviceConnector.writeData(dataToSend);
        Toast.makeText(requireContext(),dataToSend, Toast.LENGTH_SHORT).show();
    }

    private boolean isFormValid() {
        return isFieldValid(binding.textInputWheelNo)
                && isFieldValid(binding.textInputDistance)
                && isFieldValid(binding.textInputFertilizer)
                && isFieldValid(binding.textInputRows)
                && isFieldValid(binding.textInputRowDistance);
    }

    private boolean isFieldValid(TextInputLayout textInputLayout) {
        String text = textInputLayout.getEditText().getText().toString().trim();
        return !text.isEmpty();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
