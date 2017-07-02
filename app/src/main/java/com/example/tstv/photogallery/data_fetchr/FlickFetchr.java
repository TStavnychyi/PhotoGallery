package com.example.tstv.photogallery.data_fetchr;

import android.net.Uri;
import android.util.Log;

import com.example.tstv.photogallery.model.GalleryItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tstv on 28.06.2017.
 */

public class FlickFetchr {
    private static int page = 1;
    private static final String TAG = "FlickrFetchr";
    private static final String API_KEY = "a0cc9a600f03533cc740cf5ce19af2b4";
    private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final Uri ENDPOINT = Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("page", String.valueOf(page))
            .appendQueryParameter("extras", "url_s")
            .build();

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0){
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        }finally {
            connection.disconnect();
        }
    }

    public List<GalleryItem> fetchRecentPhotos(int page){
        String url = buildUrl(FETCH_RECENTS_METHOD, null, page);
        return downloadGalleryItems(url);
    }
    public List<GalleryItem> searchPhotos(String query, int page){
        String url = buildUrl(SEARCH_METHOD, query, page);
        return downloadGalleryItems(url);

    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }
    private String buildUrl(String method, String query, int page){
        Uri.Builder uriBuilder = ENDPOINT.buildUpon()
                .appendQueryParameter("page", String.valueOf(page))
                .appendQueryParameter("method", method);
        if (method.equals(SEARCH_METHOD)){
            uriBuilder.appendQueryParameter("text", query);
        }
        return uriBuilder.build().toString();
    }

    public List<GalleryItem> downloadGalleryItems(String url){
        List<GalleryItem> items = new ArrayList<>();
        try {
         //   page = pageNumber;
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
         //   parseItemsGson(items, jsonString);
        }catch (IOException ioe){
            Log.e(TAG, "Failed to fetch items " + ioe);
        }catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON" , je);
        }
        return items;
    }


 /*   private void parseItemsGson(List<GalleryItem> items, String jsonString){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Gallery galleryObj = gson.fromJson(jsonString, Gallery.class);
        Photos photosObj = galleryObj.getPhotos();
        List<Photo> photos = new ArrayList<>();
        photos.addAll(photosObj.getPhoto());

        for (int i = 0; i < photos.size(); i++){
            GalleryItem item = new GalleryItem();

            item.setId(photos.get(i).getId());
            item.setCaption(photos.get(i).getTitle());


        }
        Log.e("TAG" , "Size: " + photos.size());
    }

    */

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws JSONException {
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photosJsonArray = photosJsonObject.getJSONArray("photo");

        for (int i = 0 ; i < photosJsonArray.length(); i++){
            JSONObject photoJsonObject = photosJsonArray.getJSONObject(i);

            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));

            if (!photoJsonObject.has("url_s")){
                continue;
            }

            item.setUrl(photoJsonObject.getString("url_s"));
            item.setOwner(photoJsonObject.getString("owner"));

         //   Log.e("TAG", "URL_S : " + item.getUrl());
            items.add(item);
        }
    }
}
