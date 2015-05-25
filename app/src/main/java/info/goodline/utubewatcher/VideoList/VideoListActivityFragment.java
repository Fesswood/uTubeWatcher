package info.goodline.utubewatcher.VideoList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

import info.goodline.utubewatcher.Animation.DropDownAnim;
import info.goodline.utubewatcher.Model.VideoItem;
import info.goodline.utubewatcher.PlayerActivityFragment;
import info.goodline.utubewatcher.R;
import info.goodline.utubewatcher.Util.DeveloperKey;
import info.goodline.utubewatcher.Util.DraggableState;
import info.goodline.utubewatcher.Util.UtubeDataConnector;



public class VideoListActivityFragment extends Fragment {



    private MaterialEditText mSearchInput;
    private ListView mVideosListView;
    private ProgressBar mEmptyProgressBar;
    private VideoListAdapter mVideoListAdapter;
    private LinearLayout mLinearLayout;
    private DraggablePanel mDraggablePanel;
    private Handler mHandler;

    private DraggableState mDraggablePanelState;
    private ArrayList<VideoItem> mSearchResults;


    private YouTubePlayerSupportFragment mYoutubeFragment;
    private YouTubePlayer mYoutubePlayer;
    private PlayerActivityFragment mMovieDescFragment;

    private static final String STATE_IS_PLAYING = "isPlayingState";
    private static final String PLAYER_SAVE_STATE ="playerSaveState" ;
    public static final String VIDEO_ID_TAG="VideoListActivityFragment.videoID";
    public static final String VIDEO_QUERY_TAG="mQueryString";
    public static final String VIDEO_LIST_SAVE_STATE="videoListSaveState";
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private static final int SPEECH_REQUEST_CODE = 0;


    private boolean isNeedShowSearchLayout=true;
    private boolean mIsPlayerInitializeSuccess=false;
    private boolean mIsDragablePanelMaximized;
    private UtubeDataConnector mUtubeDataConnector;
    private boolean mIsFirstUpdate =true;


    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mLinearLayout     = (LinearLayout) getView().findViewById(R.id.search_layout);
        mSearchInput      = (MaterialEditText) getView().findViewById(R.id.search_input);
        mVideosListView   = (ListView) getView().findViewById(R.id.videos_found);
        mEmptyProgressBar = (ProgressBar) getView().findViewById(R.id.empty_progressbar);
        mDraggablePanel   = (DraggablePanel) getView().findViewById(R.id.draggable_panel);



        mVideosListView.setEmptyView(mEmptyProgressBar);
        mEmptyProgressBar.setVisibility(View.VISIBLE);
        mDraggablePanel.setVisibility(View.GONE);

        mLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(mLinearLayout.getWidth(), 0));


        mUtubeDataConnector = new UtubeDataConnector(getActivity());
        mVideoListAdapter=new VideoListAdapter(getActivity());
        mVideosListView.setAdapter(mVideoListAdapter);
        mHandler = new Handler();

        setHasOptionsMenu(true);


        searchInputActionListenerHandle();
        handleVideosListItemClickListener();
        initializeYoutubeFragment();
        initializeDraggablePanel();

        if (savedInstanceState != null) {
            mSearchResults =(ArrayList) savedInstanceState.getSerializable(VIDEO_LIST_SAVE_STATE);
            if (mSearchResults != null && mSearchResults.size()>0) {
                mVideoListAdapter.addNewslist(mSearchResults);
                mVideosListView.setAdapter(mVideoListAdapter);
                if(mIsFirstUpdate){
                    initializeInfinityScroll();
                    mIsFirstUpdate =false;
                }
            }else{
                searchOnYoutube(null);
            }
            mUtubeDataConnector=UtubeDataConnector.recoverNextAndPrevPagesState(savedInstanceState,getActivity());

              final VideoItem videoItem = (VideoItem) savedInstanceState.getSerializable(PlayerActivityFragment.VIDEO_TAG);
           mDraggablePanelState= (DraggableState) savedInstanceState.getSerializable(DraggableState.DRAGGABLE_PANEL_STATE);
            if(mDraggablePanelState != null){
                new Thread(){
                    public void run(){
                        while(true){
                            if(mYoutubePlayer != null && mMovieDescFragment.isInitialized()){

                                if( videoItem != null && videoItem.getId() != null
                                        && videoItem.getId() != ""){
                                    mMovieDescFragment.setVideoItem(videoItem,mUtubeDataConnector);
                                    mYoutubePlayer.loadVideo(videoItem.getId());
                                }

                                mHandler.post(new Runnable() {
                                    public void run() {
                                        if(DraggableState.MAXIMIZED ==  mDraggablePanelState ){
                                            mDraggablePanel.setVisibility(View.VISIBLE);
                                            mDraggablePanel.maximize();
                                        }
                                    }
                                });

                                break;
                            }
                            try {
                                Thread.sleep(100L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
            }

        } else {
            searchOnYoutube(null);
        }
    }

    private void initializeInfinityScroll() {
        mVideosListView.setOnScrollListener(new InfinityScrollListener(mUtubeDataConnector.getResultSetSize()) {
            // событие возникает во время того как скроллинг дойдет до конца
            @Override
            public void loadMore(int page, int totalItemsCount) {
                // загрузить еще данных
                new Thread(){
                    public void run(){
                        mSearchResults=mUtubeDataConnector.getNextPage();
                        mHandler.post(new Runnable() {
                            public void run() {
                                if(mSearchResults != null && mSearchResults.size()>0){
                                    mVideoListAdapter.addNewslist((ArrayList) mSearchResults);
                                }else{
                                    Toast.makeText(getActivity(),"Видеоролики закончились",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }.start();
            }
        });
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<VideoItem> videoList = mVideoListAdapter.getVideoList();

        UtubeDataConnector.saveNextAndPrevPagesState(outState,mUtubeDataConnector);
        DraggableState.saveDraggableState(outState, mIsDragablePanelMaximized);
        outState.putSerializable(VIDEO_LIST_SAVE_STATE, videoList);

        outState.putSerializable(mMovieDescFragment.VIDEO_TAG, mMovieDescFragment.getmVideoItem());

        if (mIsPlayerInitializeSuccess ) {
            if(mYoutubePlayer!=null &&  !mYoutubePlayer.isPlaying())
            outState.putBoolean(STATE_IS_PLAYING, true);
        }

    }

    private void searchInputActionListenerHandle() {
        mSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                final boolean isEnterEvent = event != null
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER;
                final boolean isEnterUpEvent = isEnterEvent && event.getAction() == KeyEvent.ACTION_UP;
                final boolean isEnterDownEvent = isEnterEvent && event.getAction() == KeyEvent.ACTION_DOWN;

                if (actionId == EditorInfo.IME_ACTION_DONE || isEnterUpEvent) {
                    // Do your action here
                    searchOnYoutube(v.getText().toString());
                    return true;
                } else if (isEnterDownEvent) {
                    searchOnYoutube(v.getText().toString());
                    // Capture this event to receive ACTION_UP
                    return true;
                } else {
                    // We do not care on other actions
                    return false;
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_list, container, false);
    }

    private void searchOnYoutube(@Nullable final String keywords){
        new Thread(){
            public void run(){

                if (keywords==null){
                    mSearchResults = mUtubeDataConnector.showLastVideo();
                }else{
                    mSearchResults = mUtubeDataConnector.search(keywords);
                }
                mHandler.post(new Runnable() {
                    public void run() {
                        updateVideosFound();
                    }
                });
            }
        }.start();
    }



    private void updateVideosFound() {
        mVideoListAdapter.clear();
        mVideoListAdapter.addNewslist((ArrayList) mSearchResults);
        if(mIsFirstUpdate){
            initializeInfinityScroll();
            mIsFirstUpdate =false;
        }
    }

    private void handleVideosListItemClickListener() {
        final Context cnxt = getActivity().getApplicationContext();
        mVideosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {

                VideoItem videoItem = mVideoListAdapter.getItem(pos);


                if (mYoutubePlayer == null) {
                    initializeYoutubeFragment();
                } else {
                    mYoutubePlayer.loadVideo(videoItem.getId());
                }
                if (mDraggablePanel.getVisibility() != View.VISIBLE) {
                    mDraggablePanel.setVisibility(View.VISIBLE);
                }
                mMovieDescFragment.setVideoItem(videoItem,mUtubeDataConnector);
                mDraggablePanel.maximize();
            }

        });
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
        if (id == R.id.action_search_speech) {
            displaySpeechRecognizer();
            if(mLinearLayout.getVisibility()==View.GONE){
                handleSearchLayoutAnimation();
            }
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

    private void initializeYoutubeFragment() {

        mYoutubeFragment = new YouTubePlayerSupportFragment();

        mYoutubeFragment.initialize(DeveloperKey.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    mYoutubePlayer = player;
                    //    mYoutubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
                    //   mYoutubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
                    mYoutubePlayer.setShowFullscreenButton(true);
                    mIsPlayerInitializeSuccess=true;




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

    private void initializeDraggablePanel() {
        mDraggablePanel.setFragmentManager(getActivity().getSupportFragmentManager());
        mDraggablePanel.setTopFragment(mYoutubeFragment);
        mMovieDescFragment = new PlayerActivityFragment();

        mDraggablePanel.setBottomFragment(mMovieDescFragment);
        hookDraggablePanelListeners();
        mDraggablePanel.initializeView();

    }

    private void hookDraggablePanelListeners() {
        mDraggablePanel.setDraggableListener(new DraggableListener() {
            @Override
            public void onMaximized() {
                playVideo();
                mIsDragablePanelMaximized = true;
            }

            @Override
            public void onMinimized() {
                mIsDragablePanelMaximized = false;
            }

            @Override
            public void onClosedToLeft() {
                pauseVideo();
                mIsDragablePanelMaximized=false;
            }

            @Override
            public void onClosedToRight() {
                pauseVideo();
                mIsDragablePanelMaximized=false;
            }
        });
    }

    private void pauseVideo() {
        if (mYoutubePlayer.isPlaying()) {
            mYoutubePlayer.pause();
        }
    }

    private void playVideo() {
        if (!mYoutubePlayer.isPlaying()) {
             mYoutubePlayer.play();
        }
    }

    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
// This is where you process the intent and extract the speech text from the intent.
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            mSearchInput.setText(spokenText);
            searchOnYoutube(spokenText);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mYoutubePlayer=null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mYoutubePlayer=null;
    }
}
