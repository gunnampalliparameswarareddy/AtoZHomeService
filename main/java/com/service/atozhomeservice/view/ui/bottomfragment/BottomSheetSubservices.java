package com.service.atozhomeservice.view.ui.bottomfragment;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.service.atozhomeservice.R;
import com.service.atozhomeservice.databinding.FragmentBottomSheetSubservicesBinding;
import com.service.atozhomeservice.model.CartItem;
import com.service.atozhomeservice.model.CartManager;
import com.service.atozhomeservice.view.ui.recyclerlayout.QuantityAdapter;

import java.util.ArrayList;
import java.util.List;

/*********************************************************************************************************
 * Author         : Gunnampalli Parameswara Reddy
 * Class Name     : BottomSheetSubservices
 * Description    : A BottomSheetDialogFragment that displays sub-services related to a selected service
 *                  category. Allows users to adjust item quantities, updates the cart accordingly, and
 *                  displays the total price. Facilitates navigation to cart view.
 * Called By      : Service fragments (e.g., Carpenter) when a sub-service grid needs to be shown.
 * Instance Vars  : String[] labels         – Sub-service labels
 *                  int[] icons             – Drawable resource IDs for sub-service icons
 *                  double[] itemPrices     – Prices for each sub-service item
 *                  String title            – Title displayed in the sheet
 *                  String mainService      – Parent category of the sub-services
 *                  FragmentBottomSheetSubservicesBinding binding – View binding reference
 *********************************************************************************************************/
public class BottomSheetSubservices extends BottomSheetDialogFragment {

    private String []labels;
    private int [] icons;
    private String title;

    private String mainService;

    private double [] itemPrices;
    private FragmentBottomSheetSubservicesBinding binding;

    /*********************************************************************************************************
     * Constructor     : BottomSheetSubservices
     * Description     : Default constructor. Required for instantiating the dialog fragment.
     * Parameters      : None
     * Return          : None
     *********************************************************************************************************/
    public BottomSheetSubservices() {
        // Required empty public constructor
    }

    /*********************************************************************************************************
     * Static Method   : newInstance
     * Description     : Factory method to create and pass arguments to a new instance of the dialog.
     * Parameters      : String mainService – Parent service category
     *                   String title       – Header/title for the sub-service sheet
     *                   int[] icons        – Array of drawable IDs for icons
     *                   String[] labels    – Sub-service labels
     *                   double[] itemPrices– Prices for each sub-service item
     * Return          : BottomSheetSubservices – Configured instance of the bottom sheet
     *********************************************************************************************************/
    public static BottomSheetSubservices newInstance(String mainService,String title, int[] icons, String[] labels,double [] itemPrices) {
        BottomSheetSubservices sheet = new BottomSheetSubservices();
        Bundle args = new Bundle();
        args.putString("mainService",mainService);
        args.putString("title", title);
        args.putIntArray("icons", icons);
        args.putStringArray("labels", labels);
        args.putDoubleArray("itemprices",itemPrices);
        sheet.setArguments(args);
        return sheet;
    }


    /*********************************************************************************************************
     * Method Name     : onCreate
     * Description     : Extracts arguments passed via bundle and initializes corresponding instance variables.
     * Called By       : Fragment lifecycle
     * Parameters      : Bundle savedInstanceState – Previously saved instance state
     * Return          : void
     *********************************************************************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mainService = getArguments().getString("mainService");
            title = getArguments().getString("title");
            icons = getArguments().getIntArray("icons");
            labels = getArguments().getStringArray("labels");
            itemPrices = getArguments().getDoubleArray("itemprices");
        }

    }

    /*********************************************************************************************************
     * Method Name     : onCreateView
     * Description     : Inflates the fragment layout, sets up the header, item grid, and UI click listeners.
     *                   Binds the QuantityAdapter and updates the total amount on load and whenever items change.
     * Called By       : Fragment lifecycle
     * Parameters      : LayoutInflater inflater – Inflater for layout XML
     *                   ViewGroup container     – Parent view group
     *                   Bundle savedInstanceState – Saved state if any
     * Return          : View – The inflated and configured root view
     *********************************************************************************************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentBottomSheetSubservicesBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        binding.headerTitle.setText(title);
        QuantityAdapter adapter = new QuantityAdapter(requireContext(), this,mainService,title, icons, labels,itemPrices,this::updateTotalAmount);
        binding.itemView.setAdapter(adapter);
        updateTotalAmount();
        binding.btnClose.setOnClickListener(v->dismiss());

        binding.viewCart.setOnClickListener(v->{
            dismiss();
            NavHostFragment.findNavController(this).navigate(R.id.navigation_view_cart);
        });
        return root;
    }
    /*********************************************************************************************************
     * Method Name     : updateTotalAmount
     * Description     : Calculates and updates the total cost of all selected items from the cart.
     * Called By       : QuantityAdapter (via callback) and onCreateView
     * Parameters      : None
     * Return          : void
     *********************************************************************************************************/
    private void updateTotalAmount() {
        double total = 0.0;
        List<CartItem> cartItems = CartManager.getInstance().getItems();
        for (CartItem item : cartItems) {
            total += item.getItemPrice() * item.getQuantity();
        }
        binding.totalItemAmount.setText(String.format("₹ %.2f", total));
    }

    /*********************************************************************************************************
     * Method Name     : onCreateDialog
     * Description     : Customizes bottom sheet behavior including peek height and initial collapsed state.
     * Called By       : Fragment lifecycle
     * Parameters      : Bundle savedInstanceState – Saved dialog state
     * Return          : Dialog – Customized BottomSheetDialog instance
     *********************************************************************************************************/
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setPeekHeight((int)(Resources.getSystem().getDisplayMetrics().heightPixels * 0.5f));
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        return dialog;
    }

    /*********************************************************************************************************
     * Method Name     : onResume
     * Description     : Ensures bottom navigation highlights the "Home" tab while this sheet is active.
     * Called By       : Fragment lifecycle
     * Parameters      : None
     * Return          : void
     *********************************************************************************************************/
    @Override
    public void onResume() {
        super.onResume();

        BottomNavigationView navView = requireActivity().findViewById(R.id.bottom_nav_view);
        if (navView.getSelectedItemId() != R.id.navigation_home) {
            navView.setSelectedItemId(R.id.navigation_home);
        }
    }
}