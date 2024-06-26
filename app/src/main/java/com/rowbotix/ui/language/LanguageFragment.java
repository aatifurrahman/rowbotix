package com.rowbotix.ui.language;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.rowbotix.databinding.FragmentLanguageBinding;

public class LanguageFragment extends Fragment {

    private FragmentLanguageBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LanguageViewModel languageViewModel =
                new ViewModelProvider(this).get(LanguageViewModel.class);

        binding = FragmentLanguageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textLanguage;
        languageViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}