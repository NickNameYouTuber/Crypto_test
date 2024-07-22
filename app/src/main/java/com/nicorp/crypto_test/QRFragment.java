package com.nicorp.crypto_test;

import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QRFragment extends Fragment {

    private PreviewView previewView;
    private ExecutorService cameraExecutor;

    public QRFragment() {
        // Required empty public constructor
    }

    public static QRFragment newInstance(String param1, String param2) {
        QRFragment fragment = new QRFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("param1");
            String mParam2 = getArguments().getString("param2");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr, container, false);
        previewView = view.findViewById(R.id.previewView);
        cameraExecutor = Executors.newSingleThreadExecutor();
        startCamera();
        return view;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void bindCameraUseCases(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);

        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
    }

    private void analyzeImage(@NonNull ImageProxy image) {
        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer();
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer();

        int width = image.getWidth();
        int height = image.getHeight();
        int[] rgbData = new int[width * height];

        // Ensure the buffers are properly filled
        byte[] yData = new byte[yBuffer.remaining()];
        byte[] uData = new byte[uBuffer.remaining()];
        byte[] vData = new byte[vBuffer.remaining()];

        yBuffer.get(yData);
        uBuffer.get(uData);
        vBuffer.get(vData);

        // Convert YUV to RGB
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int yIndex = y * width + x;
                int uvIndex = (y >> 1) * (width >> 1) + (x >> 1);
                int yValue = yData[yIndex] & 0xFF;
                int uValue = uData[uvIndex] & 0xFF;
                int vValue = vData[uvIndex] & 0xFF;

                // YUV to RGB conversion
                int r = yValue + (int) (1.402 * (vValue - 128));
                int g = yValue - (int) (0.344136 * (uValue - 128) + 0.714136 * (vValue - 128));
                int b = yValue + (int) (1.772 * (uValue - 128));

                // Clamp RGB values to be between 0 and 255
                r = Math.max(0, Math.min(255, r));
                g = Math.max(0, Math.min(255, g));
                b = Math.max(0, Math.min(255, b));

                rgbData[yIndex] = (0xFF000000 | (r << 16) | (g << 8) | b);
            }
        }

        LuminanceSource source = new RGBLuminanceSource(width, height, rgbData);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        MultiFormatReader reader = new MultiFormatReader();

        try {
            Result result = reader.decode(bitmap);
            String qrData = result.getText();
            Log.d("QR", qrData);
            if (qrData.startsWith("\"QRYPT\"")) {
                // Remove "QRYPT:" prefix
                String jsonData = qrData.substring(8);

                Log.d("QR json", jsonData);

                // Parse JSON data
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONObject toObject = jsonObject.getJSONObject("to");

                String name = toObject.getString("name");
                String address = toObject.getString("address");
                int amount = toObject.getInt("amount");
                String currency = toObject.getString("currency");

                // Create a bundle to pass data to the next fragment
                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("address", address);
                bundle.putInt("amount", amount);
                bundle.putString("currency", currency);

                // Navigate to PaymentFragment with data
                NavigationHelper.navigateToFragment(requireActivity(), new PaymentFragment(), bundle);
            }
        } catch (Exception e) {
            // QR code not found or parsing error
            Log.e("QR", "Failed to decode QR code", e);
        } finally {
            image.close();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}
