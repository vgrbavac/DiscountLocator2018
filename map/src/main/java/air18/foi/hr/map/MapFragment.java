package air18.foi.hr.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.air.ws.core.NavigationItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.util.List;

import air18.foi.hr.database.entities.Discount;
import air18.foi.hr.database.entities.Store;

public class MapFragment extends Fragment implements NavigationItem, OnMapReadyCallback {
    private List<Store> stores;
    private List<Discount> discounts;
    private GoogleMap mMap;
    private com.google.android.gms.maps.SupportMapFragment mapFragment;

    private boolean dataReadyFlag;
    private boolean moduleReadyFlag;
    private int MY_LOCATION_REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        Analytics.trackEvent("Ovo je main!");
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public String getName(Context context) {
        return context.getString(R.string.menu_map);
    }

    @Override
    public Drawable getIcon(Context context) {
        return context.getDrawable(android.R.drawable.ic_menu_mylocation);
    }

    @Override
    public void setData(List<Store> stores, List<Discount> discounts) {

        this.stores = stores;
        this.discounts = discounts;

        dataReadyFlag = true;
        tryToDisplayData();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showUserLocation();

        moduleReadyFlag = true;
        tryToDisplayData();
    }

    private void showUserLocation()
    {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
        }
        else
        {
            mMap.setMyLocationEnabled(true);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
            else {
                // Permission was denied. Display an error message.
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void tryToDisplayData() {
        if (dataReadyFlag && moduleReadyFlag)
            displayData();
    }

    private void displayData()
    {
        if(stores != null){
            boolean cameraReady = false;
            for(Store store : stores)
            {
                // Add a markers
                LatLng position = new LatLng(store.getLatitude() / 1000000d, store.getLongitude() / 1000000d);
                mMap.addMarker(new MarkerOptions().position(position).title(store.getName()));

                // Move the camera
                if (!cameraReady)
                {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
                    cameraReady = true;
                }
            }
        }
    }
}
