package com.service.atozhomeservice.view;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import android.Manifest;

import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.service.atozhomeservice.MainActivity;
import com.service.atozhomeservice.R;
import com.service.atozhomeservice.databinding.ActivityLocationTrackerBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationTracker extends AppCompatActivity {

    //Instance Variables
    private FusedLocationProviderClient fusedLocationClient;
    private static String fetchedAddress = "";
    private ActivityLocationTrackerBinding binding;

    private static String areaLine,regionLine ;



    /*************************************************************************************************************
     * Author         : Gunnampalli Parameswara Reddy
     * Function Name  : onCreate
     * Description    : Initializes the Activity UI and sets up high-accuracy location tracking using the Fused
     *                  Location Provider. Applies edge-to-edge display with proper insets, starts an animation,
     *                  configures location request intervals, validates device settings, and prompts the user to
     *                  enable location services if required. If settings are adequate, it triggers location fetch.
     * Called By      : Android System on Activity Launch
     * Parameters     : Bundle savedInstanceState – Used to restore the previous state of the Activity
     * Return         : None
     *************************************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLocationTrackerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        binding.locationAnimation.playAnimation();

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(5000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true); // 👈 shows dialog even if location is off

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(locationSettingsResponse -> {
            // All location settings are satisfied. Proceed with location fetch
            fetchLocation();
        });

        task.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    // Show the dialog to turn on location
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(this, 101);
                } catch (IntentSender.SendIntentException sendEx) {
                    sendEx.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Location settings are inadequate.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /*************************************************************************************************************
     * Author         : Gunnampalli Parameswara Reddy
     * Function Name  : fetchLocation
     * Description    : Checks for location permission and retrieves the user's last known location. If not available,
     *                  it requests a single high-accuracy location update. Upon success, it invokes a custom method
     *                  to handle the retrieved location. Provides fallback handling and animation cancellation if
     *                  location retrieval fails.
     * Called By      : onCreate()
     * Parameters     : None
     * Return         : None
     *************************************************************************************************************/

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                handleLocation(location);
            } else {
                // 🔁 Fallback: request a fresh location
                LocationRequest locationRequest = LocationRequest.create()
                        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                        .setInterval(0)
                        .setFastestInterval(0)
                        .setNumUpdates(1); // Only one update needed

                fusedLocationClient.requestLocationUpdates(locationRequest, new com.google.android.gms.location.LocationCallback() {
                    @Override
                    public void onLocationResult(com.google.android.gms.location.LocationResult locationResult) {
                        if (locationResult != null && !locationResult.getLocations().isEmpty()) {
                            handleLocation(locationResult.getLastLocation());
                        } else {
                            binding.addressText.setText("Still unable to fetch location");
                            binding.locationAnimation.cancelAnimation();
                        }
                    }
                }, getMainLooper());
            }
        });
    }

    /*************************************************************************************************************
     * Author         : Gunnampalli Parameswara Reddy
     * Function Name  : handleLocation
     * Description    : Converts the user's latitude and longitude into a human-readable address using Geocoder.
     *                  Updates the UI with the fetched address, pauses the animation, and navigates to the home
     *                  screen upon success. Handles errors gracefully if geocoding fails due to network or service
     *                  issues.
     * Called By      : fetchLocation()
     * Parameters     : android.location.Location location – The GPS-based geographic coordinates of the user
     * Return         : None
     *************************************************************************************************************/
    /*private void handleLocation(android.location.Location location) {
        binding.locationAnimation.pauseAnimation();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!addresses.isEmpty()) {
                fetchedAddress = addresses.get(0).getAddressLine(0);
                binding.addressText.setText(fetchedAddress);
                navigateToHome();
            }
        } catch (IOException e) {
            binding.addressText.setText("Error fetching address");
            e.printStackTrace();
        }
    }*/

    private void handleLocation(android.location.Location location) {
        binding.locationAnimation.pauseAnimation();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);

                // First line: Building + Area/Street
                areaLine = "";

                if (address.getFeatureName() != null && !address.getFeatureName().equals(address.getThoroughfare())) {
                    areaLine += address.getFeatureName() + " ";
                }
                if (address.getSubThoroughfare() != null) {
                    areaLine += address.getSubThoroughfare() + " ";
                }
                if (address.getThoroughfare() != null) {
                    areaLine += address.getThoroughfare();
                }
                if (address.getSubLocality() != null) {
                    areaLine += ", " + address.getSubLocality();
                }


                // Second line: City, State, Country
                regionLine = "";
                if (address.getLocality() != null) regionLine += address.getLocality() + ", ";
                if (address.getAdminArea() != null) regionLine += address.getAdminArea() + ", ";
                if (address.getCountryName() != null) regionLine += address.getCountryName()+", ";
                if(address.getPostalCode() != null) regionLine += address.getPostalCode();
                fetchedAddress = areaLine +", "+ regionLine;
                binding.addressText.setText(fetchedAddress);
                navigateToHome();
            }

        } catch (IOException e) {
            binding.addressText.setText("Error fetching address");
            e.printStackTrace();
        }
    }



    /*************************************************************************************************************
     * Author         : Gunnampalli Parameswara Reddy
     * Function Name  : onActivityResult
     * Description    : Handles the result from a settings resolution dialog triggered when location services were
     *                  disabled. If the user enables location (RESULT_OK), it proceeds to fetch the user's location.
     *                  Otherwise, it notifies the user that location access is required using a Toast message.
     * Called By      : Android System upon returning from startResolutionForResult()
     * Parameters     : int requestCode    – Identifier for the location settings resolution request
     *                  int resultCode     – Result status indicating user action (e.g., RESULT_OK or RESULT_CANCELED)
     *                  Intent data        – Additional data returned by the resolution activity (not used here)
     * Return         : None
     *************************************************************************************************************/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                // ✅ User turned on location — fetch it now
                fetchLocation();
            } else {
                // ❌ User declined — show a message or fallback
                Toast.makeText(this, "Location is required to continue", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*************************************************************************************************************
     * Author         : Gunnampalli Parameswara Reddy
     * Function Name  : onRequestPermissionsResult
     * Description    : Handles the runtime permission response for location access. If the permission is granted,
     *                  it proceeds to fetch the user's location. Otherwise, it notifies the user that the
     *                  permission was denied using a Toast message. This is essential for Android 6.0+ devices
     *                  that require runtime permission handling.
     * Called By      : Android System after user responds to the location permission request
     * Parameters     : int requestCode        – The code identifying the permission request (expected: 100)
     *                  String[] permissions   – The array of requested permissions (not directly used)
     *                  int[] grantResults     – Results for the requested permissions
     * Return         : None
     *************************************************************************************************************/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLocation();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    /*************************************************************************************************************
     * Author         : Gunnampalli Parameswara Reddy
     * Function Name  : navigateToHome
     * Description    : Delays navigation to the BottomNavigationManager activity by 3 seconds to allow UI
     *                  transitions or animations (such as displaying the user's address) to complete smoothly.
     *                  It clears the current task stack and starts a new task, preventing the user from
     *                  navigating back to the location screen.
     * Called By      : handleLocation()
     * Parameters     : None
     * Return         : None
     *************************************************************************************************************/
    private void navigateToHome() {
        new android.os.Handler().postDelayed(() -> {
            Intent intent = new Intent(LocationTracker.this, BottomNavigationManager.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("location",fetchedAddress);
            startActivity(intent);
            finish();
        }, 3000);
    }
    /*********************************************************************************************************
     * Method Name     : getFetchedAddress
     * Description     : Retrieves the full formatted address string previously stored during location
     *                   selection via map tap or search.
     * Parameters      : None
     * Return          : String – The stored full address.
     *********************************************************************************************************/
    public static String getFetchedAddress()
    {
        return fetchedAddress;
    }

    /*********************************************************************************************************
     * Method Name     : setFetchedAddress
     * Description     : Updates the fetched address with a new location string, typically after a new
     *                   location is selected by the user.
     * Parameters      : String location – The full formatted address to be saved.
     * Return          : void
     *********************************************************************************************************/
    public static void setFetchedAddress(String location)
    {
        fetchedAddress = location;
    }

    /*********************************************************************************************************
     * Method Name     : getAreaLine
     * Description     : Returns the local area line component of the address (e.g., street, neighborhood).
     * Parameters      : None
     * Return          : String – A localized portion of the address used for display.
     *********************************************************************************************************/
    public static String getAreaLine()
    {
        return areaLine;
    }

    /*********************************************************************************************************
     * Method Name     : getRegionLine
     * Description     : Returns the region-level component of the address (e.g., city, district, state).
     * Parameters      : None
     * Return          : String – Higher-level geographic info used for display.
     *********************************************************************************************************/
    public static String getRegionLine()
    {
        return regionLine;
    }
}