package com.nicorp.crypto_test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Обработчик нажатия на кнопку "Управление счетами"
        ConstraintLayout billsManagementButton = view.findViewById(R.id.billsManagementButton);
        billsManagementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Replace the current fragment with BillsManagementFragment with animation
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.fragmentContainerView, new BillsManagementFragment());
                transaction.addToBackStack(null); // Optionally add to back stack
                transaction.commit();
            }
        });

        return view;
    }
}
