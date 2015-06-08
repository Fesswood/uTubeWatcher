package info.goodline.utubewatcher.util;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.github.pedrovgs.DraggablePanel;

import java.io.Serializable;

/**
 * Created by Балдин Сергей on 22.05.2015.
 */
public enum DraggableState implements Serializable {

    MINIMIZED, MAXIMIZED;


    public static final String DRAGGABLE_PANEL_STATE = "DraggableState.DraggablePanel";

    public static void saveDraggableState(Bundle outState ,boolean isDragablePanelMaximized) {
        DraggableState draggableState = null;
        if (isDragablePanelMaximized) {
            draggableState = DraggableState.MAXIMIZED;
        } else {
            draggableState = DraggableState.MINIMIZED;
        }
        outState.putSerializable(DRAGGABLE_PANEL_STATE, draggableState);
    }

}
