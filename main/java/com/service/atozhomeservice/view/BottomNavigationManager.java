package com.service.atozhomeservice.view;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.service.atozhomeservice.R;
import com.service.atozhomeservice.databinding.ActivityBottomNavigationManagerBinding;
/*********************************************************************************************************
 * Author         : Gunnampalli Parameswara Reddy
 * Class Name     : BottomNavigationManager
 * Description    : Main activity responsible for managing navigation between Home, Cart, and Orders
 *                  sections using BottomNavigationView and Navigation Components. Ensures that navigation
 *                  resets back stack appropriately and handles back press behavior based on login state.
 * Called By      : Android Launcher / Manifest-defined main activity.
 * Instance Vars  : ActivityBottomNavigationManagerBinding binding – View binding for layout components.
 *                  BottomNavigationView navView – Bottom navigation component instance.
 *                  FirebaseAuth auth – Firebase Authentication instance for checking login state.
 *********************************************************************************************************/
public class BottomNavigationManager extends AppCompatActivity {

    private ActivityBottomNavigationManagerBinding binding;
    private BottomNavigationView navView;

    private FirebaseAuth auth;

    /*********************************************************************************************************
     * Method Name     : onCreate
     * Description     : Initializes the activity, sets up view binding, assigns navigation logic to bottom
     *                   menu items with proper back stack clearing. Ensures redundant navigation is avoided.
     * Parameters      : Bundle savedInstanceState – Saved instance state, if any.
     * Return          : void
     *********************************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();


        binding = ActivityBottomNavigationManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navView = binding.bottomNavView;
/*        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_view_cart, R.id.navigation_orders)
                .build();
        */

        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                NavController navController = Navigation.findNavController(BottomNavigationManager.this,R.id.nav_host_fragment_activity_bottom_navigation_manager);
                int currentDestinationId = navController.getCurrentDestination().getId();
                if (currentDestinationId != id) {
                    navController.navigate(id, null, new NavOptions.Builder()
                            .setPopUpTo(id, true)
                            .build());
                }


                if (id == R.id.navigation_home) {
                    navController.navigate(R.id.navigation_home,null, new NavOptions.Builder()
                            .setPopUpTo(R.id.navigation_home, true) // Clears all previous instances
                            .build());
                } else if (id == R.id.navigation_view_cart) {
                    navController.navigate(R.id.navigation_view_cart,null,new NavOptions.Builder()
                            .setPopUpTo(R.id.navigation_view_cart, true) // Clears all previous instances
                            .build());
                }
                else if (id == R.id.navigation_orders) {
                    navController.navigate(R.id.navigation_orders,null,new NavOptions.Builder()
                            .setPopUpTo(R.id.navigation_orders, true) // Clears all previous instances
                            .build());
                }
                return true;
            }
        });
    }

    /*********************************************************************************************************
     * Method Name     : onBackPressed
     * Description     : Overrides default back press behavior. If user is logged in and on the home screen,
     *                   exits the app. Else, allows normal back navigation.
     * Parameters      : None
     * Return          : void
     *********************************************************************************************************/
    @Override
    public void onBackPressed() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_bottom_navigation_manager);
        int currentDestinationId = navController.getCurrentDestination().getId();
        // Check if user is logged in
        if (auth != null && auth.getCurrentUser() != null) {
            if (currentDestinationId == R.id.navigation_home) {
                // ✅ If already on Home, exit the app
                finishAffinity(); // Closes all activities in the task
            } else {
                super.onBackPressed(); // Navigate back normally
            }
        } else {
            // ✅ If user is NOT logged in, allow normal back navigation
            super.onBackPressed();
        }
    }


}