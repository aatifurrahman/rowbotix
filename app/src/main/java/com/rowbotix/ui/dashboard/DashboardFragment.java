package com.rowbotix.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.rowbotix.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        dashboardViewModel.getWheel().observe(getViewLifecycleOwner(), wheel -> {
            binding.text1.setText(wheel);
        });

        dashboardViewModel.getFertiliser().observe(getViewLifecycleOwner(), fertiliser -> {
            binding.text2.setText(fertiliser);
        });

        dashboardViewModel.getDistance().observe(getViewLifecycleOwner(), distance -> {
            binding.text3.setText(distance);
        });

        // Observe ViewModel's image resource
        dashboardViewModel.getDeviceImageResource().observe(getViewLifecycleOwner(), imageResource -> {
            // Update ImageView with ViewModel's image resource
            binding.deviceImage.setImageResource(imageResource);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
