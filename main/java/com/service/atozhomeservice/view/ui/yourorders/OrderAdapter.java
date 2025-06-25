package com.service.atozhomeservice.view.ui.yourorders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.service.atozhomeservice.R;
import com.service.atozhomeservice.model.Order;

import java.util.List;

/*********************************************************************************************************
 * Author         : Gunnampalli Parameswara Reddy
 * Class Name     : OrderAdapter
 * Description    : RecyclerView adapter responsible for displaying a list of user orders. Each order includes
 *                  status, amount, payment type, timestamp, and a nested list of ordered items displayed
 *                  through OrderItemAdapter.
 * Called By      : OrdersDetails fragment during order history rendering.
 * Instance Vars  : List<Order> orders – List of orders to be rendered.
 *                  Context context     – Context used for inflating views and initializing sub-adapters.
 *********************************************************************************************************/
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orders;
    private Context context;

    /*********************************************************************************************************
     * Constructor     : OrderAdapter
     * Description     : Initializes the adapter with the application context and list of orders.
     * Parameters      : Context context – Application or fragment context.
     *                   List<Order> orders – List of Order objects to be displayed.
     * Return          : None
     *********************************************************************************************************/
    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    /*********************************************************************************************************
     * Method Name     : onCreateViewHolder
     * Description     : Inflates the layout for a single order card and wraps it in an OrderViewHolder.
     * Parameters      : ViewGroup parent – RecyclerView that holds the list items.
     *                   int viewType – View type (unused in this case).
     * Return          : OrderViewHolder – ViewHolder containing inflated views for an order entry.
     *********************************************************************************************************/
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }


    /*********************************************************************************************************
     * Method Name     : onBindViewHolder
     * Description     : Binds an Order object to its corresponding ViewHolder. Populates status, amount,
     *                   payment method, formatted timestamp, and nested RecyclerView of cart items.
     * Parameters      : OrderViewHolder holder – ViewHolder instance to populate.
     *                   int position – Index of the Order in the list.
     * Return          : void
     *********************************************************************************************************/
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.amount.setText("Total: ₹ " + order.getTotalAmount());
        holder.payment.setText("Payment: " + order.getPaymentType());
        holder.status.setText("Status: " + order.getStatus());

        String timeFormatted = new java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a",
                java.util.Locale.getDefault()).format(new java.util.Date(order.getTimestamp()));
        holder.time.setText(timeFormatted);

        // Nested items RecyclerView
        OrderItemAdapter itemAdapter = new OrderItemAdapter(context, order.getItems());
        holder.itemsRecycler.setLayoutManager(new LinearLayoutManager(context));
        holder.itemsRecycler.setAdapter(itemAdapter);
    }

    /*********************************************************************************************************
     * Method Name     : getItemCount
     * Description     : Returns the total number of orders to display.
     * Parameters      : None
     * Return          : int – Number of items in the orders list.
     *********************************************************************************************************/
    @Override
    public int getItemCount() {
        return orders.size();
    }

    /*********************************************************************************************************
     * Inner Class     : OrderViewHolder
     * Description     : ViewHolder that holds views representing a single order summary along with a nested
     *                   RecyclerView to display individual order items.
     * Parameters      : View itemView – Inflated layout passed from onCreateViewHolder.
     *********************************************************************************************************/
    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView amount, payment, status, time;
        RecyclerView itemsRecycler;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            amount = itemView.findViewById(R.id.orderAmount);
            payment = itemView.findViewById(R.id.orderPayment);
            status = itemView.findViewById(R.id.orderStatus);
            time = itemView.findViewById(R.id.orderTime);
            itemsRecycler = itemView.findViewById(R.id.itemsRecycler);
        }
    }
}

