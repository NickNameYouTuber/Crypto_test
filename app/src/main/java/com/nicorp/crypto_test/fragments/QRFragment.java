package com.nicorp.crypto_test.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;
import com.nicorp.crypto_test.R;
import com.nicorp.crypto_test.helpers.NavigationHelper;

import org.json.JSONObject;
import java.util.List;

public class QRFragment extends Fragment {
    private static final int CAMERA_PERMISSION_REQUEST = 1;
    private static BarcodeView barcodeView;
    private static FragmentActivity activity;
    private static final String TAG = "QRFragment";
    private Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr, container, false);
        barcodeView = view.findViewById(R.id.qrCodeContainer);
        activity = getActivity();

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        } else {
            // Delay the start of scanning
            handler.postDelayed(this::startScanning, 1000); // 500 ms delay
        }
        return view;
    }

    private void startScanning() {
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                barcodeView.pause();
                handleQrResult(result.getText());
            }

            @Override
            public void possibleResultPoints(List<com.google.zxing.ResultPoint> resultPoints) {
                // Not used in this example
            }
        });
    }

    private void handleQrResult(String qrData) {
        try {
            if (qrData.startsWith("\"QRYPT\"")) {
                String jsonData = qrData.substring(8);
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONObject toObject = jsonObject.getJSONObject("to");

                String name = toObject.getString("name");
                String address = toObject.getString("address");
                int amount = toObject.getInt("amount");
                String currency = toObject.getString("currency");

                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("address", address);
                bundle.putInt("amount", amount);
                bundle.putString("currency", currency);

                NavigationHelper.navigateToFragment(getActivity(), new PaymentFragment(), bundle);
            } else {
                showError("Invalid QR code format");
            }
        } catch (Exception e) {
            showError("Error processing QR code");
            Log.e(TAG, "Error processing QR code", e);
        }
    }

    private void showError(String message) {
        requireActivity().runOnUiThread(() -> {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            barcodeView.resume();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Delay the start of scanning
                handler.postDelayed(this::startScanning, 500); // 500 ms delay
            } else {
                showError("Camera permission is required to scan QR codes");
            }
        }
    }

    // Method to restart scanning
    public static void restartScanning() {
        Log.d(TAG, "Restarting scanning");
        if (barcodeView != null) {
            barcodeView.resume();
            barcodeView.decodeContinuous(new BarcodeCallback() {
                @Override
                public void barcodeResult(BarcodeResult result) {
                    barcodeView.pause();
                    handleQrResultStatic(result.getText());
                }

                @Override
                public void possibleResultPoints(List<com.google.zxing.ResultPoint> resultPoints) {
                    // Not used in this example
                }
            });
        }
    }

    public static void stopScanning() {
        if (barcodeView != null) {
            barcodeView.pause();
        }
    }

    public static void handleQrResultStatic(String qrData) {
        try {
            if (qrData.startsWith("\"QRYPT\"")) {
                String jsonData = qrData.substring(8);
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONObject toObject = jsonObject.getJSONObject("to");

                String name = toObject.getString("name");
                String address = toObject.getString("address");
                int amount = toObject.getInt("amount");
                String currency = toObject.getString("currency");

                Log.d(TAG, "Received data: " + name + ", " + address + ", " + amount + ", " + currency);

                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("address", address);
                bundle.putInt("amount", amount);
                bundle.putString("currency", currency);

                if (activity != null) {
                    PaymentFragment paymentFragment = new PaymentFragment();
                    paymentFragment.setArguments(bundle);
                    NavigationHelper.navigateToFragment(activity, paymentFragment);
                } else {
                    Log.e(TAG, "Activity is null, cannot navigate to PaymentFragment");
                }
            } else {
                Log.e(TAG, "Invalid QR code format");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error processing QR code", e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            barcodeView.resume();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        barcodeView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            barcodeView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeView.pause();
    }
}