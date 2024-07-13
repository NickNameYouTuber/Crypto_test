package com.nicorp.crypto_test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SelectPlatformFragment extends Fragment {

    private RecyclerView recyclerView;
    private PlatformAdapter adapter;
    private List<Platform> platformList;

    public SelectPlatformFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize platform list
        platformList = new ArrayList<>();
        platformList.add(new Platform("Qrypt", R.drawable.qrypt));
        platformList.add(new Platform("Metamask", R.drawable.metamask));
        platformList.add(new Platform("Trust Wallet", R.drawable.trust_wallet));
        // Add more platforms as needed
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_platform, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewPlatforms);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PlatformAdapter(platformList, new PlatformAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Platform platform) {
                // Handle platform selection and navigate to AddWalletFragment
                Bundle bundle = new Bundle();
                bundle.putString("selectedPlatform", platform.getName());
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
