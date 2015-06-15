package info.goodline.utubewatcher.fragment;


import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggablePanel;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import info.goodline.utubewatcher.R;
import info.goodline.utubewatcher.util.DeveloperKey;

/**
 *  Fragment initializes draggable panel and hooks events of it
 *  {@link VideoListFragment} extends this class and add search function for it
 *  @author  Sergey Baldin
 */
public class BaseFragment extends Fragment implements YouTubePlayer.OnInitializedListener , YouTubePlayer.OnFullscreenListener{

    private static final int RECOVERY_DIALOG_REQUEST = 1;

    protected YouTubePlayerSupportFragment mYoutubeFragment;
    protected YouTubePlayer mYoutubePlayer;
    protected VideoDescFragment mMovieDescFragment;
    protected DraggablePanel mDraggablePanel;
    /**
     * Indicates success initializing of youtube player
     */
    protected boolean mIsPlayerInitializeSuccess=false;
    /**
     * Indicates current state of Draggable panel
     */
    protected boolean mIsDraggablePanelMaximized=false;
    private View mRootView;
    private boolean mIsFullscreen;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public void initializeBaseFragment(){

        if (mRootView ==null){
            throw new IllegalArgumentException("mRootView mist be set!");
        }

        mDraggablePanel   = (DraggablePanel) mRootView.findViewById(R.id.draggable_panel);
        mDraggablePanel.setVisibility(View.GONE);

        initializeYoutubeFragment();
        initializeDraggablePanel();
    }

    /**
     * Set root view which contains draggable panel
     * @param rootView
     */
    public void setRootView(View rootView){
        mRootView =rootView;
    }

    /**
     * initialize YoutubeFragment like a {@link YouTubePlayerSupportFragment}
     */
    protected void initializeYoutubeFragment() {
        mYoutubeFragment = new YouTubePlayerSupportFragment();
        mYoutubeFragment.initialize(DeveloperKey.DEVELOPER_KEY, this);
    }
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {
            mYoutubePlayer = youTubePlayer;
            mYoutubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE | YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION );
            mYoutubePlayer.setShowFullscreenButton(true);
            mYoutubePlayer.setOnFullscreenListener(this);
            mIsPlayerInitializeSuccess = true;
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(getActivity(), RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(
                    "There was an error initializing the YouTubePlayer (%1$s)",
                    errorReason.toString());
            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
        }
    }
    /**
     * Initialize DraggablePanel and sets to top frame {@link #mYoutubeFragment}
     * and to bottom frame {@link #mMovieDescFragment}
     */
    private void initializeDraggablePanel() {
        mDraggablePanel.setFragmentManager(getActivity().getSupportFragmentManager());
        mDraggablePanel.setTopFragment(mYoutubeFragment);
        mMovieDescFragment = new VideoDescFragment();

        mDraggablePanel.setBottomFragment(mMovieDescFragment);
        hookDraggablePanelListeners();
        mDraggablePanel.initializeView();

    }
    /**
     * Sets listener for hooking draggable states
     */
    private void hookDraggablePanelListeners() {
        mDraggablePanel.setDraggableListener(new DraggableListener() {
            @Override
            public void onMaximized() {
                playVideo();
                mIsDraggablePanelMaximized = true;
            }

            @Override
            public void onMinimized() {
                mIsDraggablePanelMaximized = false;
            }

            @Override
            public void onClosedToLeft() {
                pauseVideo();
                mIsDraggablePanelMaximized = false;
            }

            @Override
            public void onClosedToRight() {
                pauseVideo();
                mIsDraggablePanelMaximized = false;
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


    @Override
    public void onFullscreen(boolean isFullscreenEnabled) {
        mIsFullscreen = isFullscreenEnabled;
       if(!mIsFullscreen){
           getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
       }
    }
}
