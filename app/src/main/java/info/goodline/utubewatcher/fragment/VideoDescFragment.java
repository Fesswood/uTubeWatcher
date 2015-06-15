package info.goodline.utubewatcher.fragment;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.goodline.utubewatcher.R;
import info.goodline.utubewatcher.data.VideoItem;
import info.goodline.utubewatcher.connector.YoutubeDataConnector;

/**
 *  Fragments shows video description in bottom frame of DraggablePanel
 *  @author  Sergey Baldin
 */
public class VideoDescFragment extends Fragment {

    public static final String VIDEO_TAG = "VideoDescFragment.VideoSaveState";
    /**
     * Item with description for displaying
     */
    private VideoItem mVideoItem;

    private TextView mVideoTitleBigTextView;
    private TextView mTimeTextView         ;
    private TextView mViewsCountTextView   ;
    private TextView mVideoDescTextView;

    private Handler mDescHandler;

    public VideoDescFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDescHandler=new Handler();
        if (savedInstanceState != null) {
            mVideoItem =(VideoItem) savedInstanceState.getSerializable(VIDEO_TAG);
            if (mVideoItem == null ) {
                mVideoItem=new VideoItem();
            }
        }else{
            mVideoItem=new VideoItem();
        }

    }

    public VideoItem getmVideoItem() {
        return mVideoItem;
    }

    public void setVideoItem(final VideoItem videoItem,final YoutubeDataConnector utube) {
        this.mVideoItem = videoItem;

        mVideoTitleBigTextView.setText(mVideoItem.getTitle());
        mVideoDescTextView.setText(mVideoItem.getDescription());

        handleEmptyViewAndDuration(videoItem, utube);

    }

    /**
     * Handle items with empty information of duration and counts view
     * @param videoItem item for specified empty fields
     * @param utube instance of current YoutubeDataConnector
     */
    private void handleEmptyViewAndDuration(final VideoItem videoItem, final YoutubeDataConnector utube) {
        if(videoItem.getViewCounts() == null || videoItem.getDuration() == null){
            mTimeTextView.setText("...");
            mViewsCountTextView.setText("...");

            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            final VideoItem itemWithDesc = utube.getDesc(videoItem.getId());
                            mDescHandler.post(new Runnable() {
                                @Override
                                public void run() {

                                    mTimeTextView.setText(itemWithDesc.getDuration());
                                    mViewsCountTextView.setText(""+itemWithDesc.getViewCounts());

                                }
                            });
                        }
                    }
            ).start();

        }else{

            mTimeTextView.setText(mVideoItem.getDuration());
            mViewsCountTextView.setText(""+mVideoItem.getViewCounts());

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_desc, container, false);
        mVideoTitleBigTextView =(TextView) view.findViewById(R.id.videoTitleBig);
        mTimeTextView          =(TextView) view.findViewById(R.id.Time);
        mViewsCountTextView    =(TextView) view.findViewById(R.id.viewsCount);
        mVideoDescTextView    =(TextView) view.findViewById(R.id.videoDesc);


        return view;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }
    /**
     * Check That all text view was initialize correctly
     */
    public boolean isInitialized() {
        return mVideoTitleBigTextView != null
               && mTimeTextView       != null
               && mViewsCountTextView != null;
    }
}
