package com.service.atozhomeservice.view.ui.searchlocation;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.service.atozhomeservice.R;
import com.service.atozhomeservice.view.LocationTracker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
/*********************************************************************************************************
 * Author         : Gunnampalli Parameswara Reddy
 * Class Name     : SearchLocation
 * Description    : Fragment that allows users to search for a geographic location by name or by tapping on
 *                  a map. Displays selected coordinates and resolved address, and stores the location
 *                  using LocationTracker for later retrieval.
 * Called By      : Navigation component during location selection workflow.
 * Instance Vars  : EditText searchEditText – Input field for address search.
 *                  GoogleMap googleMap     – Map interface for user interaction.
 *                  LatLng selectedLatLng   – Coordinates of the user-selected location.
 *                  String selectedAddress  – Human-readable address for the selected coordinates.
 *                  int REQUEST_LOCATION_SETTINGS – Request code to handle settings resolution.
 *********************************************************************************************************/
public class SearchLocation extends Fragment {
    private EditText searchEditText;
    private GoogleMap googleMap;
    private LatLng selectedLatLng;
    private String selectedAddress = "";

    private static final int REQUEST_LOCATION_SETTINGS = 101;

    public SearchLocation() {}

    /*********************************************************************************************************
     * Method Name     : onCreateView
     * Description     : Inflates the layout, initializes map fragment and sets up listeners for search
     *                   and map tap interactions.
     * Parameters      : LayoutInflater inflater – Inflater to inflate layout XML
     *                   ViewGroup container     – Parent container for the fragment UI
     *                   Bundle savedInstanceState – Previous saved state if any
     * Return          : View – The root view of the fragment layout
     *********************************************************************************************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_location, container, false);
        searchEditText = view.findViewById(R.id.search_edit_text);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map_fragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(map -> {
                googleMap = map;
                setupSearchListener();
                setupMapTapListener();
            });
        }

        return view;
    }

    /*********************************************************************************************************
     * Method Name     : setupSearchListener
     * Description     : Sets up listener on the EditText to initiate geocoding search based on entered location
     *                   name when the user submits the input.
     * Parameters      : None
     * Return          : void
     *********************************************************************************************************/
    private void setupSearchListener() {
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                checkLocationSettingsAndSearch(query);
            }
            return true;
        });
    }

    /*********************************************************************************************************
     * Method Name     : setupMapTapListener
     * Description     : Allows user to tap on the map to choose a location. Marker is updated and reverse geocoding
     *                   is performed to resolve address which is then displayed and stored.
     * Parameters      : None
     * Return          : void
     *********************************************************************************************************/
    private void setupMapTapListener() {
        googleMap.setOnMapClickListener(latLng -> {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (!addresses.isEmpty()) {
                    selectedAddress = addresses.get(0).getAddressLine(0);
                    selectedLatLng = latLng;

                    String fullLocation = selectedAddress + " (" +
                            latLng.latitude + ", " + latLng.longitude + ")";
                    searchEditText.setText(selectedAddress);
                    LocationTracker.setFetchedAddress(fullLocation);
                    Log.d("SearchLocation", "Picked via map: " + fullLocation);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Failed to get address", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*********************************************************************************************************
     * Method Name     : checkLocationSettingsAndSearch
     * Description     : Ensures that device location settings are enabled before proceeding with geocoding
     *                   the searched query. Attempts to resolve missing permissions interactively.
     * Parameters      : String query – Address query entered by the user
     * Return          : void
     *********************************************************************************************************/
    private void checkLocationSettingsAndSearch(String query) {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);

        SettingsClient client = LocationServices.getSettingsClient(requireActivity());
        client.checkLocationSettings(builder.build())
                .addOnSuccessListener(response -> searchAddress(query))
                .addOnFailureListener(e -> {
                    if (e instanceof ResolvableApiException) {
                        try {
                            ((ResolvableApiException) e).startResolutionForResult(requireActivity(), REQUEST_LOCATION_SETTINGS);
                        } catch (IntentSender.SendIntentException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Location services are required.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /*********************************************************************************************************
     * Method Name     : searchAddress
     * Description     : Uses Geocoder to resolve an address from the query string. If found, a marker is placed
     *                   and the map is updated. Also updates LocationTracker with the address.
     * Parameters      : String query – The address string to be geocoded
     * Return          : void
     *********************************************************************************************************/
    private void searchAddress(String query) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(query, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                selectedLatLng = new LatLng(address.getLatitude(), address.getLongitude());
                selectedAddress = address.getAddressLine(0);

                String fullLocation = selectedAddress + " (" +
                        selectedLatLng.latitude + ", " +
                        selectedLatLng.longitude + ")";

                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(selectedLatLng).title("Selected Location"));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15));

                LocationTracker.setFetchedAddress(fullLocation);
                Log.d("SearchLocation", "Picked via search: " + fullLocation);
            } else {
                Toast.makeText(requireContext(), "No address found.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Geocoding failed.", Toast.LENGTH_SHORT).show();
        }
    }

    /*********************************************************************************************************
     * Method Name     : onActivityResult
     * Description     : Callback invoked when the user returns from the location settings prompt. If accepted,
     *                   retries the address search with the previously entered query.
     * Parameters      : int requestCode – Code identifying the request
     *                   int resultCode – Result status
     *                   Intent data – Returned intent data (not used here)
     * Return          : void
     *********************************************************************************************************/
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION_SETTINGS && resultCode == Activity.RESULT_OK) {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                searchAddress(query);
            }
        }
    }
}
