package com.futureworkshops.android.autocapture.presentation.camera.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.futureworkshops.android.autocapture.R;
import com.futureworkshops.android.autocapture.presentation.camera.view.model.PageItem;
import com.futureworkshops.datacap.common.ScaleTransformation;
import com.futureworkshops.datacap.common.model.Page;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by stelian on 25/10/2017.
 */

public class PageImageAdapter extends RecyclerView.Adapter<PageImageAdapter.RecentPictureViewHolder> {

    private Context mContext;
    private List<PageItem> mItems;

    public PageImageAdapter(@NonNull Context context) {
        mContext = context;
        mItems = new ArrayList<>();
    }

    /**
     * Set all the adapter items.
     *
     * @param pages
     */
    public void setItems(List<Page> pages) {
        mItems.clear();
        for (Page page : pages) {
            mItems.add(new PageItem(page));
        }
    }

    /**
     * Add a new item to the existing item list.
     *
     * @param pageItem
     */
    public void addItem(@NonNull PageItem pageItem) {
        mItems.add(pageItem);
        notifyItemInserted(mItems.size() - 1);
    }

    /**
     * Update an existing item.
     *
     * @param page
     */
    public void updateItem(@NonNull Page page) {
        final String pageId = page.getId();

        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).getPageId().equals(pageId)) {
                final PageItem pageItem = mItems.get(i);
                pageItem.setPage(page);
                pageItem.setState(PageItem.LOADED);
                notifyItemChanged(i);
                break;
            }
        }
    }

    /**
     * Remove an item from the list.
     *
     * @param pageId
     */
    public void removeItem(@NonNull String pageId) {
        int position = -1;

        // find item position
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).getPageId().equals(pageId)) {
                position = i;
                break;
            }
        }

        // if item position is valid, remove the item
        if (position > -1) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public RecentPictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_page_image_item, parent, false);
        return new RecentPictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecentPictureViewHolder holder, int position) {
        PageItem item = mItems.get(position);

        if (item.getState() == PageItem.CAPTURING) {
            holder.pageImage.setVisibility(View.INVISIBLE);
            holder.mProgressBar.setVisibility(View.VISIBLE);
        } else {
            final String correctedImagePath = item.getPage().getImagePath();
            if (TextUtils.isEmpty(correctedImagePath)) {
                // show loading icon
                holder.pageImage.setVisibility(View.VISIBLE);
                holder.pageImage.setBackgroundResource(R.drawable.picasso_placeholder);
                holder.mProgressBar.setVisibility(View.INVISIBLE);
            } else {
                holder.mProgressBar.setVisibility(View.INVISIBLE);
                holder.pageImage.setImageDrawable(null);
                holder.pageImage.setVisibility(View.VISIBLE);

                // transform image
                Picasso.with(mContext)
                        .load(new File(correctedImagePath))
                        .transform(ScaleTransformation.getScaleHeightTransformation(mContext.getResources().getDimension(R.dimen.recent_picture_height)))
                        .placeholder(R.drawable.picasso_placeholder)
                        .into(holder.pageImage);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    public static class RecentPictureViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progress)
        ProgressBar mProgressBar;

        @BindView(R.id.page_image)
        ImageView pageImage;

        public RecentPictureViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
