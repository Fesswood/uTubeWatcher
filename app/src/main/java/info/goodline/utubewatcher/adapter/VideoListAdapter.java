package info.goodline.utubewatcher.adapter;

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

import info.goodline.utubewatcher.data.VideoItem;
import info.goodline.utubewatcher.R;

/**
 *  Adapter to operate with list of {@link VideoItem} in listView Of {@link info.goodline.utubewatcher.fragment.VideoListFragment}
 *  Used ViewHolder pattern for avoiding slow down performance while scrolling
 *  @author  Sergey Baldin
 */
public class VideoListAdapter extends ArrayAdapter<VideoItem> {

    private ArrayList<VideoItem> mVideoList;
    private final Context mContext;
    private StringBuilder mStringBuilder;
    private SimpleDateFormat mJUD;

    public VideoListAdapter(Context context) {
        super(context, R.layout.video_item);
        mVideoList =new ArrayList<>();
        mContext=context;
        mStringBuilder =new StringBuilder();
        JUDInit();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.video_item, parent, false);
            holder  = getViewHolder(convertView);
        } else{
            holder = (ViewHolder) convertView.getTag();
        }

        setValuesToViewHolder(position, holder);
        return convertView;
    }
    /**
     *  set Values from video item to viewHolder
     * @param position position of current video item
     * @param holder viewHolder of current view
     */
    private void setValuesToViewHolder(int position, ViewHolder holder) {
        VideoItem videoItem = mVideoList.get(position);
        if(!videoItem.getThumbnailURL().isEmpty()){
            Picasso.with(mContext.getApplicationContext()).load(videoItem.getThumbnailURL()).into(holder.thumbnailView);
        }else{
            holder.thumbnailView.setImageResource(R.drawable.watcher);
        }
        holder.titleView.setText(videoItem.getTitle());
        Date date = new Date(videoItem.getDate());
        holder.videoDateView.setText(mJUD.format(date));
        mStringBuilder.setLength(0);
        mStringBuilder.append(videoItem.getDescription());
        if(mStringBuilder.length()>100){
            mStringBuilder.setLength(100);
            mStringBuilder.append("...");
        }
        holder.descriptionView.setText(mStringBuilder.toString());
    }
    /**
     * Get viewHolder by tag or create new if it doesn't exist
     * @param convertView view of news topic
     * @return instance of viewHolder
     */
    private ViewHolder getViewHolder(View convertView) {
        ViewHolder holder;
        holder = new ViewHolder();
        holder.thumbnailView = (ImageView)convertView.findViewById(R.id.video_thumbnail);
        holder.titleView = (TextView) convertView.findViewById(R.id.video_title);
        holder.videoDateView = (TextView) convertView.findViewById(R.id.video_date);
        holder.descriptionView = (TextView) convertView.findViewById(R.id.video_description);
        convertView.setTag(holder);
        return holder;
    }
    /**
     * Initialize SimpleDateFormater for displaying dates
     */
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

    @Override
    public int getCount() {
        return mVideoList.size();
    }

    @Override
    public VideoItem getItem(int position) {
        return mVideoList.get(position);
    }


    @Override
    public void clear() {
        super.clear();
        mVideoList.clear();
    }
    public void addVideoItemList(ArrayList<VideoItem> parsedNewsList) {
        parsedNewsList.removeAll(mVideoList);
        mVideoList.addAll(parsedNewsList);
        notifyDataSetChanged();
    }
    public ArrayList<VideoItem> getVideoList() {
        return mVideoList;
    }
    /**
     * implementation of ViewHolder pattern for VideoListAdapter
     */
    static class ViewHolder {
        public ImageView thumbnailView;
        public TextView titleView;
        public TextView descriptionView;
        public TextView videoDateView;
    }
}