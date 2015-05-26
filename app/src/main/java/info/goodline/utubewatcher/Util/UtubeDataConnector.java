package info.goodline.utubewatcher.Util;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.pedrovgs.DraggablePanel;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import info.goodline.utubewatcher.Model.VideoItem;
import info.goodline.utubewatcher.R;


public class UtubeDataConnector {
    private static final String NEXT_PAGE_STATE = "UtubeDataConnector.NEXT_PAGE_STATE";
    private static final String PREV_PAGE_STATE =  "UtubeDataConnector.PREV_PAGE_STATE";
    private final Context mContext;
    private YouTube youtube;
    private YouTube.Search.List query;
    private YouTube.Videos.List queryVideo;
    private String mDefaultQuery="PLgMaGEI-ZiiZ0ZvUtduoDRVXcU5ELjPcI";

    public String mNextPageToken;
    private String mPrevPageToken;
    public long mResultSetSize=10;
    public static final String LOCALE_RU="RU";
    public static final String DEBUG_TAG = "UtubeDataConnector";
    private String mKeywords;


    public UtubeDataConnector(Context context) {
        mContext=context;
        youtube = new YouTube.Builder(new NetHttpTransport(),
                new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(com.google.api.client.http.HttpRequest request) throws IOException {

            }
        }).setApplicationName(context.getString(R.string.app_name)).build();

    }



    public void setResultSetSize(long mtResultSetSize) {
        this.mResultSetSize = mtResultSetSize;
    }


    public ArrayList<VideoItem> search(String keywords){

        try{
            query = youtube.search().list("id,snippet");
            query.setKey(DeveloperKey.DEVELOPER_KEY);
            query.setRegionCode(LOCALE_RU);
            query.setType("video");
            query.setMaxResults(mResultSetSize);
            query.setFields("items(id/videoId,snippet/publishedAt,snippet/title,snippet/description,snippet/thumbnails/default/url)");
            mKeywords=keywords;
            query.setQ(mKeywords);
            if(mNextPageToken != null){
                query.setPageToken(mNextPageToken);
            }
            SearchListResponse response = query.execute();

            mNextPageToken = response.getNextPageToken();
            mPrevPageToken = response.getPrevPageToken();

            List<SearchResult> results = response.getItems();
            ArrayList<VideoItem> items = new ArrayList<VideoItem>();

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
            return new ArrayList<>();
        }
    }
    public VideoItem getDesc(String Id) {
        try{
            queryVideo =  youtube.videos().list("id,snippet,contentDetails,statistics");
            queryVideo.setKey(DeveloperKey.DEVELOPER_KEY);
            queryVideo.setMaxResults(mResultSetSize);

            queryVideo.setId(Id);
            queryVideo.setRegionCode(LOCALE_RU);
            VideoListResponse response = queryVideo.execute();
            List<Video> results = response.getItems();
            VideoItem item = new VideoItem();

            for(Video result:results){
                item.setTitle(result.getSnippet().getTitle());
                item.setDate(result.getSnippet().getPublishedAt().getValue());
                item.setDescription(result.getSnippet().getDescription());
                item.setDuration(timeHumanReadable(result.getContentDetails().getDuration()));
                item.setViewCounts(result.getStatistics().getViewCount());
                item.setThumbnailURL(result.getSnippet().getThumbnails().getDefault().getUrl());
                item.setId(result.getId());
            }
            return item;
        }catch(IOException e){
            Log.d(DEBUG_TAG, "Could not search: "+e);
            return new VideoItem();
        }
    }

    public ArrayList<VideoItem> showLastVideo() {
        try{
            YouTube.PlaylistItems.List queryItemsList = youtube.playlistItems().list("id,snippet");
            queryItemsList.setKey(DeveloperKey.DEVELOPER_KEY);
            queryItemsList.setMaxResults(mResultSetSize);

            queryItemsList.setPlaylistId(mDefaultQuery);

            if(mNextPageToken != null){
                queryItemsList.setPageToken(mNextPageToken);
            }
            PlaylistItemListResponse response = queryItemsList.execute();
            List<PlaylistItem> results = response.getItems();
            mNextPageToken= response.getNextPageToken();
            mPrevPageToken= response.getPrevPageToken();


            ArrayList<VideoItem> items = new ArrayList<VideoItem>();

            for(PlaylistItem result:results){
                VideoItem item = new VideoItem();
                item.setTitle(result.getSnippet().getTitle());
                item.setDate(result.getSnippet().getPublishedAt().getValue());
                item.setDescription(result.getSnippet().getDescription());
                item.setThumbnailURL(result.getSnippet().getThumbnails().getDefault().getUrl());
                item.setId(result.getSnippet().getResourceId().getVideoId());
                items.add(item);
            }
            return items;
        }catch(IOException e){
            Log.d(DEBUG_TAG, "Could not search: "+e);
            return new ArrayList<>();
        }
    }
    private String timeHumanReadable(String youtubeTimeFormat){
    // Gets a PThhHmmMssS time and returns a hh:mm:ss time

        String
                temp = "",
                hour = "",
                minute = "",
                second = "",
                returnString;

        // Starts in position 2 to ignore P and T characters
        for (int i = 2; i < youtubeTimeFormat.length(); ++ i)
        {
            // Put current char in c
            char c = youtubeTimeFormat.charAt(i);

            // Put number in temp
            if (c >= '0' && c <= '9')
                temp = temp + c;
            else
            {
                // Test char after number
                switch (c)
                {
                    case 'H' : // Deal with hours
                        // Puts a zero in the left if only one digit is found
                        if (temp.length() == 1) temp = "0" + temp;

                        // This is hours
                        hour = temp;

                        break;

                    case 'M' : // Deal with minutes
                        // Puts a zero in the left if only one digit is found
                        if (temp.length() == 1) temp = "0" + temp;

                        // This is minutes
                        minute = temp;

                        break;

                    case  'S': // Deal with seconds
                        // Puts a zero in the left if only one digit is found
                        if (temp.length() == 1) temp = "0" + temp;

                        // This is seconds
                        second = temp;

                        break;

                } // switch (coffee)

                // Restarts temp for the eventual next number
                temp = "";

            } // else

        } // for

        if (hour == "" && minute == "") // Only seconds
            returnString = second + " c.";
        else {
            if (hour == "") // Minutes and seconds
                returnString = minute + ":" + second;
            else // Hours, minutes and seconds
                returnString = hour + ":" + minute + ":" + second;
        }

        // Returns a string in hh:mm:ss format
        return returnString;
    }

    public ArrayList<VideoItem> getNextPage() {

        ArrayList<VideoItem> nextPageResult = new ArrayList<>();

       if(mNextPageToken == null && mPrevPageToken != null){
       }else{
           if(mKeywords != null){
               nextPageResult = search(mKeywords);
           }else{
               nextPageResult = showLastVideo();
           }
       }
        return nextPageResult;
    }


    public static void saveNextAndPrevPagesState(Bundle outState, UtubeDataConnector con ) {

        outState.putString(NEXT_PAGE_STATE, con.getmNextPageToken());
        outState.putString(PREV_PAGE_STATE,  con.getmPrevPageToken());
    }
    public static UtubeDataConnector recoverNextAndPrevPagesState(Bundle savedInstanceState, Context context) {

        UtubeDataConnector utubeDataConnector = new UtubeDataConnector(context);

        utubeDataConnector.setmNextPageToken(savedInstanceState.getString(NEXT_PAGE_STATE));

        utubeDataConnector.setmPrevPageToken(savedInstanceState.getString(PREV_PAGE_STATE));

            return utubeDataConnector;

    }
    public String getmPrevPageToken() {
        return mPrevPageToken;
    }

    public void setmPrevPageToken(String mPrevPageToken) {
        this.mPrevPageToken = mPrevPageToken;
    }

    public String getmNextPageToken() {
        return mNextPageToken;
    }

    public void setmNextPageToken(String mNextPageToken) {
        this.mNextPageToken = mNextPageToken;
    }
    public long getResultSetSize() {
        return mResultSetSize;
    }
}
