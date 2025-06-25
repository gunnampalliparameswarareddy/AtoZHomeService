package com.service.atozhomeservice.view.ui.viewcart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.service.atozhomeservice.R;
import com.service.atozhomeservice.model.CartItem;

import java.util.ArrayList;
/*********************************************************************************************************
 * Author         : Gunnampalli Parameswara Reddy
 * Class Name     : CartAdapter
 * Description    : RecyclerView adapter for displaying items in the user's cart. Binds item data such as
 *                  icon, label, quantity, price, and service hierarchy to corresponding views in the cart layout.
 * Called By      : ViewCart, PlaceOrder fragments, or any screen displaying selected cart items.
 * Instance Vars  : ArrayList<CartItem> cartItems – List of cart items to be displayed.
 *********************************************************************************************************/
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private ArrayList<CartItem> cartItems;

    /*********************************************************************************************************
     * Constructor     : CartAdapter
     * Description     : Initializes the adapter with a list of CartItem objects.
     * Parameters      : ArrayList<CartItem> cartItems – List of selected items in the user's cart.
     * Return          : None
     *********************************************************************************************************/
    public CartAdapter(ArrayList<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    /*********************************************************************************************************
     * Method Name     : onCreateViewHolder
     * Description     : Inflates the layout for a single cart item and wraps it in a ViewHolder.
     * Parameters      : ViewGroup parent – Parent view that holds the RecyclerView
     *                   int viewType – View type of the new View
     * Return          : CartViewHolder – ViewHolder containing the inflated cart item layout.
     *********************************************************************************************************/
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your cart item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    /*********************************************************************************************************
     * Method Name     : onBindViewHolder
     * Description     : Binds item data to the views inside the ViewHolder at the given position.
     * Parameters      : CartViewHolder holder – ViewHolder containing views to be populated.
     *                   int position – Position of the item within the adapter.
     * Return          : void
     *********************************************************************************************************/
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.itemImage.setImageResource(item.getIcon());
        holder.label.setText(item.getItemType());
        holder.quantity.setText("Qty: " + item.getQuantity());
        holder.price.setText(String.format("₹ %.2f", item.getItemPrice()*item.getQuantity()));
        holder.service.setText(item.getMainService() + " > " + item.getSubService());
    }

    /*********************************************************************************************************
     * Method Name     : getItemCount
     * Description     : Returns the number of items present in the cart.
     * Parameters      : None
     * Return          : int – Total number of cart items.
     *********************************************************************************************************/
    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    /*********************************************************************************************************
     * Inner Class     : CartViewHolder
     * Description     : Holds view references for a single cart item layout. These include image, labels,
     *                   price, quantity, and the service hierarchy.
     * Parameters      : View itemView – Inflated layout passed from onCreateViewHolder.
     *********************************************************************************************************/
    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView label, quantity, service,price;
        ImageView itemImage;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            label = itemView.findViewById(R.id.itemLabel);
            quantity = itemView.findViewById(R.id.itemQuantity);
            price = itemView.findViewById(R.id.itemPrice);
            service = itemView.findViewById(R.id.serviceHierarchy);
        }
    }
}
