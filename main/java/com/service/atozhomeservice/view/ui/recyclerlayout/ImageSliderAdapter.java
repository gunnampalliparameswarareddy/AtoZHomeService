package com.service.atozhomeservice.view.ui.recyclerlayout;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/*********************************************************************************************************
 * Author         : Gunnampalli Parameswara Reddy
 * Class Name     : ImageSliderAdapter
 * Description    : Adapter for displaying a list of image resources in a horizontally scrollable
 *                  RecyclerView-based image slider. Each item in the slider is a full-width image
 *                  with center cropping applied.
 *********************************************************************************************************/
public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ViewHolder> {

    private List<Integer> imageList; // Drawable resource IDs

    /*********************************************************************************************************
     * Constructor     : ImageSliderAdapter
     * Description     : Initializes the adapter with a list of drawable resource IDs representing images to
     *                   be displayed in the slider.
     * Parameters      : List<Integer> images – List of drawable resource IDs to populate the slider.
     * Return          : None
     *********************************************************************************************************/
    public ImageSliderAdapter(List<Integer> images) {
        this.imageList = images;
    }

    /*********************************************************************************************************
     * Function Name   : onCreateViewHolder
     * Description     : Creates a new ViewHolder by instantiating a full-width ImageView with
     *                   CENTER_CROP scale type.
     * Called By       : RecyclerView when a new view is required.
     * Parameters      : ViewGroup parent – The parent ViewGroup into which the new view will be added.
     *                   int viewType – The view type of the new view (not used here).
     * Return          : ViewHolder – An instance containing the initialized ImageView.
     *********************************************************************************************************/

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        return new ViewHolder(imageView);
    }

    /*********************************************************************************************************
     * Function Name   : onBindViewHolder
     * Description     : Binds an image resource to the ImageView at the specified position.
     * Called By       : RecyclerView to display the data at the specified position.
     * Parameters      : ViewHolder holder – The ViewHolder containing the ImageView to bind data to.
     *                   int position – The position of the item within the adapter's data set.
     * Return          : void
     *********************************************************************************************************/
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imageView.setImageResource(imageList.get(position));
    }

    /*********************************************************************************************************
     * Function Name   : getItemCount
     * Description     : Returns the total number of items (images) managed by the adapter.
     * Called By       : RecyclerView to determine the number of items to render.
     * Parameters      : None
     * Return          : int – Number of images in the slider.
     *********************************************************************************************************/
    @Override
    public int getItemCount() {
        return imageList.size();
    }

    /*********************************************************************************************************
     * Inner Class     : ViewHolder
     * Description     : ViewHolder that holds a single ImageView representing one item in the image slider.
     * Parameters      : View itemView – The ImageView that will represent the item.
     *********************************************************************************************************/
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }
}
