package com.service.atozhomeservice.view.ui.yourorders;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.service.atozhomeservice.R;
import com.service.atozhomeservice.databinding.FragmentOrdersDetailsBinding;
import com.service.atozhomeservice.viewmodel.AuthViewModel;

/*********************************************************************************************************
 * Author         : Gunnampalli Parameswara Reddy
 * Class Name     : OrdersDetails
 * Description    : Fragment responsible for displaying all user orders. It observes LiveData from the
 *                  ViewModel to reactively populate the order history using a RecyclerView. Displays a
 *                  fallback message if no orders exist. Ensures the navigation bar is correctly synced.
 * Called By      : Navigation component during order history flow.
 * Instance Vars  : FragmentOrdersDetailsBinding binding – View binding reference for layout views.
 *                  AuthViewModel viewModel               – ViewModel to fetch orders from Firestore.
 *                  OrderAdapter adapter                  – Adapter for rendering order items.
 *********************************************************************************************************/
public class OrdersDetails extends Fragment {

    private FragmentOrdersDetailsBinding binding;
    private AuthViewModel viewModel;
    private OrderAdapter adapter;

    /*********************************************************************************************************
     * Constructor     : OrdersDetails
     * Description     : Required empty constructor for fragment instantiation.
     * Parameters      : None
     * Return          : None
     *********************************************************************************************************/
    public OrdersDetails() {
        // Required empty public constructor
    }

    /*********************************************************************************************************
     * Method Name     : onCreate
     * Description     : Lifecycle method invoked at fragment creation. Reserved for non-UI logic if needed.
     * Parameters      : Bundle savedInstanceState – Saved state information.
     * Return          : void
     *********************************************************************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /*********************************************************************************************************
     * Method Name     : onCreateView
     * Description     : Inflates the layout, sets up the RecyclerView, observes LiveData from ViewModel to
     *                   update UI based on order presence, and triggers Firestore fetch.
     * Parameters      : LayoutInflater inflater – Inflater to load the layout.
     *                   ViewGroup container     – Parent container for the fragment view.
     *                   Bundle savedInstanceState – Previously saved fragment state.
     * Return          : View – Root view of the fragment layout.
     *********************************************************************************************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrdersDetailsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Set up RecyclerView
        binding.ordersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.ordersRecycler.setHasFixedSize(true);

        // Observe Firestore LiveData
        viewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            if (orders.isEmpty()) {
                binding.emptyText.setVisibility(View.VISIBLE);
            } else {
                binding.emptyText.setVisibility(View.GONE);
                adapter = new OrderAdapter(getContext(), orders);
                binding.ordersRecycler.setAdapter(adapter);
            }
        });

        // Trigger fetch
        viewModel.fetchOrders();

        return binding.getRoot();
    }


    /*********************************************************************************************************
     * Method Name     : onResume
     * Description     : Ensures the bottom navigation bar highlights the orders tab when the fragment resumes.
     * Parameters      : None
     * Return          : void
     *********************************************************************************************************/
    @Override
    public void onResume() {
        super.onResume();

        BottomNavigationView navView = requireActivity().findViewById(R.id.bottom_nav_view);
        if (navView.getSelectedItemId() != R.id.navigation_orders) {
            navView.setSelectedItemId(R.id.navigation_orders);
        }
    }
}