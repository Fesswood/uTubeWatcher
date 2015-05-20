package info.goodline.utubewatcher.VideoList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggablePanel;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import info.goodline.utubewatcher.Animation.DropDownAnim;
import info.goodline.utubewatcher.Model.VideoItem;
import info.goodline.utubewatcher.PlayerActivityFragment;
import info.goodline.utubewatcher.PlayerYouTubeFragment;
import info.goodline.utubewatcher.R;
import info.goodline.utubewatcher.Util.DeveloperKey;
import info.goodline.utubewatcher.Util.UtubeDataConnector;



public class VideoListActivityFragment extends Fragment {

    private MaterialEditText mSearchInput;
    private ListView mVideosListView;
    private ProgressBar mEmptyProgressBar;
    private VideoListAdapter mVideoListAdapter;
    private LinearLayout mLinearLayout;
    private DraggablePanel mDraggablePanel;
    private String mQueryString;
    private Handler mHandler;
    private ArrayList<VideoItem> mSearchResults;


    private YouTubePlayerSupportFragment mYoutubeFragment;
    private YouTubePlayer mYoutubePlayer;


    public static final String VIDEO_ID_TAG="VideoListActivityFragment.videoID";
    public static final String VIDEO_QUERY_TAG="mQueryString";
    public static final String VIDEO_LIST_SAVE_STATE="videoList";
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    private boolean isNeedShowSearchLayout=true;
    private boolean mIsFirstCall=true;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mLinearLayout     = (LinearLayout) getView().findViewById(R.id.search_layout);
        mSearchInput      = (MaterialEditText) getView().findViewById(R.id.search_input);
        mVideosListView   = (ListView) getView().findViewById(R.id.videos_found);
        mEmptyProgressBar = (ProgressBar) getView().findViewById(R.id.empty_progressbar);
        mDraggablePanel   = (DraggablePanel) getView().findViewById(R.id.draggable_panel);

        mVideosListView.setEmptyView(mEmptyProgressBar);
        mEmptyProgressBar.setVisibility(View.VISIBLE);

        mLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(mLinearLayout.getWidth(), 0));


        mVideoListAdapter=new VideoListAdapter(getActivity());
        mHandler = new Handler();
        setHasOptionsMenu(true);



         mSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
             @Override
             public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                 final boolean isEnterEvent = event != null
                         && event.getKeyCode() == KeyEvent.KEYCODE_ENTER;
                 final boolean isEnterUpEvent = isEnterEvent && event.getAction() == KeyEvent.ACTION_UP;
                 final boolean isEnterDownEvent = isEnterEvent && event.getAction() == KeyEvent.ACTION_DOWN;

                 if (actionId == EditorInfo.IME_ACTION_DONE || isEnterUpEvent) {
                     // Do your action here
                     handleOnEditFinish(v);
                     return true;
                 } else if (isEnterDownEvent) {
                     handleOnEditFinish(v);
                     // Capture this event to receive ACTION_UP
                     return true;
                 } else {
                     // We do not care on other actions
                     return false;
                 }
             }
         });
        addClickListener();


        if (savedInstanceState != null) {
            mSearchResults =(ArrayList) savedInstanceState.getSerializable(VIDEO_LIST_SAVE_STATE);
            if (mSearchResults != null && mSearchResults.size()>0) {
                mVideoListAdapter.addNewslist(mSearchResults);
                mVideosListView.setAdapter(mVideoListAdapter);
            }else{
                searchOnYoutube(mQueryString);
            }
        }else{
            searchOnYoutube(mQueryString);
        }

    }

    private void handleOnEditFinish(TextView v) {
        searchOnYoutube(v.getText().toString());
        mVideoListAdapter.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_list, container, false);
    }

    private void searchOnYoutube(@Nullable final String keywords){
        final FragmentActivity activity = getActivity();
        new Thread(){
            public void run(){
                UtubeDataConnector tubeCon = new UtubeDataConnector(activity);
                if (keywords==null){
                    mSearchResults = tubeCon.showLastVideo();
                }else{
                    mSearchResults = tubeCon.search(keywords);
                }
                mHandler.post(new Runnable() {
                    public void run() {
                        updateVideosFound();
                    }
                });
            }
        }.start();
    }



    private void updateVideosFound(){
        mVideoListAdapter.addNewslist((ArrayList)mSearchResults);
        mVideosListView.setAdapter(mVideoListAdapter);
        if(mIsFirstCall){
            handleSearchLayoutAnimation();
        }

    }
    private void addClickListener(){
       final Context cnxt = getActivity().getApplicationContext();
        mVideosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {

                VideoItem videoItem = mSearchResults.get(pos);
                initializeYoutubeFragment(videoItem.getId());
                initializeDraggablePanel(videoItem);
                hookDraggablePanelListeners();
            }

        });
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(VIDEO_QUERY_TAG, mQueryString);
        ArrayList<VideoItem> videoList = mVideoListAdapter.getVideoList();
        outState.putSerializable(VIDEO_LIST_SAVE_STATE, videoList);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_video_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_to_top) {
            mVideosListView.smoothScrollToPosition(0);
        }
        if (id == R.id.action_to_bottom) {
            mVideosListView.smoothScrollToPosition(mVideoListAdapter.getCount() - 1);
        }
        if (id == R.id.action_search) {
            handleSearchLayoutAnimation();
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleSearchLayoutAnimation() {
        int TargetHeight = 0;
        if(isNeedShowSearchLayout){
            TargetHeight=130;
           mLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
        }
        DropDownAnim dropDownAnim = new DropDownAnim(mLinearLayout, TargetHeight, isNeedShowSearchLayout);
        dropDownAnim.setDuration(500L);
        dropDownAnim.setFillAfter(true);
        dropDownAnim.setFillEnabled(true);
        mLinearLayout.setAnimation(dropDownAnim);
        mLinearLayout.animate().setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mLinearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!isNeedShowSearchLayout) {
                    mLinearLayout.setVisibility(View.GONE);
                    mSearchInput.setActivated(false);
                } else {
                    mSearchInput.setActivated(true);
                }
                isNeedShowSearchLayout =!isNeedShowSearchLayout;
            }
        }).start();

    }
  //Dragable Panel methods
    /**
     * Initialize the YouTubeSupportFrament attached as top fragment to the DraggablePanel widget and
     * reproduce the YouTube video represented with a YouTube url.
     */
    private void initializeYoutubeFragment(final String videoKey) {
        mYoutubeFragment = new YouTubePlayerSupportFragment();
        mYoutubeFragment.initialize(DeveloperKey.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    mYoutubePlayer = player;
                    mYoutubePlayer.loadVideo(videoKey);
                    mYoutubePlayer.setShowFullscreenButton(true);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                YouTubeInitializationResult errorReason) {

                if (errorReason.isUserRecoverableError()) {
                    errorReason.getErrorDialog(getActivity(), RECOVERY_DIALOG_REQUEST).show();
                } else {
                    String errorMessage = String.format(
                            "There was an error initializing the YouTubePlayer (%1$s)",
                            errorReason.toString());
                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Initialize and configure the DraggablePanel widget with two fragments and some attributes.
     */
    private void initializeDraggablePanel(VideoItem videoItem) {
        mDraggablePanel.setFragmentManager(getActivity().getSupportFragmentManager());
        mDraggablePanel.setTopFragment(mYoutubeFragment);
        PlayerActivityFragment movieDescFragment = new PlayerActivityFragment();
        movieDescFragment.setmVideoItem(videoItem);
        mDraggablePanel.setBottomFragment(movieDescFragment);
        mDraggablePanel.initializeView();
        mDraggablePanel.setVisibility(View.VISIBLE);
        mDraggablePanel.maximize();

    }

    /**
     * Hook the DraggableListener to DraggablePanel to pause or resume the video when the
     * DragglabePanel is maximized or closed.
     */
    private void hookDraggablePanelListeners() {
        mDraggablePanel.setDraggableListener(new DraggableListener() {
            @Override
            public void onMaximized() {
                playVideo();
            }

            @Override
            public void onMinimized() {
                //Empty
            }

            @Override
            public void onClosedToLeft() {
                pauseVideo();
            }

            @Override
            public void onClosedToRight() {
                pauseVideo();
            }
        });
    }

    /**
     * Pause the video reproduced in the YouTubePlayer.
     */
    private void pauseVideo() {
        if (mYoutubePlayer.isPlaying()) {
            mYoutubePlayer.pause();
        }
    }

    /**
     * Resume the video reproduced in the YouTubePlayer.
     */
    private void playVideo() {
        if (!mYoutubePlayer.isPlaying()) {
             mYoutubePlayer.play();
        }
    }



}
