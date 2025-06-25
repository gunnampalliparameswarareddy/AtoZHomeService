package com.service.atozhomeservice.view.ui.yourorders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.service.atozhomeservice.model.CartItem;

import java.util.List;

/*********************************************************************************************************
 * Author         : Gunnampalli Parameswara Reddy
 * Class Name     : OrderItemAdapter
 * Description    : RecyclerView adapter for displaying individual items within an order. Binds CartItem
 *                  data to a simple list layout containing service name, quantity, and price per item.
 * Called By      : OrderAdapter for populating nested items within each order.
 * Instance Vars  : Context context – Context used to inflate views.
 *                  List<CartItem> items – List of CartItem objects representing ordered services.
 *********************************************************************************************************/
public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ItemViewHolder> {

    private Context context;
    private List<CartItem> items;

    /*********************************************************************************************************
     * Constructor     : OrderItemAdapter
     * Description     : Initializes the adapter with context and the list of ordered items.
     * Parameters      : Context context – Calling context for layout inflation.
     *                   List<CartItem> items – List of items associated with a particular order.
     * Return          : None
     *********************************************************************************************************/
    public OrderItemAdapter(Context context, List<CartItem> items) {
        this.context = context;
        this.items = items;
    }

    /*********************************************************************************************************
     * Method Name     : onCreateViewHolder
     * Description     : Inflates a built-in Android layout for each item and returns a wrapped ViewHolder.
     * Parameters      : ViewGroup parent – Parent RecyclerView container.
     *                   int viewType – View type of the new view (unused here).
     * Return          : ItemViewHolder – Holds the inflated views for a cart item.
     *********************************************************************************************************/
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ItemViewHolder(view);
    }


    /*********************************************************************************************************
     * Method Name     : onBindViewHolder
     * Description     : Binds a CartItem's label, quantity, and price into the provided ViewHolder. Formats
     *                   it into a human-readable line item.
     * Parameters      : ItemViewHolder holder – ViewHolder containing target views.
     *                   int position – Position of the item in the list.
     * Return          : void
     *********************************************************************************************************/
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.title.setText(item.getItemType()); // or "Fan Installation"
        holder.subtitle.setText("Qty: " + item.getQuantity() + "  ₹" + item.getItemPrice());
    }

    /*********************************************************************************************************
     * Method Name     : getItemCount
     * Description     : Returns the total number of ordered items in the adapter.
     * Parameters      : None
     * Return          : int – Number of cart items in the list.
     *********************************************************************************************************/
    @Override
    public int getItemCount() {
        return items.size();
    }


    /*********************************************************************************************************
     * Inner Class     : ItemViewHolder
     * Description     : ViewHolder class that holds references to TextViews displaying item title and
     *                   subtitle (used for quantity and price).
     * Parameters      : View itemView – Layout view to bind UI elements from.
     *********************************************************************************************************/
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(android.R.id.text1);
            subtitle = itemView.findViewById(android.R.id.text2);
        }
    }
}

