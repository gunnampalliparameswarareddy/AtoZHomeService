package com.service.atozhomeservice.model;

import java.util.List;

/*********************************************************************************************************
 * Author         : Gunnampalli Parameswara Reddy
 * Class Name     : Order
 * Description    : Data model representing a user’s complete service order. Encapsulates ordered items,
 *                  total amount, timestamp, payment method, service status, and user-selected location.
 *                  Designed for Firestore compatibility and seamless integration with cart, order history,
 *                  and payment workflows.
 * Usage Scope    : Used across order placement, order detail views, adapter population, Firestore operations,
 *                  and ViewModel LiveData binding.
 *********************************************************************************************************/
public class Order {

    private List<CartItem> items;
    private double totalAmount;
    private long timestamp;
    private String status;
    private String paymentType;
    private String location;

    public Order(List<CartItem> items, double totalAmount, long timestamp, String status, String paymentType, String location) {
        this.items = items;
        this.totalAmount = totalAmount;
        this.timestamp = timestamp;
        this.status = status;
        this.paymentType = paymentType;
        this.location = location;
    }

    public Order() {} // Firestore needs this

    // Getters and setters...
    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
