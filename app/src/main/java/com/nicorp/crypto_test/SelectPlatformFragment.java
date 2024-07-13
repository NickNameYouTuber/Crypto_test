package com.nicorp.crypto_test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SelectPlatformFragment extends Fragment {

    private RecyclerView recyclerView;
    private PlatformAdapter adapter;
    private List<String> platformList;

    public SelectPlatformFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize platform list
        platformList = new ArrayList<>();
        platformList.add("Qrypt");
        platformList.add("Metamask");
        platformList.add("Trust Wallet");
        // Add more platforms as needed
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_platform, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewPlatforms);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        adapter = new PlatformAdapter(platformList, new PlatformAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String platform) {
                // Handle platform selection and navigate to AddWalletFragment
                Bundle bundle = new Bundle();
                bundle.putString("selectedPlatform", platform);
                AddWalletFragment addWalletFragment = new AddWalletFragment();
                addWalletFragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, addWalletFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        recyclerView.setAdapter(adapter);

        return view;
    }
}
