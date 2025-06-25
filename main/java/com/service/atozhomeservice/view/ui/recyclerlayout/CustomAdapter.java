package com.service.atozhomeservice.view.ui.recyclerlayout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.service.atozhomeservice.databinding.ImageTextElementBinding;
/*********************************************************************************************************
 * Author         : Gunnampalli Parameswara Reddy
 * Class Name     : CustomAdapter
 * Description    : Adapter class for populating a GridView with service category items, each consisting
 *                  of an image and label. Manages user interaction with individual items and notifies
 *                  the listener of click events.
 *********************************************************************************************************/
public class CustomAdapter extends BaseAdapter {
    private final Context context;
    private final Fragment fragment;
    private final int[] images;
    private final String[] labels;
    private final OnServiceCategoryClickListener listener;

    /*********************************************************************************************************
     * Constructor     : CustomAdapter
     * Description     : Initializes the adapter with context, fragment reference, data arrays for images
     *                   and labels, and a listener for handling service category clicks.
     * Parameters      : Context context – The context from the host activity or fragment.
     *                   Fragment fragment – Reference to the hosting fragment for navigation or context use.
     *                   int[] images – Drawable resource IDs representing each service icon.
     *                   String[] labels – Text labels corresponding to each service.
     *                   OnServiceCategoryClickListener listener – Callback interface for click events.
     * Return          : None
     *********************************************************************************************************/
    public CustomAdapter(Context context, Fragment fragment, int[] images, String[] labels, OnServiceCategoryClickListener listener) {
        this.context = context;
        this.fragment = fragment;
        this.images = images;
        this.labels = labels;
        this.listener = listener;
    }

    /*********************************************************************************************************
     * Function Name   : getCount
     * Description     : Returns the total number of items managed by the adapter.
     * Called By       : GridView to determine how many items to render.
     * Parameters      : None
     * Return          : int – Number of items in the adapter (based on image array size).
     *********************************************************************************************************/
    @Override
    public int getCount() {
        return images.length;
    }

    /*********************************************************************************************************
     * Function Name   : getItem
     * Description     : Retrieves the label at the specified index.
     * Called By       : AdapterView (optional use when accessing items by index).
     * Parameters      : int i – Position of the item in the dataset.
     * Return          : Object – The label string at the given position.
     *********************************************************************************************************/
    @Override
    public Object getItem(int i) {
        return labels[i];
    }

    /*********************************************************************************************************
     * Function Name   : getItemId
     * Description     : Returns the ID of the item at a given position. Here, the position itself is used
     *                   as the ID.
     * Called By       : AdapterView for item identification.
     * Parameters      : int i – Index of the item.
     * Return          : long – The ID for the item (same as the index).
     *********************************************************************************************************/
    @Override
    public long getItemId(int i) {
        return i;
    }

    /*********************************************************************************************************
     * Function Name   : getView
     * Description     : Inflates and returns the view for each GridView item. Binds image and text resources
     *                   to UI elements and handles click events on the thumbnail button, triggering the
     *                   associated listener callback.
     * Called By       : GridView when rendering or recycling views.
     * Parameters      : int position – Position of the item in the data set.
     *                   View convertView – Previously returned view to be reused.
     *                   ViewGroup parent – Parent view that this view will be attached to.
     * Return          : View – The fully configured GridView item view.
     *********************************************************************************************************/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageTextElementBinding binding;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            binding = ImageTextElementBinding.inflate(inflater, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ImageTextElementBinding) convertView.getTag();
        }

        binding.itemImage.setImageResource(images[position]);
        binding.itemText.setText(labels[position]);

        binding.thumbnailButton.setOnClickListener(v -> {
            Toast.makeText(context, "Clicked: " + labels[position], Toast.LENGTH_SHORT).show();
            if (listener != null) {
                listener.onServiceClicked(labels[position]);
            }
        });

        return convertView;
    }
}
