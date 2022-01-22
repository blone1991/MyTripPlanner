package com.example.mytripplanner;

import static com.example.mytripplanner.MainActivity.REQUEST_PERMISSIONS;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;

public class MapsFragment extends Fragment {
    MainActivityViewModel viewModel;
    private GoogleMap map;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng sydney = new LatLng(-34, 151);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            map = googleMap;            ;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(getActivity(), new MainActivityViewModelProviderFactory()).get(MainActivityViewModel.class);
        viewModel.init();
        View v = inflater.inflate(R.layout.fragment_maps, container, false);
        checkPermission();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int result: grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireActivity(), "앱 권한을 확인하십시오", Toast.LENGTH_SHORT).show();
                Handler handler = new Handler();
                handler.postDelayed(() -> System.exit(0), 2000);

                return;
            }
        }
    }

    void checkPermission () {
        ArrayList<String> requiredPermmsions = new ArrayList<>();
        requiredPermmsions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        requiredPermmsions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        for (String p : requiredPermmsions) {
            if (getContext().checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(requiredPermmsions.toArray(new String[0]), REQUEST_PERMISSIONS);
                break;
            }
        }
    }
}