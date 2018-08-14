package com.futureworkshops.android.autocapture.presentation.batch.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.futureworkshops.android.autocapture.R;
import com.futureworkshops.datacap.common.ScaleTransformation;
import com.futureworkshops.datacap.common.model.Page;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by stelian on 24/10/2017.
 */

public class PageRecyclerAdapter extends RecyclerView.Adapter<PageRecyclerAdapter.PageViewHolder> {

    public interface OnBatchItemClickListener {
        void onPageClick(String pageId);
    }

    private Context mContext;
    private List<Page> mItems;
    private OnBatchItemClickListener mOnBatchItemClickListener;
    private float mThumbnailSize;

    public PageRecyclerAdapter(@NonNull Context context) {
        mContext = context;
        mItems = new ArrayList<>();

        mThumbnailSize = mContext.getResources().getDimension(R.dimen.page_thumbnail_size);
    }

    /**
     * Set all the adapter items.
     *
     * @param pages
     */
    public void setItems(List<Page> pages) {
        mItems.clear();
        mItems.addAll(pages);
        notifyDataSetChanged();
    }

    public void setOnBatchItemClickListener(OnBatchItemClickListener onBatchItemClickListener) {
        mOnBatchItemClickListener = onBatchItemClickListener;
    }

    /**
     * Update an existing item.
     *
     * @param pageId
     */
    public void updateItemImage(@NonNull String pageId) {
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).getId().equals(pageId)) {

                final Page page = mItems.get(i);

                // invalidate Picasso cache for the image
                Picasso.with(mContext).invalidate(page.getImagePath());

                // reload the image
                notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    public PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_batch_page_item, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PageViewHolder holder, int position) {
        final Page page = mItems.get(position);

        String label = removeUnderscore(page.getLabel());
        holder.pageName.setText(label);
        holder.pageType.setText(label);

        if (page.getImagePath() != null) {
            File file = new File(page.getImagePath());
            if (file.exists()) {
                Picasso.with(mContext)
                        .load(file)
                        .transform(ScaleTransformation.getScaleHeightTransformation(mThumbnailSize))
                        .placeholder(R.drawable.picasso_placeholder)
                        .into(holder.pageImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnBatchItemClickListener != null) {
                            mOnBatchItemClickListener.onPageClick(page.getId());
                        }
                    }
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private String removeUnderscore(@NonNull String type) {
        return type.replace("_", " ");
    }

    public static class PageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.page_thumbnail)
        ImageView pageImage;

        @BindView(R.id.page_name)
        TextView pageName;

        @BindView(R.id.page_type)
        TextView pageType;

        public PageViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
