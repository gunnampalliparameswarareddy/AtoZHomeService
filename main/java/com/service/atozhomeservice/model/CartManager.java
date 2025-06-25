package com.service.atozhomeservice.model;

import java.util.ArrayList;
import java.util.List;

/*********************************************************************************************************
 * Author         : Gunnampalli Parameswara Reddy
 * Class Name     : CartManager
 * Description    : Singleton utility class responsible for managing the user's cart throughout the app
 *                  session. Supports adding new items, aggregating quantities of existing entries, and
 *                  clearing or retrieving the current cart contents. Designed for lightweight, in-memory
 *                  cart state management with persistent access across fragments.
 * Usage Scope    : Centralized cart state controller used by adapters, bottom sheets, and order placement modules.
 *********************************************************************************************************/
public class CartManager {
    private static CartManager instance;
    private final List<CartItem> cartItems = new ArrayList<>();

    private CartManager() {}

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addItem(String mainService, String subService,int icon, String itemType, int quantity,double price) {
        for (CartItem item : cartItems) {
            if (item.getMainService().equals(mainService) &&
                    item.getSubService().equals(subService) && (item.getItemType().equals(itemType)))
            {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        cartItems.add(new CartItem(mainService, subService,icon, itemType, quantity,price));
    }

    public List<CartItem> getItems() {
        return cartItems;
    }

    public void clearCart() {
        cartItems.clear();
    }
}
