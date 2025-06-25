package com.service.atozhomeservice.view.ui.viewcart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.service.atozhomeservice.R;
import com.service.atozhomeservice.databinding.FragmentViewCartBinding;
import com.service.atozhomeservice.model.CartItem;
import com.service.atozhomeservice.model.CartManager;

import java.util.ArrayList;

/*********************************************************************************************************
 * Author         : Gunnampalli Parameswara Reddy
 * Class Name     : ViewCart
 * Description    : Fragment responsible for displaying the user's current cart. Shows a list of selected
 *                  service items, calculates and displays the total amount, and enables navigation to
 *                  the order placement screen. Handles empty cart state gracefully.
 * Called By      : Navigation component when navigating to the cart screen.
 * Instance Vars  : FragmentViewCartBinding binding – View binding reference for accessing layout views.
 *                  double total_amount – Stores the total price of selected items in the cart.
 *********************************************************************************************************/
public class ViewCart extends Fragment {

    private FragmentViewCartBinding binding;

    private double total_amount;

    /*********************************************************************************************************
     * Constructor     : ViewCart
     * Description     : Default required empty constructor for fragment instantiation.
     * Parameters      : None
     * Return          : None
     *********************************************************************************************************/
    public ViewCart() {
        // Required empty public constructor
    }


    /*********************************************************************************************************
     * Method Name     : onCreate
     * Description     : Lifecycle method invoked when the fragment is being created. Reserved for non-UI
     *                   initialization logic (currently unused).
     * Parameters      : Bundle savedInstanceState – Saved state if re-creating fragment
     * Return          : void
     *********************************************************************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /*********************************************************************************************************
     * Method Name     : onCreateView
     * Description     : Inflates the fragment layout, retrieves items from the CartManager, updates the UI
     *                   based on cart state (empty vs filled), displays cart summary, and enables navigation
     *                   to the order screen.
     * Parameters      : LayoutInflater inflater – Inflater used to load XML layout
     *                   ViewGroup container     – Parent container for fragment UI
     *                   Bundle savedInstanceState – Previous instance state, if any
     * Return          : View – The root view of the fragment layout
     *********************************************************************************************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentViewCartBinding.inflate(inflater, container, false);

        ArrayList<CartItem> cartList = new ArrayList<>(CartManager.getInstance().getItems());

        if(cartList.isEmpty())
        {
            binding.emptyCartLayout.setVisibility(View.VISIBLE);
            binding.cartRecyclerView.setVisibility(View.GONE);
            binding.cartSummaryBar.setVisibility(View.GONE);
        }else
        {
            CartAdapter adapter = new CartAdapter(cartList);
            binding.cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.cartRecyclerView.setAdapter(adapter);
            total_amount = calculateTotalAmount(cartList);
            binding.totalAmountLabel.setText(String.format("Total : ₹ %.2f", total_amount));
            binding.placeOrderButton.setOnClickListener(v->{
                NavHostFragment.findNavController(this).navigate(R.id.navigation_place_order);
            });
        }

        return binding.getRoot();
    }
    /*********************************************************************************************************
     * Method Name     : calculateTotalAmount
     * Description     : Calculates the total payable amount by iterating over each CartItem and summing the
     *                   product of price and quantity.
     * Parameters      : ArrayList<CartItem> cartList – List of items in the cart
     * Return          : double – Total price of items in the cart
     *********************************************************************************************************/
    private double calculateTotalAmount(ArrayList<CartItem> cartList)
    {
        double total =0;
        for (CartItem item: cartList) {
            total += item.getItemPrice() * item.getQuantity();
        }
        return total;
    }
    /*********************************************************************************************************
     * Method Name     : onResume
     * Description     : Ensures the bottom navigation view highlights the cart tab when this fragment resumes.
     * Parameters      : None
     * Return          : void
     *********************************************************************************************************/
    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView navView = requireActivity().findViewById(R.id.bottom_nav_view);
        if (navView.getSelectedItemId() != R.id.navigation_view_cart) {
            navView.setSelectedItemId(R.id.navigation_view_cart);
        }
    }

}