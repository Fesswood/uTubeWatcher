package info.goodline.utubewatcher.Util;

import android.content.Context;
import android.util.Log;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import org.apache.http.HttpRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import info.goodline.utubewatcher.Model.VideoItem;
import info.goodline.utubewatcher.R;


public class UtubeDataConnector {
    private YouTube youtube;
    private YouTube.Search.List query;
    private YouTube.Videos.List queryVideo;
    private String mDefaultQuery="mostPopular";
    private long mtResultSetSize=10;

    public static final String LOCALE_RU="RU";
    public static final String DEBUG_TAG = "UtubeDataConnector";


    public UtubeDataConnector(Context context) {
        youtube = new YouTube.Builder(new NetHttpTransport(),
                new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(com.google.api.client.http.HttpRequest request) throws IOException {

            }
        }).setApplicationName(context.getString(R.string.app_name)).build();

    }

    public ArrayList<VideoItem> search(String keywords){

        try{
            query = youtube.search().list("id,snippet");
            query.setKey(DeveloperKey.DEVELOPER_KEY);
            query.setRegionCode(LOCALE_RU);
            query.setType("video");
            query.setMaxResults(mtResultSetSize);
            query.setFields("items(id/videoId,snippet/publishedAt,snippet/title,snippet/description,snippet/thumbnails/default/url)");
            query.setQ(keywords);
            SearchListResponse response = query.execute();
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


    public ArrayList<VideoItem> showLastVideo() {
        try{
            queryVideo = youtube.videos().list("id,snippet,contentDetails,statistics");
            queryVideo.setKey(DeveloperKey.DEVELOPER_KEY);
            queryVideo.setMaxResults(mtResultSetSize);

            queryVideo.setChart(mDefaultQuery);
            queryVideo.setRegionCode(LOCALE_RU);
            VideoListResponse response = queryVideo.execute();
            List<Video> results = response.getItems();
            ArrayList<VideoItem> items = new ArrayList<VideoItem>();

            for(Video result:results){
                VideoItem item = new VideoItem();
                item.setTitle(result.getSnippet().getTitle());
                item.setDate(result.getSnippet().getPublishedAt().getValue());
                item.setDescription(result.getSnippet().getDescription());
                item.setDuration(timeHumanReadable(result.getContentDetails().getDuration()));
                item.setViewCounts(result.getStatistics().getViewCount());
                item.setThumbnailURL(result.getSnippet().getThumbnails().getDefault().getUrl());
                item.setId(result.getId());
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
}
