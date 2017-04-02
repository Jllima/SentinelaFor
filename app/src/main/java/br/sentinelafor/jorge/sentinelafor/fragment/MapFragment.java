package br.sentinelafor.jorge.sentinelafor.fragment;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import br.sentinelafor.jorge.sentinelafor.R;
import br.sentinelafor.jorge.sentinelafor.map.MapService;
import br.sentinelafor.jorge.sentinelafor.utils.PermissionMap;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements LocationListener{

    MapView mMapView;
    private GoogleMap mMap;
    private MapService mapService;

    private static final String MYTAG = "MYTAG";

    // Request Code to ask the user for permission to view their current location (***).
    // Value 8bit (value <256)
    public static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Mapa");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);


        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                onMyMapReady(googleMap);
            }
        });

        return rootView;
    }

    public void onMyMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
                //askPermissionsAndShowMyLocation();
            }
        });

        // Add a marker in Sydney and move the camera
        LatLng fortaleza = new LatLng(-3.7905171, -38.5885084);
        //mMap.addMarker(new MarkerOptions().position(fortaleza).title("Marker in Fortaleza"));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                fortaleza).zoom(12).build();

        mMap.moveCamera(CameraUpdateFactory.newLatLng(fortaleza));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //my location
        mMap.setMyLocationEnabled(true);
    }

    private void askPermissionsAndShowMyLocation() {

        // With API> = 23, you have to ask the user for permission to view their location.
        if (Build.VERSION.SDK_INT >= 23) {
            int accessCoarsePermission
                    = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
            int accessFinePermission
                    = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);


            if (accessCoarsePermission != PackageManager.PERMISSION_GRANTED
                    || accessFinePermission != PackageManager.PERMISSION_GRANTED) {
                // The Permissions to ask user.
                String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION};
                // Show a dialog asking the user to allow the above permissions.
                ActivityCompat.requestPermissions(getActivity(), permissions,
                        REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);

                return;
            }
        }

        // Show current location on Map.
        mapService.showMyLocation(getActivity(),mMap);
    }

    // When you have the request results.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case REQUEST_ID_ACCESS_COURSE_FINE_LOCATION: {

                // Note: If request is cancelled, the result arrays are empty.
                // Permissions granted (read/write).
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getActivity(), "Permission granted!", Toast.LENGTH_LONG).show();

                    // Show current location on Map.
                    mapService.showMyLocation(getActivity(),mMap);
                }
                // Cancelled or denied.
                else {
                    Toast.makeText(getActivity(), "Permission denied!", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
