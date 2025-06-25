package com.service.atozhomeservice.view.ui.carpentering;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.service.atozhomeservice.R;
import com.service.atozhomeservice.databinding.FragmentCarpenterBinding;
import com.service.atozhomeservice.view.ui.bottomfragment.BottomSheetSubservices;
import com.service.atozhomeservice.view.ui.recyclerlayout.CustomAdapter;
import com.service.atozhomeservice.view.ui.recyclerlayout.OnServiceCategoryClickListener;
import com.service.atozhomeservice.view.ui.recyclerlayout.QuantityAdapter;
/*********************************************************************************************************
 * Author         : Gunnampalli Parameswara Reddy
 * Class Name     : carpenter
 * Description    : Fragment representing the "Carpenter" category in the service app. Displays a grid of
 *                  available carpenter services using a CustomAdapter and handles user interactions for
 *                  each service. If a category supports sub-services, a bottom sheet dialog is displayed;
 *                  otherwise, direct navigation is triggered.
 * Called By      : Navigation component when navigating to carpenter-related services.
 * Implements     : OnServiceCategoryClickListener – Handles click interactions on service grid items.
 * Instance Vars  : FragmentCarpenterBinding binding – Used to bind views declared in layout XML to code.
 *********************************************************************************************************/
public class carpenter extends Fragment implements OnServiceCategoryClickListener {

    private FragmentCarpenterBinding binding;

    public carpenter() {
        // Required empty public constructor
    }

    /*************************************************************************************************************
     * Author         : Gunnampalli Parameswara Reddy
     * Function Name  : onCreateView
     * Description    : Initializes the carpenter service fragment UI. It inflates the layout using view binding,
     *                  prepares image resources and labels representing different carpenter services (e.g., cupboards,
     *                  fittings, repairs), and sets up a custom adapter to populate a GridView with service items.
     *                  This setup ensures the fragment displays a visual selection grid to the user.
     * Called By      : Android Framework when the fragment view is being created
     * Parameters     : LayoutInflater inflater      – Used to inflate the fragment layout
     *                  ViewGroup container          – Parent view that the fragment UI should be attached to
     *                  Bundle savedInstanceState    – Previously saved state if any
     * Return         : View                         – The root view of the inflated fragment layout
     *************************************************************************************************************/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCarpenterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        int[] image_ids = {
                R.drawable.cupboards, R.drawable.kitchenfitting, R.drawable.shelves_decor,
                R.drawable.woodendoor, R.drawable.window_curtain, R.drawable.furniture_repair,
                R.drawable.clothes_hanger
        };

        String[] labels = {
                "Cupboards & Drawers", "Kitchen Fittings", "Shelves & Decor",
                "Wooden Door", "Window & Repair", "Furniture Repair", "Clothes Hanger"
        };

        CustomAdapter customAdapter = new CustomAdapter(requireContext(), this, image_ids, labels, this);
        binding.gridView.setAdapter(customAdapter);
        return root;
    }

    /*********************************************************************************************************
     * Method Name     : onServiceClicked
     * Description     : Handles item selection from the service grid. When "Cupboards & Drawers" is selected,
     *                   a BottomSheet is shown with corresponding sub-services and their pricing. For other
     *                   generic labels, navigates to predefined destinations.
     * Called By       : CustomAdapter when a service item is tapped.
     * Parameters      : String label – Label of the tapped service item.
     * Return          : void
     *********************************************************************************************************/
    @Override
    public void onServiceClicked(String label) {
        if (label.equals("Cupboards & Drawers")) {
            int[] ids = {
                    R.drawable.cupboardlock, R.drawable.cabinethinges,
                    R.drawable.wardrobe, R.drawable.cupboardrepair
            };

            String[] titles = {
                    "Cupboard Locks Repair", "Cupboard Cabinet",
                    "Cupboard Wardrobe", "Cupboard Repair"
            };

            double [] prices ={
                    99.0,72,100,200
            };

            BottomSheetSubservices sheet = BottomSheetSubservices.newInstance("Carpenter",label, ids, titles,prices);
            sheet.show(getParentFragmentManager(), "SubServices");

        } else if (label.equals("Carpenter")) {
            NavHostFragment.findNavController(this).navigate(R.id.navigation_carpenter_service);
        }
    }
}

