package com.nicorp.crypto_test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Обработчик нажатия на кнопку "Управление счетами"
        ConstraintLayout billsManagementButton = view.findViewById(R.id.billsManagementButton);
        billsManagementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to BillsManagementFragment using NavigationHelper
                NavigationHelper.navigateToFragment(getActivity(), new BillsManagementFragment());
            }
        });

        return view;
    }
}
