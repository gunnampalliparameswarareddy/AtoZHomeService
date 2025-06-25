package com.service.atozhomeservice.model;

/*********************************************************************************************************
 * Author         : Gunnampalli Parameswara Reddy
 * Class Name     : CustomerData
 * Description    : Model class representing a customer's service booking details. This includes customer
 *                  identification, service and sub-service types, scheduled service time, and full address
 *                  breakdown. Also tracks service status, making it ideal for storage, retrieval, and
 *                  display in both backend and frontend layers of the application.
 * Usage Scope    : Used throughout booking, confirmation, admin tracking, and order summary features.
 *********************************************************************************************************/

public class CustomerData {
    private String customerId;
    private String customerName;
    private String typeOfService;
    private String typeOfSubService;
    private String preferredDateTime;
    private String streetName;
    private String cityName;
    private String stateName;
    private String countryName;
    private String pinCode;

    private String serviceStatus;

    /*Default Constructor*/
    public CustomerData() {
    }

    /*Parameterized Constructor*/
    public CustomerData(String customerId,String customerName,String typeOfService, String typeOfSubService, String preferredDateTime, String streetName, String cityName, String stateName, String countryName, String pinCode,String serviceStatus) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.typeOfService = typeOfService;
        this.typeOfSubService = typeOfSubService;
        this.preferredDateTime = preferredDateTime;
        this.streetName = streetName;
        this.cityName = cityName;
        this.stateName = stateName;
        this.countryName = countryName;
        this.pinCode = pinCode;
        this.serviceStatus=serviceStatus;
    }
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getTypeOfService() {
        return typeOfService;
    }

    public void setTypeOfService(String typeOfService) {
        this.typeOfService = typeOfService;
    }

    public String getTypeOfSubService() {
        return typeOfSubService;
    }

    public void setTypeOfSubService(String typeOfSubService) {
        this.typeOfSubService = typeOfSubService;
    }

    public String getPreferredDateTime() {
        return preferredDateTime;
    }

    public void setPreferredDateTime(String preferredDateTime) {
        this.preferredDateTime = preferredDateTime;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }
}
