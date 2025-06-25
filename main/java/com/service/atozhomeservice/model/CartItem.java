package com.service.atozhomeservice.model;

/*********************************************************************************************************
 * Author         : Gunnampalli Parameswara Reddy
 * Class Name     : CartItem
 * Description    : Model class representing a single item in the user's service cart. Encapsulates all
 *                  necessary properties including service category, icon, type, quantity, and unit price
 *                  for proper cart representation, UI display, and backend operations.
 * Usage Scope    : Used across cart management, order review, and placement processes. Supports data
 *                  binding, serialization, and UI rendering via adapters.
 *********************************************************************************************************/

public class CartItem {

    private String mainService;
    private String subService;
    private  int icon;
    private String itemType;
    private int quantity;

    private double itemPrice;

    public CartItem() {
    }

    public CartItem(String mainService, String subService,int icon, String itemType, int quantity,double itemPrice) {
        this.mainService = mainService;
        this.subService = subService;
        this.icon = icon;
        this.itemType = itemType;
        this.quantity = quantity;
        this.itemPrice = itemPrice;
    }

    public String getMainService() {
        return mainService;
    }

    public void setMainService(String mainService) {
        this.mainService = mainService;
    }

    public String getSubService() {
        return subService;
    }

    public void setSubService(String subService) {
        this.subService = subService;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }
}
