package com.service.atozhomeservice.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.service.atozhomeservice.model.CartItem;
import com.service.atozhomeservice.model.CustomerData;
import com.service.atozhomeservice.model.Order;
import com.google.firebase.firestore.Query;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*************************************************************************************************************
 * Author         : Gunnampalli Parameswara Reddy
 * Class Name     : AuthRepository
 * Description    : Repository layer responsible for interfacing with Firebase Authentication and Firestore.
 *                  Handles the persistence of customer data using Firestore's document-based structure.
 *************************************************************************************************************/
public class AuthRepository {

    // Instance Variables
    private final FirebaseAuth auth;
    private final FirebaseFirestore database;

    private final MutableLiveData<List<Order>> ordersLiveData = new MutableLiveData<>();

    /*********************************************************************************************************
     * Author         : Gunnampalli Parameswara Reddy
     * Function Name  : AuthRepository (Constructor)
     * Description    : Initializes instances for Firebase Authentication and Firestore to be used across
     *                  repository functions. Accepts Application context if further app-specific resources
     *                  need to be accessed.
     * Called By      : ViewModel layer or App initialization
     * Parameters     : Application application – the context passed from the ViewModel
     * Return         : None
     *********************************************************************************************************/
    public AuthRepository(Application application) {
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
    }

    /*************************************************************************************************************
     * Author         : Gunnampalli Parameswara Reddy
     * Function Name  : saveUserDetails
     * Description    : Appends a new service request to the "serviceRequests" array in the Firestore document
     *                  identified by the customerId. Uses FieldValue.arrayUnion() to avoid overwriting existing
     *                  services and merge strategy to preserve user data.
     * Called By      : Any form submission logic that collects and stores customer service information
     * Parameters     : CustomerData customerData – the object containing all necessary customer service details
     * Return         : LiveData<Boolean> – true if save is successful, false otherwise
     *************************************************************************************************************/
    public LiveData<Boolean> saveUserDetails(CustomerData customerData) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        if (customerData == null || customerData.getCustomerId() == null) {
            result.setValue(false);
            return result;
        }

        DocumentReference documentRef = database
                .collection("users")
                .document(customerData.getCustomerId());

        // Create a map for the new service request
        Map<String, Object> serviceEntry = new HashMap<>();
        serviceEntry.put("customerName", customerData.getCustomerName());
        serviceEntry.put("customerServiceType", customerData.getTypeOfService());
        serviceEntry.put("customerSubServiceType", customerData.getTypeOfSubService());
        serviceEntry.put("customerPreferredDateTime", customerData.getPreferredDateTime());
        serviceEntry.put("customerStreetName", customerData.getStreetName());
        serviceEntry.put("customerCityName", customerData.getCityName());
        serviceEntry.put("customerStateName", customerData.getStateName());
        serviceEntry.put("customerCountryName", customerData.getCountryName());
        serviceEntry.put("customerPinCode", customerData.getPinCode());

        // Use FieldValue.arrayUnion() to append the service entry
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("customerId", customerData.getCustomerId()); // to ensure ID is retained
        updateMap.put("serviceRequests", FieldValue.arrayUnion(serviceEntry));

        documentRef.set(updateMap, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.setValue(true);
                    } else {
                        Log.e("FirestoreError", "Failed to append service request", task.getException());
                        result.setValue(false);
                    }
                });

        return result;
    }

    /*********************************************************************************************************
     * Interface Name  : OrderCallback
     * Description     : Callback interface for asynchronous order operations. Invoked when an order creation
     *                   or update request completes successfully or fails with an error.
     *********************************************************************************************************/
    public interface OrderCallback {
        /*********************************************************************************************************
         * Method Name     : onSuccess
         * Description     : Called when the corresponding Firestore operation succeeds.
         * Parameters      : None
         * Return          : void
         *********************************************************************************************************/
        void onSuccess();
        /*********************************************************************************************************
         * Method Name     : onFailure
         * Description     : Called when the corresponding Firestore operation fails.
         * Parameters      : String errorMessage – Message describing the failure reason.
         * Return          : void
         *********************************************************************************************************/
        void onFailure(String errorMessage);
    }

    /*********************************************************************************************************
     * Method Name     : placeOrder
     * Description     : Saves a new order to Firestore under the current user's document. Invokes the
     *                   OrderCallback methods to indicate success or failure.
     * Parameters      : String userId – ID of the user placing the order.
     *                   Order order – Order object to be added.
     *                   OrderCallback callback – Listener to handle response.
     * Return          : void
     *********************************************************************************************************/
    public void placeOrder(String userId, Order order, OrderCallback callback) {
        database.collection("users")
                .document(userId)
                .collection("orders")
                .add(order)
                .addOnSuccessListener(documentReference -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
    /*********************************************************************************************************
     * Method Name     : updateOrder
     * Description     : Updates an existing order document with new field values in Firestore. Notifies via
     *                   OrderCallback about result of the operation.
     * Parameters      : String userId – User ID owning the order.
     *                   String orderId – ID of the order to update.
     *                   Map<String, Object> updatedFields – Fields to modify in the order.
     *                   OrderCallback callback – Listener for operation results.
     * Return          : void
     *********************************************************************************************************/
    public void updateOrder(String userId, String orderId, Map<String, Object> updatedFields, OrderCallback callback) {
        database.collection("users")
                .document(userId)
                .collection("orders")
                .document(orderId)
                .update(updatedFields)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    /*********************************************************************************************************
     * Method Name     : getOrders
     * Description     : Returns the LiveData object containing the list of orders.
     * Parameters      : None
     * Return          : LiveData<List<Order>> – Reference to observable order list.
     *********************************************************************************************************/
    public LiveData<List<Order>> getOrders() {
        return ordersLiveData;
    }

    /*********************************************************************************************************
     * Method Name     : fetchOrders
     * Description     : Retrieves orders from Firestore in descending timestamp order and updates the
     *                   observer via callback.
     * Parameters      : String userId – ID of the user whose orders should be fetched.
     *                   OrderListCallback callback – Listener for success or failure.
     * Return          : void
     *********************************************************************************************************/
    public void fetchOrders(String userId, OrderListCallback callback) {
        database.collection("users")
                .document(userId)
                .collection("orders")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Order> orders = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Order order = doc.toObject(Order.class);
                        orders.add(order);
                    }
                    callback.onSuccess(orders);
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e.getMessage());
                });
    }

    /*********************************************************************************************************
     * Interface Name  : OrderListCallback
     * Description     : Callback interface to return a list of fetched orders or an error message.
     *********************************************************************************************************/
    public interface OrderListCallback {
        /*********************************************************************************************************
         * Method Name     : onSuccess
         * Description     : Called when the list of orders is successfully retrieved.
         * Parameters      : List<Order> orders – The list of fetched orders.
         * Return          : void
         *********************************************************************************************************/
        void onSuccess(List<Order> orders);
        /*********************************************************************************************************
         * Method Name     : onFailure
         * Description     : Called when order retrieval fails.
         * Parameters      : String errorMessage – Description of the failure reason.
         * Return          : void
         *********************************************************************************************************/
        void onFailure(String errorMessage);
    }

}
