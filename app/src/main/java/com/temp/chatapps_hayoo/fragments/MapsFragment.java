package com.temp.chatapps_hayoo.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.temp.chatapps_hayoo.R;
import com.temp.chatapps_hayoo.models.User;
import com.temp.chatapps_hayoo.utilities.Constants;

public class MapsFragment extends Fragment {

    private double latitude, longitude;
    private String name;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng location = new LatLng(latitude, longitude);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(5).build();
            googleMap.addMarker(new MarkerOptions().position(location).title("Marker location " + name));
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            User user = (User) bundle.getSerializable(Constants.KEY_USER);
            latitude = user.getLatitude();
            longitude = user.getLongitude();
            name = user.getName();
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}