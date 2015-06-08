package info.goodline.utubewatcher.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;

import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggablePanel;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import info.goodline.utubewatcher.R;
import info.goodline.utubewatcher.activity.VideoListActivity;
import info.goodline.utubewatcher.listener.BaseBackPressedListener;
import info.goodline.utubewatcher.util.DeveloperKey;

/**
 *  Fragment initializes draggable panel and hooks events of it
 *  {@link VideoListFragment} extends this class and add search function for it
 *  @author  Sergey Baldin
 */
public class BaseFragment extends Fragment {

    private static final int RECOVERY_DIALOG_REQUEST = 1;

    protected YouTubePlayerSupportFragment mYoutubeFragment;
    protected YouTubePlayer mYoutubePlayer;
    protected VideoDescFragment mMovieDescFragment;
    protected DraggablePanel mDraggablePanel;

    protected boolean mIsPlayerInitializeSuccess=false;
    protected boolean mIsDraggablePanelMaximized=false;
    private View mRootView;

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

    protected void initializeYoutubeFragment() {

        mYoutubeFragment = new YouTubePlayerSupportFragment();
        mYoutubeFragment.initialize(DeveloperKey.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    mYoutubePlayer = player;
                    mYoutubePlayer.setShowFullscreenButton(true);
                    mIsPlayerInitializeSuccess = true;
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
        mMovieDescFragment = new VideoDescFragment();

        mDraggablePanel.setBottomFragment(mMovieDescFragment);
        hookDraggablePanelListeners();
        mDraggablePanel.initializeView();

    }

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


}
