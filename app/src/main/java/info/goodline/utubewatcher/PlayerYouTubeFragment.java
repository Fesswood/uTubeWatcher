package info.goodline.utubewatcher;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import info.goodline.utubewatcher.Util.DeveloperKey;


public class PlayerYouTubeFragment extends YouTubePlayerSupportFragment implements YouTubePlayer.OnInitializedListener  {
    private String currentVideoID = "";
    private YouTubePlayer activePlayer;
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    public static PlayerYouTubeFragment newInstance(String url) {

        PlayerYouTubeFragment playerYouTubeFrag = new PlayerYouTubeFragment();

        Bundle bundle = new Bundle();
        bundle.putString("url", url);

        playerYouTubeFrag.setArguments(bundle);
        playerYouTubeFrag.init();
        return playerYouTubeFrag;
    }

    private void init() {
        initialize(DeveloperKey.DEVELOPER_KEY, this);
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        activePlayer = youTubePlayer;
        activePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
        if (!wasRestored) {
            activePlayer.loadVideo(getArguments().getString("url"), 0);

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
}
