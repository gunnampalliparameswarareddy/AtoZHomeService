package com.service.atozhomeservice.view.ui.recyclerlayout;

/*********************************************************************************************************
 * Author         : Gunnampalli Parameswara Reddy
 * Interface Name : OnCartUpdatedListener
 * Description    : Defines a contract for handling cart update events. Classes implementing this interface
 *                  should define behavior to be executed when the cart is modified (e.g., items added, removed).
 *********************************************************************************************************/

public interface OnCartUpdatedListener {
    /*********************************************************************************************************
     * Method Name     : onCartUpdated
     * Description     : Callback method triggered when the cart is updated. Implement this to refresh UI elements
     *                   like badge counts, totals, or to fetch updated cart data.
     * Called By       : ViewModel, Adapter, or Fragment when cart contents change.
     * Parameters      : None
     * Return          : void
     *********************************************************************************************************/
    void onCartUpdated();
}
