package com.service.atozhomeservice.view.ui.recyclerlayout;

/*********************************************************************************************************
 * Author         : Gunnampalli Parameswara Reddy
 * Interface Name : OnServiceCategoryClickListener
 * Description    : Listener interface designed to handle click events on service category items.
 *                  Classes implementing this interface should define behavior based on the selected label.
 *********************************************************************************************************/
public interface OnServiceCategoryClickListener {
    /*********************************************************************************************************
     * Method Name     : onServiceClicked
     * Description     : Callback triggered when a user clicks on a service category item. This method
     *                   enables context-aware navigation or action based on the selected label.
     * Called By       : CustomAdapter (inside thumbnailButton click listener)
     * Parameters      : String label – The text label of the clicked service item.
     * Return          : void
     *********************************************************************************************************/
    void onServiceClicked(String label);
}
