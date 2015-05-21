package info.goodline.utubewatcher.Model;

import java.io.Serializable;

/**
 * Created by Балдин Сергей on 21.05.2015.
 */
public class DraggablePanelState implements Serializable {
    private int mCurentTime=0;
    private boolean mIsFullScreenEnabled=false;
    private boolean mIsDragablePanelMaximized=false;

    public DraggablePanelState(){
    }

    public DraggablePanelState(int mCurentTime, boolean mIsFullScreenEnabled, boolean mIsDragablePanelMaximized) {
        this.mCurentTime = mCurentTime;
        this.mIsFullScreenEnabled = mIsFullScreenEnabled;
        this.mIsDragablePanelMaximized = mIsDragablePanelMaximized;
    }

    public int getCurentTime() {
        return mCurentTime;
    }

    public void setCurentTime(int mCurentTime) {
        this.mCurentTime = mCurentTime;
    }

    public boolean isFullScreenEnabled() {
        return mIsFullScreenEnabled;
    }

    public void setFullScreenEnabled(boolean mIsFullScreenEnabled) {
        this.mIsFullScreenEnabled = mIsFullScreenEnabled;
    }

    public boolean ismIsDragablePanelMaximized() {
        return mIsDragablePanelMaximized;
    }

    public void setmIsDragablePanelMaximized(boolean mIsDragablePanelMaximized) {
        this.mIsDragablePanelMaximized = mIsDragablePanelMaximized;
    }
}
