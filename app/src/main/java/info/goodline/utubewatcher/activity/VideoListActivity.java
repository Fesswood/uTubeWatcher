package info.goodline.utubewatcher.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import info.goodline.utubewatcher.R;
import info.goodline.utubewatcher.listener.OnBackPressedListener;


public class VideoListActivity extends AppCompatActivity {

    protected OnBackPressedListener onBackPressedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
    }
    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }
    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null && onBackPressedListener.allowBack())
            onBackPressedListener.doBack();
        else
            super.onBackPressed();
    }
}
