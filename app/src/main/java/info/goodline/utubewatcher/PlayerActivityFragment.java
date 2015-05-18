package info.goodline.utubewatcher;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerView;

import info.goodline.utubewatcher.Util.DeveloperKey;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends YouTubePlayerFragment implements YouTubePlayer.OnInitializedListener {

    private YouTubePlayerView playerView;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);


        playerView = (YouTubePlayerView)getView().findViewById(R.id.player_view);
        playerView.initialize(DeveloperKey.DEVELOPER_KEY, this);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult result) {
        Toast.makeText(getActivity(), getString(R.string.failed), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean restored) {
        if(!restored){
            String videoId=getActivity().getIntent().getStringExtra(VideoListActivityFragment.VIDEO_ID_TAG);
            player.cueVideo(videoId);
        }
    }

    public PlayerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player, container, false);
    }
}
