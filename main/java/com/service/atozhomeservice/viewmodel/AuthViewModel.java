package com.service.atozhomeservice.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.service.atozhomeservice.model.CartItem;
import com.service.atozhomeservice.model.CustomerData;
import com.service.atozhomeservice.model.Order;
import com.service.atozhomeservice.repository.AuthRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*************************************************************************************************************
 * Author         : Gunnampalli Parameswara Reddy
 * Class Name     : AuthViewModel
 * Description    : Provides a lifecycle-aware bridge between the UI layer and the AuthRepository for handling
 *                  customer-related data operations. Maintains separation of concerns by delegating all
 *                  persistence logic to the repository, ensuring clean MVVM architecture.
 *************************************************************************************************************/

public class AuthViewModel extends AndroidViewModel {

    private AuthRepository repository;
    private final MutableLiveData<Boolean> orderSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> orderError = new MutableLiveData<>();

    private final MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> updateError = new MutableLiveData<>();

    private final MutableLiveData<List<Order>> ordersLiveData = new MutableLiveData<>();


    /*********************************************************************************************************
     * Author         : Gunnampalli Parameswara Reddy
     * Function Name  : AuthViewModel (Constructor)
     * Description    : Initializes the ViewModel with the application context and instantiates the
     *                  AuthRepository to enable access to customer authentication-related methods.
     * Called By      : ViewModelProvider or system during activity/fragment lifecycle
     * Parameters     : Application application – required context for repository initialization
     * Return         : None
     *********************************************************************************************************/
    public AuthViewModel(@NonNull Application application) {
        super(application);
        repository = new AuthRepository(application);
    }

    /*********************************************************************************************************
     * Author         : Gunnampalli Parameswara Reddy
     * Function Name  : saveCustomerDetails
     * Description    : Forwards the customer data to the AuthRepository for persistence in Firestore.
     *                  The result of the operation is wrapped in LiveData<Boolean> to be observed by the UI.
     * Called By      : UI components (e.g., fragments or activities)
     * Parameters     : CustomerData customerData – customer input to be saved
     * Return         : LiveData<Boolean> – true if save is successful, false otherwise
     *********************************************************************************************************/
    public LiveData<Boolean> saveCustomerDetails(CustomerData customerData) {
        return repository.saveUserDetails(customerData);
    }

    /*********************************************************************************************************
     * Method Name     : getUpdateSuccess
     * Description     : Exposes LiveData that emits true when an order update operation completes successfully.
     * Parameters      : None
     * Return          : LiveData<Boolean> – Indicates update success state.
     *********************************************************************************************************/
    public LiveData<Boolean> getUpdateSuccess() {
        return updateSuccess;
    }

    /*********************************************************************************************************
     * Method Name     : getUpdateError
     * Description     : Exposes LiveData containing error messages related to order update failures.
     * Parameters      : None
     * Return          : LiveData<String> – Update error message.
     *********************************************************************************************************/
    public LiveData<String> getUpdateError() {
        return updateError;
    }

    /*********************************************************************************************************
     * Method Name     : getOrderSuccess
     * Description     : Exposes LiveData that emits true when a new order is placed successfully.
     * Parameters      : None
     * Return          : LiveData<Boolean> – Indicates placement success.
     *********************************************************************************************************/
    public LiveData<Boolean> getOrderSuccess() {
        return orderSuccess;
    }

    /*********************************************************************************************************
     * Method Name     : getOrderError
     * Description     : Exposes LiveData for order placement errors reported during repository operations.
     * Parameters      : None
     * Return          : LiveData<String> – Order creation failure message.
     *********************************************************************************************************/
    public LiveData<String> getOrderError() {
        return orderError;
    }

    /*********************************************************************************************************
     * Method Name     : placeOrder
     * Description     : Triggers the repository to save a new order under the authenticated user's UID.
     *                   Updates LiveData based on the success or failure result of the operation.
     * Parameters      : Order order – The new order to be persisted.
     * Return          : void
     *********************************************************************************************************/
    public void placeOrder(Order order) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        repository.placeOrder(userId, order, new AuthRepository.OrderCallback() {
            @Override
            public void onSuccess() {
                orderSuccess.setValue(true);
            }

            @Override
            public void onFailure(String errorMessage) {
                orderError.setValue(errorMessage);
            }
        });
    }

    /*********************************************************************************************************
     * Method Name     : updateOrder
     * Description     : Updates fields of an existing order in Firestore tied to the authenticated user.
     *                   Emits success or error result to LiveData observers.
     * Parameters      : String orderId – ID of the order to be updated.
     *                   Map<String, Object> fields – Fields to be updated with their new values.
     * Return          : void
     *********************************************************************************************************/
    public void updateOrder(String orderId, Map<String, Object> fields) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        repository.updateOrder(userId, orderId, fields, new AuthRepository.OrderCallback() {
            @Override
            public void onSuccess() {
                updateSuccess.setValue(true);
            }

            @Override
            public void onFailure(String errorMessage) {
                updateError.setValue(errorMessage);
            }
        });
    }

    /*********************************************************************************************************
     * Method Name     : getOrders
     * Description     : Returns a LiveData reference containing the list of orders for the user.
     *                   Typically observed by UI fragments.
     * Parameters      : None
     * Return          : LiveData<List<Order>> – Reactive order list.
     *********************************************************************************************************/
    public LiveData<List<Order>> getOrders() {
        return ordersLiveData;
    }

    /*********************************************************************************************************
     * Method Name     : fetchOrders
     * Description     : Initiates fetching of order history from the repository for the current user.
     *                   Updates the LiveData list or falls back to an empty list if retrieval fails.
     * Parameters      : None
     * Return          : void
     *********************************************************************************************************/
    public void fetchOrders() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        repository.fetchOrders(userId, new AuthRepository.OrderListCallback() {
            @Override
            public void onSuccess(List<Order> orders) {
                ordersLiveData.setValue(orders);
            }

            @Override
            public void onFailure(String error) {
                ordersLiveData.setValue(new ArrayList<>());
            }
        });
    }


}

