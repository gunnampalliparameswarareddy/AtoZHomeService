package com.service.atozhomeservice.view.ui.placeorder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.service.atozhomeservice.R;
import com.service.atozhomeservice.databinding.FragmentPlaceOrderBinding;
import com.service.atozhomeservice.model.CartItem;
import com.service.atozhomeservice.model.CartManager;
import com.service.atozhomeservice.model.Order;
import com.service.atozhomeservice.view.ui.viewcart.CartAdapter;
import com.service.atozhomeservice.viewmodel.AuthViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/*********************************************************************************************************
 * Author         : Gunnampalli Parameswara Reddy
 * Class Name     : PlaceOrder
 * Description    : Fragment that handles the review and placement of user orders. Displays selected cart
 *                  items, allows the user to choose a payment method (Cash on Service or UPI-based online
 *                  payment), and communicates with AuthViewModel to place or update orders. Also supports
 *                  handling payment success/failure and navigates to the orders screen upon confirmation.
 * Called By      : Navigation component when user proceeds to place an order.
 * Instance Vars  : FragmentPlaceOrderBinding binding – View binding for UI access.
 *                  static int UPI_PAYMENT_REQUEST     – Request code for UPI payment response.
 *                  double totalAmount                 – Total price of selected cart items.
 *                  AuthViewModel viewModel            – ViewModel for placing and updating orders.
 *                  ArrayList<CartItem> cartList       – Items selected by the user.
 *                  String userId                      – Firebase UID of the current user.
 *                  String orderIdToUpdate             – Used to update an existing order if provided.
 *********************************************************************************************************/
public class PlaceOrder extends Fragment {

    private FragmentPlaceOrderBinding binding;
    private static final int UPI_PAYMENT_REQUEST = 199;
    private double totalAmount;
    private AuthViewModel viewModel;
    private ArrayList<CartItem> cartList;
    private String userId;
    private String orderIdToUpdate; // Optional if passed from elsewhere

