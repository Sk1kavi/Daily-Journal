package com.example.dailyjournal;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        // Set version info (example)
        @SuppressLint("MissingInflatedId") TextView versionText = view.findViewById(R.id.tvVersion);
        try {
            String version = getContext().getPackageManager()
                    .getPackageInfo(getContext().getPackageName(), 0)
                    .versionName;
            versionText.setText("Version: " + version);
        } catch (Exception e) {
            versionText.setText("Version: 1.0");
        }

        return view;
    }
}
