package info.goodline.utubewatcher;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.util.ArrayList;

import info.goodline.utubewatcher.Model.VideoItem;
import info.goodline.utubewatcher.Util.UtubeDataConnector;
import info.goodline.utubewatcher.VideoList.VideoListActivityFragment;


public class PlayerActivityFragment extends Fragment {

    public static final String VIDEO_TAG = "VideoSaveState";
    private VideoItem mVideoItem;

    private TextView mVideoTitleBigTextView;
    private TextView mTimeTextView         ;
    private TextView mViewsCountTextView   ;
    private TextView mVideoDescTextView;

    private Handler mDescHandler;

    public PlayerActivityFragment() {
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

    public void setVideoItem(final VideoItem videoItem,final UtubeDataConnector utube) {
        this.mVideoItem = videoItem;

        mVideoTitleBigTextView.setText(mVideoItem.getTitle());
        mVideoDescTextView.setText(mVideoItem.getDescription());

        handleEmptyViewAndDuration(videoItem, utube);

    }

    private void handleEmptyViewAndDuration(final VideoItem videoItem, final UtubeDataConnector utube) {
        if(videoItem.getViewCounts() == null || videoItem.getDuration() == null){
            mTimeTextView.setText("Подгружаем...");
            mViewsCountTextView.setText("Подгружаем...");

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
        View view = inflater.inflate(R.layout.fragment_player, container, false);
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

    public boolean isInitialized() {
        return mVideoTitleBigTextView != null
               && mTimeTextView       != null
               && mViewsCountTextView != null;
    }
}