    /*********************************************************************************************************
     * Method Name     : onCreateView
     * Description     : Inflates the layout, initializes the ViewModel, retrieves cart data, sets up the
     *                   recycler view, observes LiveData for order updates, and manages user interactions
     *                   like payment option selection and order confirmation.
     * Parameters      : LayoutInflater inflater – Inflater used to load fragment layout.
     *                   ViewGroup container     – The parent view for the fragment layout.
     *                   Bundle savedInstanceState – Previous saved state (if any).
     * Return          : View – Root view of the inflated layout.
     *********************************************************************************************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPlaceOrderBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Optional: Get orderId from arguments (if updating an existing one)
        if (getArguments() != null) {
            orderIdToUpdate = getArguments().getString("orderId");
        }

        cartList = new ArrayList<>(CartManager.getInstance().getItems());
        CartAdapter adapter = new CartAdapter(cartList);
        binding.selectedItemsGrid.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.selectedItemsGrid.setAdapter(adapter);

        totalAmount = calculateTotalAmount(cartList);
        binding.totalAmountText.setText(String.format("Total : ₹ %.2f", totalAmount));

        setupObservers();

        binding.paymentOptions.setOnCheckedChangeListener((group, checkedId) -> {
            binding.paymentOptionsContainer.setVisibility(
                    checkedId == R.id.onlinePayment ? View.VISIBLE : View.GONE
            );
        });

        binding.confirmOrderButton.setOnClickListener(v -> {
            int selectedId = binding.paymentOptions.getCheckedRadioButtonId();
            if (selectedId == R.id.cashOnService) {
                if (orderIdToUpdate != null) {
                    // Update existing order fields
                    Map<String, Object> updateFields = new HashMap<>();
                    updateFields.put("paymentType", "Cash on Service");
                    updateFields.put("status", "Confirmed");
                    updateFields.put("totalAmount", totalAmount);
                    viewModel.updateOrder(orderIdToUpdate, updateFields);
                } else {
                    // Create new order
                    Order order = buildOrder("Cash on Service");
                    viewModel.placeOrder(order);
                }
            } else if (selectedId == R.id.onlinePayment) {
                Toast.makeText(getContext(), "Select a UPI app below", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Please select a payment method", Toast.LENGTH_SHORT).show();
            }
        });

        binding.phonepeOption.setOnClickListener(v -> launchUPIPayment("gunnampalliparamesh@ybl", totalAmount));
        binding.gpayOption.setOnClickListener(v -> launchUPIPayment("gunnampalliparamesh@ybl", totalAmount));
        binding.amazonPayOption.setOnClickListener(v -> launchUPIPayment("gunnampalliparamesh@ybl", totalAmount));

        return binding.getRoot();
    }

    /*********************************************************************************************************
     * Method Name     : setupObservers
     * Description     : Attaches observers to LiveData objects in the ViewModel to respond to success or failure
     *                   events for both order creation and update. Navigates to orders screen on success.
     * Parameters      : None
     * Return          : void
     *********************************************************************************************************/
    private void setupObservers() {
        viewModel.getUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Order updated!", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).navigate(R.id.navigation_orders);
            }
        });

        viewModel.getUpdateError().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(getContext(), "Update failed: " + error, Toast.LENGTH_LONG).show();
        });

        viewModel.getOrderSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();
                CartManager.getInstance().clearCart();
                NavHostFragment.findNavController(this).navigate(R.id.navigation_orders);
            }
        });

        viewModel.getOrderError().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(getContext(), "Failed to place order: " + error, Toast.LENGTH_LONG).show();
        });
    }

    /*********************************************************************************************************
     * Method Name     : calculateTotalAmount
     * Description     : Computes the total payable amount based on the quantity and price of items in the cart.
     * Parameters      : ArrayList<CartItem> cartList – List of selected cart items.
     * Return          : double – Total price of the order.
     *********************************************************************************************************/
    private double calculateTotalAmount(ArrayList<CartItem> cartList) {
        double total = 0;
        for (CartItem item : cartList) {
            total += item.getItemPrice() * item.getQuantity();
        }
        return total;
    }

    /*********************************************************************************************************
     * Method Name     : buildOrder
     * Description     : Builds and returns a new Order object using the selected items, amount, timestamp,
     *                   payment type, and current location.
     * Parameters      : String paymentType – "Cash on Service" or "Online Payment".
     * Return          : Order – Populated order object ready for submission.
     *********************************************************************************************************/
    private Order buildOrder(String paymentType) {
        String location = "Unknown";
        if (getContext() != null) {
            location = com.service.atozhomeservice.view.LocationTracker.getFetchedAddress();
        }
        return new Order(cartList, totalAmount, System.currentTimeMillis(), "Confirmed", paymentType, location);
    }

    /*********************************************************************************************************
     * Method Name     : launchUPIPayment
     * Description     : Launches an Intent for external UPI apps to initiate payment using the passed UPI ID
     *                   and amount. Displays toast messages for UPI app availability or missing apps.
     * Parameters      : String upiId – The target UPI ID.
     *                   double amount – The amount to be paid.
     * Return          : void
     *********************************************************************************************************/
    private void launchUPIPayment(String upiId, double amount) {
        String uri = "upi://pay?pa=" + upiId +
                "&pn=AtoZ%20Services&tn=Service%20Order&am=" + amount + "&cu=INR";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(uri));
        Intent chooser = Intent.createChooser(intent, "Pay with");

        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(chooser, UPI_PAYMENT_REQUEST);
        } else {
            Toast.makeText(getContext(), "No UPI app found", Toast.LENGTH_SHORT).show();
        }
    }

    /*********************************************************************************************************
     * Method Name     : onActivityResult
     * Description     : Handles the result from the launched UPI payment app. Checks for payment success and
     *                   proceeds to place or update the order accordingly. Displays messages on failure.
     * Parameters      : int requestCode – The request identifier for UPI payment.
     *                   int resultCode – The result returned by the UPI activity.
     *                   Intent data – Intent containing payment response details.
     * Return          : void
     *********************************************************************************************************/
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPI_PAYMENT_REQUEST) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                String response = data.getStringExtra("response");
                if (response != null && response.toLowerCase().contains("success")) {
                    if (orderIdToUpdate != null) {
                        Map<String, Object> fields = new HashMap<>();
                        fields.put("paymentType", "Online Payment");
                        fields.put("status", "Confirmed");
                        viewModel.updateOrder(orderIdToUpdate, fields);
                    } else {
                        Order order = buildOrder("Online Payment");
                        viewModel.placeOrder(order);
                    }
                } else {
                    Toast.makeText(getContext(), "Payment failed or canceled", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Payment not completed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
