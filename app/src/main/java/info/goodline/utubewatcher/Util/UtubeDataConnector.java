package info.goodline.utubewatcher.Util;

import android.content.Context;
import android.util.Log;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import org.apache.http.HttpRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import info.goodline.utubewatcher.Model.VideoItem;
import info.goodline.utubewatcher.R;

/**
 * Created by ������ ������ on 18.05.2015.
 */
public class UtubeDataConnector {
    private YouTube youtube;
    private YouTube.Search.List query;

    public static final String DEBUG_TAG = "UtubeDataConnector";

    public UtubeDataConnector(Context context) {
        youtube = new YouTube.Builder(new NetHttpTransport(),
                new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(com.google.api.client.http.HttpRequest request) throws IOException {

            }
        }).setApplicationName(context.getString(R.string.app_name)).build();

        try{
            query = youtube.search().list("id,snippet");
            query.setKey(DeveloperKey.DEVELOPER_KEY);
            query.setType("video");
            query.setFields("items(id/videoId,snippet/publishedAt,snippet/title,snippet/description,snippet/thumbnails/default/url)");
        }catch(IOException e){
            Log.d(DEBUG_TAG, "Could not initialize: " + e);
        }
    }

    public List<VideoItem> search(String keywords){
        query.setQ(keywords);
        try{
            SearchListResponse response = query.execute();
            List<SearchResult> results = response.getItems();

            List<VideoItem> items = new ArrayList<VideoItem>();
            for(SearchResult result:results){
                VideoItem item = new VideoItem();
                item.setTitle(result.getSnippet().getTitle());
                item.setDate(result.getSnippet().getPublishedAt().getValue());
                item.setDescription(result.getSnippet().getDescription());
                item.setThumbnailURL(result.getSnippet().getThumbnails().getDefault().getUrl());
                item.setId(result.getId().getVideoId());
                items.add(item);
            }
            return items;
        }catch(IOException e){
            Log.d(DEBUG_TAG, "Could not search: "+e);
            return null;
        }
    }



}
