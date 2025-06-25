package com.service.atozhomeservice.view.ui.recyclerlayout;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;

import com.service.atozhomeservice.databinding.ImageTextElementBinding;
import com.service.atozhomeservice.databinding.QuantypickerBinding;
import com.service.atozhomeservice.model.CartItem;
import com.service.atozhomeservice.model.CartManager;

import java.util.ArrayList;
import java.util.List;

/*********************************************************************************************************
 * Author         : Gunnampalli Parameswara Reddy
 * Class Name     : QuantityAdapter
 * Description    : Adapter for displaying a list of service items with their respective icons, prices,
 *                  and quantity controls. Handles user interaction to increase or decrease quantity and
 *                  updates the CartManager accordingly. Reflects current cart state for consistency.
 *********************************************************************************************************/
public class QuantityAdapter extends BaseAdapter {
    private Context context;
    private Fragment fragment;
    private String mainService;
    private String subService;
    private int []item_icon;
    private String [] item_label;
    private int[] quantity;

    private double[] item_price;

    private OnCartUpdatedListener cartUpdatedListener;

    /*********************************************************************************************************
     * Constructor     : QuantityAdapter
     * Description     : Initializes the adapter with item details and cart update listener. Also restores
     *                   item quantities from the CartManager to ensure previously added items are reflected.
     * Parameters      : Context context – Context from the hosting environment.
     *                   Fragment fragment – The fragment instantiating this adapter.
     *                   String mainService – Main category of the service.
     *                   String subService – Subcategory of the service.
     *                   int[] item_icon – Drawable resource IDs for service icons.
     *                   String[] item_label – Names of the service items.
     *                   double[] item_price – Prices for each service item.
     *                   OnCartUpdatedListener listener – Callback to notify when the cart is modified.
     * Return          : None
     *********************************************************************************************************/
    public QuantityAdapter(Context context, Fragment fragment,String mainService,String subService, int []item_icon, String []item_label,double[] item_price,OnCartUpdatedListener listener)
    {
        this.context = context;
        this.fragment = fragment;
        this.mainService = mainService;
        this.subService = subService;
        this.item_icon = item_icon;
        this.item_label =item_label;
        this.quantity = new int[item_label.length];
        this.item_price = item_price;
        this.cartUpdatedListener = listener;
        restoreQuantitiesFromCart();
    }

    /*********************************************************************************************************
     * Function Name   : getCount
     * Description     : Returns the number of service items available in the adapter.
     * Called By       : ListView or GridView for rendering number of views.
     * Parameters      : None
     * Return          : int – The size of the item_icon array.
     *********************************************************************************************************/
    @Override
    public int getCount() {
        return item_icon.length;
    }

    /*********************************************************************************************************
     * Function Name   : getView
     * Description     : Inflates the layout for each item view, populates it with icon, label, price, and
     *                   quantity, and wires up increment/decrement logic for item quantity along with
     *                   cart update callback.
     * Called By       : AdapterView when displaying the list/grid.
     * Parameters      : int position – Index of the item.
     *                   View convertView – Existing view if reusable.
     *                   ViewGroup parent – Parent container.
     * Return          : View – Configured view for the given position.
     *********************************************************************************************************/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QuantypickerBinding binding;
        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(context);
            binding = QuantypickerBinding.inflate(inflater,parent,false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        }
        else
        {
            binding = (QuantypickerBinding) convertView.getTag();
        }

        binding.itemImage.setImageResource(item_icon[position]);
        binding.itemType.setText(item_label[position]);
        binding.itemPrice.setText(String.format("₹ %.2f", item_price[position]));

        binding.itemQuantity.setText(String.valueOf(quantity[position]));

        binding.btnIncrease.setOnClickListener(v -> {
            quantity[position]++;
            binding.itemQuantity.setText(String.valueOf(quantity[position]));
            updateSelectedItems(position);
        });

        binding.btnDecrease.setOnClickListener(v -> {
            if (quantity[position] > 0) quantity[position]--;
            binding.itemQuantity.setText(String.valueOf(quantity[position]));
            updateSelectedItems(position);
        });

        return convertView;
    }

    /*********************************************************************************************************
     * Function Name   : getItem
     * Description     : Returns the item at the specified index. Not utilized in this implementation.
     * Called By       : AdapterView (if needed).
     * Parameters      : int i – Index of the item.
     * Return          : Object – Currently returns null.
     *********************************************************************************************************/
    @Override public Object getItem(int i) { return null; }
    /*********************************************************************************************************
     * Function Name   : getItemId
     * Description     : Provides a unique ID for each item. Not used in this case.
     * Called By       : AdapterView.
     * Parameters      : int i – Index of the item.
     * Return          : long – Always returns 0.
     *********************************************************************************************************/
    @Override public long getItemId(int i) { return 0; }

    /*********************************************************************************************************
     * Function Name   : updateSelectedItems
     * Description     : Updates the CartManager with the current quantity for the specified item. If the
     *                   quantity is zero, it removes the item from the cart. Notifies the listener after update.
     * Called By       : btnIncrease and btnDecrease click handlers.
     * Parameters      : int position – Index of the item being modified.
     * Return          : void
     *********************************************************************************************************/
    private void updateSelectedItems(int position) {
        CartManager manager = CartManager.getInstance();



        List<CartItem> currentItems = new ArrayList<>(manager.getItems());
        for (CartItem item : currentItems) {
            if (item.getMainService().equals(mainService) &&
                    item.getSubService().equals(subService) &&
                    item.getItemType().equals(item_label[position])) {
                manager.getItems().remove(item);
                break;
            }
        }

        // Add only if quantity > 0
        if (quantity[position] > 0) {
            manager.addItem(mainService, subService,item_icon[position], item_label[position], quantity[position],item_price[position]);
        }

        if (cartUpdatedListener != null) {
            cartUpdatedListener.onCartUpdated();
        }
    }

    /*********************************************************************************************************
     * Function Name   : restoreQuantitiesFromCart
     * Description     : Reads existing cart data and pre-fills quantities for items that have already been
     *                   added previously. Ensures a persistent state across sessions.
     * Called By       : Constructor
     * Parameters      : None
     * Return          : void
     *********************************************************************************************************/
    private void restoreQuantitiesFromCart() {
        List<CartItem> existingItems = CartManager.getInstance().getItems();
        for (int i = 0; i < item_label.length; i++) {
            for (CartItem item : existingItems) {
                if (item.getMainService().equals(mainService)
                        && item.getSubService().equals(subService)
                        && item.getItemType().equals(item_label[i])) {
                    quantity[i] = item.getQuantity();
                    break;
                }
            }
        }
    }
}
