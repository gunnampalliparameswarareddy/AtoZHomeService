package com.service.atozhomeservice.view.ui.recyclerlayout;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.service.atozhomeservice.R;
import com.service.atozhomeservice.databinding.FragmentBackgroundBinding;
import com.service.atozhomeservice.view.LocationTracker;
import com.service.atozhomeservice.view.ui.searchlocation.SearchLocation;

import java.util.Arrays;
import java.util.List;

/*********************************************************************************************************
 * Author         : Gunnampalli Parameswara Reddy
 * Class Name     : BackgroundFragment
 * Description    : Represents the home screen fragment displaying key service categories and user location.
 *                  Implements OnServiceCategoryClickListener to handle click events from the service grid.
 *                  This fragment inflates its view with custom layout binding and integrates navigation
 *                  logic and auto-scrolling image slider functionalities.
 * Called By      : Navigation component during app navigation.
 * Implements     : OnServiceCategoryClickListener – for handling service item selections.
 * Instance Vars  : FragmentBackgroundBinding binding – for accessing views defined in the layout XML.
 *********************************************************************************************************/

public class BackgroundFragment extends Fragment implements OnServiceCategoryClickListener {

    //Instance Variables
    private FragmentBackgroundBinding binding;
    public BackgroundFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    /*********************************************************************************************************
     * Author         : Gunnampalli Parameswara Reddy
     * Function Name  : onCreateView
     * Description    : Inflates the layout for the background fragment, initializes UI components with
     *                  location data, sets up a category grid with custom adapter, configures an image
     *                  slider with auto-scroll functionality, and defines navigation for location search
     *                  and cart viewing actions.
     * Called By      : Fragment lifecycle during layout rendering.
     * Parameters     : LayoutInflater inflater – The LayoutInflater object that can be used to inflate any
     *                                            views in the fragment.
     *                  ViewGroup container     – If non-null, this is the parent view that the fragment's
     *                                            UI should be attached to.
     *                  Bundle savedInstanceState – If non-null, this fragment is being re-constructed from
     *                                              a previous saved state as given here.
     * Return         : View – The root view of the inflated and initialized fragment UI.
     *********************************************************************************************************/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentBackgroundBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        binding.displaySubLocation.setText(LocationTracker.getAreaLine());
        binding.displayLocation.setText(LocationTracker.getRegionLine());

        int [] image_ids ={R.drawable.carpentering,R.drawable.electricalservices,R.drawable.plumber1,R.drawable.weldingservices,R.drawable.tilefittingservices,R.drawable.painting};
        String [] labels = {"Carpenter","Electrician","Plumber","Welder","Tile & Flooring","Painter"};

        CustomAdapter customAdapter = new CustomAdapter(requireContext(),this,image_ids,labels,this);
        binding.gridView.setAdapter(customAdapter);


        binding.displayLocation.setOnClickListener(v->{
            navigateToSearchLocation();
        });

        binding.viewCartButton.setOnClickListener(v->{navigateToViewCart();});

        //Sliding the Images on Home Screen using Slider using adapter
        List<Integer> images= Arrays.asList(R.drawable.carpentering,R.drawable.electricalservices,R.drawable.plumber1,R.drawable.weldingservices,R.drawable.tilefittingservices,R.drawable.painting);
        ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(images);
        binding.imageSlider.setAdapter(imageSliderAdapter);

        final int[] currentPage = {0};
        Handler handler = new Handler();
        Runnable sliderRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentPage[0] == images.size()) {
                    currentPage[0] = 0;
                    binding.imageSlider.setCurrentItem(currentPage[0], false); // no animation
                } else {
                    binding.imageSlider.setCurrentItem(currentPage[0], true);  // smooth animation
                }
                currentPage[0]++;
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(sliderRunnable, 3000);

        return root;
    }

    /*********************************************************************************************************
     * Author         : Gunnampalli Parameswara Reddy
     * Function Name  : onServiceClicked
     * Description    : Handles click events from service selection. Based on the provided label, it navigates
     *                  to the corresponding service fragment using the navigation component.
     * Called By      : CustomAdapter (when a service item is clicked in the GridView)
     * Parameters     : String label – The label of the clicked service item (e.g., "Carpenter", "Plumber")
     * Return         : void
     *********************************************************************************************************/
    @Override
    public void onServiceClicked(String label) {
        // Handle navigation based on label
        switch (label) {
            case "Carpenter":
                NavHostFragment.findNavController(this).navigate(R.id.navigation_carpenter_service);
                break;
        }
    }


    /*********************************************************************************************************
     * Author         : Gunnampalli Parameswara Reddy
     * Function Name  : navigateToSearchLocation
     * Description    : Initiates navigation to the Search Location fragment using the Navigation component.
     *                  This method is triggered when the user taps on the current location display.
     * Called By      : onCreateView (via displayLocation setOnClickListener)
     * Parameters     : None
     * Return         : void
     *********************************************************************************************************/

    private void navigateToSearchLocation()
    {
        NavHostFragment.findNavController(this)
                .navigate(R.id.navigation_search_address);
    }

    /*********************************************************************************************************
     * Author         : Gunnampalli Parameswara Reddy
     * Function Name  : navigateToViewCart
     * Description    : Navigates to the View Cart fragment using the Navigation component. Triggered when
     *                  the user taps the "View Cart" button to review selected services or items.
     * Called By      : onCreateView (via viewCartButton setOnClickListener)
     * Parameters     : None
     * Return         : void
     *********************************************************************************************************/

    private void navigateToViewCart()
    {
        NavHostFragment.findNavController(this)
                .navigate(R.id.navigation_view_cart);
    }
}