package info.goodline.utubewatcher;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;

import info.goodline.utubewatcher.Util.DeveloperKey;



public class PlayerActivityFragment extends Fragment implements YouTubePlayer.OnInitializedListener {


    private YouTubePlayerFragment mYouTubeFragment;
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

       /* mYouTubeFragment = (YouTubePlayerFragment) getActivity().getFragmentManager().findFragmentById(R.id.youtubeplayerfragment);
        mYouTubeFragment.initialize(DeveloperKey.DEVELOPER_KEY, this);*/
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
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
       // Toast.makeText(getActivity(), getString(R.string.failed), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean restored) {
        if(!restored){
            String videoId=getActivity().getIntent().getStringExtra(VideoListActivityFragment.VIDEO_ID_TAG);
            player.cueVideo(videoId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player, container, false);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

   /*     if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(DeveloperKey.DEVELOPER_KEY, this);
        }*/
    }

   /* protected YouTubePlayer.Provider getYouTubePlayerProvider() {
      *//*  return (YouTubePlayerView)getView().findViewById(R.id.youtubeplayerfragment);*//*
    }*/
}
