package info.goodline.utubewatcher.VideoList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import info.goodline.utubewatcher.Model.VideoItem;
import info.goodline.utubewatcher.R;

/**
 * Created by Балдин Сергей on 20.05.2015.
 */
public class VideoListAdapter extends ArrayAdapter<VideoItem> {

    private ArrayList<VideoItem> mVideoList;
    private final Context mContext;
    private StringBuffer mStringBuffer;
    private SimpleDateFormat mJUD;

    public VideoListAdapter(Context context) {
        super(context, R.layout.video_item);
        mVideoList =new ArrayList<>();
        mContext=context;
        mStringBuffer=new StringBuffer();
        JUDInit();
    }

    public void addNewslist(ArrayList<VideoItem> parsedNewsList) {
        parsedNewsList.removeAll(mVideoList);
        mVideoList.addAll(parsedNewsList);
        notifyDataSetChanged();
    }
    public ArrayList<VideoItem> getVideoList() {
        return mVideoList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.video_item, parent, false);
        }
        ImageView thumbnail = (ImageView)convertView.findViewById(R.id.video_thumbnail);
        TextView title = (TextView)convertView.findViewById(R.id.video_title);
        TextView description = (TextView)convertView.findViewById(R.id.video_description);
        TextView videoDate = (TextView)convertView.findViewById(R.id.video_date);

        VideoItem searchResult = mVideoList.get(position);
        Picasso.with(mContext.getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
        title.setText(searchResult.getTitle());
        mStringBuffer.setLength(0);
        mStringBuffer.append(searchResult.getDescription());
        if(mStringBuffer.length()>100){
            mStringBuffer.setLength(100);
            mStringBuffer.append("...");
        }
        Date date = new Date(searchResult.getDate());
        videoDate.setText(mJUD.format(date));
        description.setText(mStringBuffer.toString());
        return convertView;
    }
    @Override
    public int getCount() {
        return mVideoList.size();
    }

    // getItem(int) in Adapter returns Object but we can override
    // it to BananaPhone thanks to Java return type covariance
    @Override
    public VideoItem getItem(int position) {
        return mVideoList.get(position);
    }

    // getItemId() is often useless, I think this should be the default
    // implementation in BaseAdapter
    @Override
    public long getItemId(int position) {
        return position;
    }

    public void prependNewsList(ArrayList<VideoItem> newsList) {
        for(VideoItem VideoItem: newsList){
            mVideoList.add(0, VideoItem);
        }
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        super.clear();
        mVideoList.clear();
    }

    private void JUDInit() {
            Locale russian = new Locale("ru");
            String[] newMonths = {
                    "января", "февраля", "марта", "апреля", "мая", "июня",
                    "июля", "августа", "сентября", "октября", "ноября", "декабря"};
            DateFormatSymbols dfs = DateFormatSymbols.getInstance(russian);
            dfs.setMonths(newMonths);
            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, russian);
            SimpleDateFormat sdf = (SimpleDateFormat) df;
            sdf.setDateFormatSymbols(dfs);
            mJUD  =  new SimpleDateFormat("d MMMM yyyy, HH:mm", new Locale("ru"));
    }
}