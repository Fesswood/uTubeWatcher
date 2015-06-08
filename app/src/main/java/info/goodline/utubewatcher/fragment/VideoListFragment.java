package info.goodline.utubewatcher.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import com.github.pedrovgs.DraggablePanel;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

import info.goodline.utubewatcher.listener.InfinityScrollListener;
import info.goodline.utubewatcher.activity.VideoListActivity;
import info.goodline.utubewatcher.adapter.VideoListAdapter;
import info.goodline.utubewatcher.animation.DropDownAnim;
import info.goodline.utubewatcher.data.VideoItem;
import info.goodline.utubewatcher.R;
import info.goodline.utubewatcher.listener.BaseBackPressedListener;
import info.goodline.utubewatcher.util.DraggableState;
import info.goodline.utubewatcher.connector.YoutubeDataConnector;



public class VideoListFragment extends BaseFragment {

    private static final String STATE_IS_PLAYING = "isPlayingState";
    public static final String VIDEO_LIST_SAVE_STATE="videoListSaveState";

    private static final int SPEECH_REQUEST_CODE = 0;

    private MaterialEditText mSearchInput;
    private ListView mVideosListView;
    private ProgressBar mEmptyProgressBar;
    private VideoListAdapter mVideoListAdapter;
    private LinearLayout mLinearLayout;
    private Handler mHandler;

    private DraggableState mDraggablePanelState;
    private ArrayList<VideoItem> mSearchResults;
    private BaseBackPressedListener mBackPresedListener;
    private YoutubeDataConnector mYoutubeDataConnector;

    private boolean isNeedShowSearchLayout=true;
    private boolean mIsFirstUpdate =true;


    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View inflateView = inflater.inflate(R.layout.fragment_video_list, container, false);
        super.setRootView(inflateView);
        super.initializeBaseFragment();
        mLinearLayout     = (LinearLayout) inflateView.findViewById(R.id.search_layout);
        mSearchInput      = (MaterialEditText) inflateView.findViewById(R.id.search_input);
        mVideosListView   = (ListView) inflateView.findViewById(R.id.videos_found);
        mEmptyProgressBar = (ProgressBar) inflateView.findViewById(R.id.empty_progressbar);
        mDraggablePanel   = (DraggablePanel)inflateView.findViewById(R.id.draggable_panel);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        mBackPresedListener = new BaseBackPressedListener(mDraggablePanel);
        ((VideoListActivity)activity).setOnBackPressedListener(mBackPresedListener);

        mVideosListView.setEmptyView(mEmptyProgressBar);
        mEmptyProgressBar.setVisibility(View.VISIBLE);
        mDraggablePanel.setVisibility(View.GONE);

        mLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(mLinearLayout.getWidth(), 0));

        mYoutubeDataConnector = new YoutubeDataConnector(getActivity());
        mVideoListAdapter=new VideoListAdapter(getActivity());
        mVideosListView.setAdapter(mVideoListAdapter);
        mHandler = new Handler();

        searchInputActionListenerHandle();
        handleVideosListItemClickListener();

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        } else {
            searchOnYoutube(null);
        }
        return inflateView;
    }
    private void restoreInstanceState(@Nullable Bundle savedInstanceState) {
        mSearchResults =(ArrayList) savedInstanceState.getSerializable(VIDEO_LIST_SAVE_STATE);
        if (mSearchResults != null && mSearchResults.size()>0) {
            mVideoListAdapter.addVideoItemList(mSearchResults);
            mVideosListView.setAdapter(mVideoListAdapter);
            if(mIsFirstUpdate){
                initializeInfinityScroll();
                mIsFirstUpdate =false;
            }
        }else{
            searchOnYoutube(null);
        }
        mYoutubeDataConnector = YoutubeDataConnector.recoverNextAndPrevPagesState(savedInstanceState, getActivity());
        final VideoItem videoItem = (VideoItem) savedInstanceState.getSerializable(VideoDescFragment.VIDEO_TAG);
        mDraggablePanelState= (DraggableState) savedInstanceState.getSerializable(DraggableState.DRAGGABLE_PANEL_STATE);

        if(mDraggablePanelState != null){
            new Thread(){
                public void run(){
                    while(true){
                        if(mYoutubePlayer != null && mMovieDescFragment.isInitialized()){

                            if( videoItem != null && videoItem.getId() != null
                                    && videoItem.getId() != ""){
                                mMovieDescFragment.setVideoItem(videoItem, mYoutubeDataConnector);

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
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<VideoItem> videoList = mVideoListAdapter.getVideoList();

        YoutubeDataConnector.saveNextAndPrevPagesState(outState, mYoutubeDataConnector);
        DraggableState.saveDraggableState(outState, mIsDraggablePanelMaximized);
        outState.putSerializable(VIDEO_LIST_SAVE_STATE, videoList);

        outState.putSerializable(mMovieDescFragment.VIDEO_TAG, mMovieDescFragment.getmVideoItem());

        if (mIsPlayerInitializeSuccess ) {
            if(mYoutubePlayer!=null &&  !mYoutubePlayer.isPlaying())
                outState.putBoolean(STATE_IS_PLAYING, true);
        }

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_video_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

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

    @Override
    public void onDetach() {
        super.onDetach();
        mYoutubePlayer=null;
        mBackPresedListener=null;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            mSearchInput.setText(spokenText);
            searchOnYoutube(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void searchInputActionListenerHandle() {
        mSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                final boolean isEnterEvent = event != null
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER;
                final boolean isEnterUpEvent = isEnterEvent && event.getAction() == KeyEvent.ACTION_UP;
                final boolean isEnterDownEvent = isEnterEvent && event.getAction() == KeyEvent.ACTION_DOWN;

                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || isEnterUpEvent) {
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

    private void initializeInfinityScroll() {
        mVideosListView.setOnScrollListener(new InfinityScrollListener(mYoutubeDataConnector.getResultSetSize()) {
            @Override
            public void loadMore(int page, int totalItemsCount) {
                new Thread() {
                    public void run() {
                        mSearchResults = mYoutubeDataConnector.getNextPage();
                        mHandler.post(new Runnable() {
                            public void run() {
                                if (mSearchResults != null && mSearchResults.size() > 0) {
                                    mVideoListAdapter.addVideoItemList((ArrayList) mSearchResults);
                                } else {
                                    Toast.makeText(getActivity(), "Видеоролики закончились", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }.start();
            }
        });
    }

    private void searchOnYoutube(@Nullable final String keywords){
        new Thread(){
            public void run(){
                if (keywords==null){
                    mSearchResults = mYoutubeDataConnector.showLastVideo();
                }else{
                    mSearchResults = mYoutubeDataConnector.search(keywords);
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
        mVideoListAdapter.addVideoItemList((ArrayList) mSearchResults);
        if(mIsFirstUpdate){
            initializeInfinityScroll();
            mIsFirstUpdate =false;
        }
    }

    private void handleVideosListItemClickListener() {
        mVideosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {

                VideoItem videoItem = mVideoListAdapter.getItem(pos);


                if (mYoutubePlayer == null) {
                    VideoListFragment.super.initializeYoutubeFragment();
                } else {
                    mYoutubePlayer.loadVideo(videoItem.getId());
                }
                if (mDraggablePanel.getVisibility() != View.VISIBLE) {
                    mDraggablePanel.setVisibility(View.VISIBLE);
                }
                mMovieDescFragment.setVideoItem(videoItem, mYoutubeDataConnector);
                mDraggablePanel.maximize();
            }

        });
    }

    private void handleSearchLayoutAnimation() {
        int TargetHeight = 0;
        if(isNeedShowSearchLayout){
            TargetHeight=130;
            mLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
        }
        final DropDownAnim dropDownAnim = new DropDownAnim(mLinearLayout, TargetHeight, isNeedShowSearchLayout);
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

    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }




}
