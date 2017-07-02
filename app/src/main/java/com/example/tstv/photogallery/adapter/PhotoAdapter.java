package com.example.tstv.photogallery.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.tstv.photogallery.R;
import com.example.tstv.photogallery.fragments.LongPressDialogFragment;
import com.example.tstv.photogallery.activities.PhotoPageActivity;
import com.example.tstv.photogallery.model.GalleryItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by tstv on 30.06.2017.
 */

public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<GalleryItem> mGalleryItems;
    private Context mContext;
    private static final String DIALOG = "Dialog";


    public PhotoAdapter() {

    }

    public PhotoAdapter(Context context, List<GalleryItem> galleryItems) {
        mGalleryItems = galleryItems;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.list_item_gallery, parent, false);
            Log.e(TAG, "ITEM_VIEW");
            return new PhotoHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            PhotoHolder photoHolder = (PhotoHolder) holder;
            GalleryItem galleryItem = mGalleryItems.get(position);
            photoHolder.bindGalleryItem(galleryItem);
        Log.e(TAG, "onBindViewHolder");
    }


    @Override
    public int getItemCount() {
        return mGalleryItems.size();
    }


    public class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ImageView mItemImageView;
        private GalleryItem mGalleryItem;



        public PhotoHolder(View itemView) {
            super(itemView);

            mItemImageView = (ImageView) itemView.findViewById(R.id.item_image_view);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        private void bindGalleryItem(GalleryItem galleryItem){
            mGalleryItem = galleryItem;
            if (galleryItem != null) {
                Picasso.with(mContext)
                        .load(galleryItem.getUrl())
                        .placeholder(R.drawable.image_placeholder)
                        .into(mItemImageView);
            }
        }

        @Override
        public void onClick(View v) {
            Intent i = PhotoPageActivity.newIntent(mContext, mGalleryItem.getPhotoPageUri());
            mContext.startActivity(i);
    }

        @Override
        public boolean onLongClick(View v) {
            FragmentManager manager = ((AppCompatActivity) mContext).getSupportFragmentManager();
            LongPressDialogFragment dialog = new LongPressDialogFragment().newInstance(mGalleryItem.getUrl());
            dialog.show(manager, DIALOG);
            return true;
        }
    }
}