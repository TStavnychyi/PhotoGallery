package com.example.tstv.photogallery.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.tstv.photogallery.notification_job.NotificationJob;
import com.example.tstv.photogallery.adapter.PhotoAdapter;
import com.example.tstv.photogallery.preferences.QueryPreferences;
import com.example.tstv.photogallery.StrictModeClass;
import com.example.tstv.photogallery.data_fetchr.FlickFetchr;
import com.example.tstv.photogallery.R;
import com.example.tstv.photogallery.model.GalleryItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tstv on 28.06.2017.
 */

public class PhotoGalleryFragment extends Fragment {
    private static final String TAG = "PhotoGalleryFragment";
    private RecyclerView mPhotoRecyclerView;
    //    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;
    private String queryLoadInfo;
    PhotoAdapter adapter;
    private int page = 1;
    private ProgressBar mProgressBar;
    private boolean recyclerViewEndless = true;
    private Toolbar mToolbar;
    private ImageButton mUpButton;

    private List<GalleryItem> mItems = new ArrayList<>();

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        Log.e(TAG, "PhotoGalleryFragment onCreate()");
        StrictModeClass.initStrictMode();
     /*  Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
                new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
                    @Override
                    public void onThumbnailDownloaded(PhotoHolder holder, Bitmap thumbnail) {
                        Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                        holder.mItemImageView.setImageDrawable(drawable);
                        Log.e(TAG, "onThumbnailDownloaded");
                    }
                }
        );
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
        */
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //  mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //  mThumbnailDownloader.clearQueue();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        int rowNum = calculateNoOfColumns(getContext());

        ConstraintLayout root = (ConstraintLayout) view.findViewById(R.id.root_constraint);
        //HoverTouchHelper.make(root, );

       // mUpButton = (ImageButton) view.findViewById(R.id.up_button);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mPhotoRecyclerView = (RecyclerView) view.findViewById(R.id.photo_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), rowNum));
        mPhotoRecyclerView.setOnScrollListener(onScrollListener);


        updateItems();
        setupAdapter();
        startNotificationJob();

        return view;
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {
        private String mQuery;


        public FetchItemsTask(String query) {
            this.mQuery = query;
        }

        @Override
        protected void onPreExecute() {
            Log.e(TAG, "onPReExecute");
            showProgressBar();
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            if (mQuery == null) {
                Log.e(TAG, "fetchRecentPhotos");
                return new FlickFetchr().fetchRecentPhotos(page);
            } else {
                Log.e(TAG, "searchPhotos");
                return new FlickFetchr().searchPhotos(mQuery, page);
            }
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            hideProgressBar();
            mItems.addAll(galleryItems);
            if (recyclerViewEndless) {
                setupAdapter();
            }
            recyclerViewEndless = true;
            Log.e(TAG, "onPostExecute");

        }
    }

    private void setupAdapter() {
        adapter = new PhotoAdapter(getContext(), mItems);
            if (isAdded()) {
                Log.e(TAG, "SetupAdapter");
                mPhotoRecyclerView.setAdapter(adapter);
            }

    }

    private int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!mPhotoRecyclerView.canScrollVertically(mPhotoRecyclerView.getLayoutManager().getItemCount())) {
                if (page < 10) {
                    page = page + 1;
                    recyclerViewEndless = false;
                    updateItems();
                    Log.e(TAG, page + " - mPage");

                }
            }
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_photo_gallery, menu);

        final MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "QueryTextSubmit: " + query);
                queryLoadInfo = query;
                QueryPreferences.setStoredQuery(getActivity(), query);
                mItems.clear();
                updateItems();
                searchView.clearFocus();
                Log.e(TAG, "onQueryTextSubmit : " + query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "QueryTextChange: " + newText);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QueryPreferences.getStoredQuery(getActivity());
                MenuItemCompat.expandActionView(searchItem);
                searchView.setQueryHint(query);
            }
        });

        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
        boolean isChecked = QueryPreferences.isAlarmOn(getActivity());
        toggleItem.setChecked(isChecked);
    }

    private void updateItems() {
        String query = QueryPreferences.getStoredQuery(getActivity());
        Log.e(TAG, "UpdateItems : " + query);
        setToolbarTitle(query);
        new FetchItemsTask(query).execute();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                mItems.clear();
                updateItems();
                return true;
            case R.id.menu_item_toggle_polling:
                boolean isChecked = !QueryPreferences.isAlarmOn(getActivity());
                item.setChecked(isChecked);
                if (item.isChecked()){
                    QueryPreferences.setAlarmOn(getActivity(), true);
                }else {
                    QueryPreferences.setAlarmOn(getActivity(), false);
                }
             /*   boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlarm(getActivity(), shouldStartAlarm);
                getActivity().invalidateOptionsMenu();
                */
                return true;
            case R.id.menu_item_refresh:
                mItems.clear();
                String query = QueryPreferences.getStoredQuery(getActivity());
                new FetchItemsTask(query).execute();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void showProgressBar(){
        mProgressBar.setVisibility(View.VISIBLE);
        mPhotoRecyclerView.setVisibility(View.GONE);

    }

    private void hideProgressBar(){
        mProgressBar.setVisibility(View.GONE);
        mPhotoRecyclerView.setVisibility(View.VISIBLE);
    }

    private void startNotificationJob(){
        boolean isCheckable = QueryPreferences.isAlarmOn(getActivity());
        if (isCheckable) {
            NotificationJob.schedulePeriodic();
        }
    }

    private void setToolbarTitle(String param) {
        if (param == null) {
            ((AppCompatActivity) getActivity()).setTitle("Recent");
        } else {
            param = param.substring(0, 1).toUpperCase() + param.substring(1).toLowerCase();
            ((AppCompatActivity) getActivity()).setTitle("Flickr - " + param);
        }

    }
}
