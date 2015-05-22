package info.goodline.utubewatcher.Util;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.github.pedrovgs.DraggablePanel;

import java.io.Serializable;

/**
 * Created by Балдин Сергей on 22.05.2015.
 */
public enum DraggableState implements Serializable {

    MINIMIZED, MAXIMIZED, CLOSED_AT_LEFT, CLOSED_AT_RIGHT;


    public static final String DRAGGABLE_PANEL_STATE = "DraggableState.DraggablePanel";
    private static final long DELAY_MILLIS = 100;

    public static void saveDraggableState(Bundle outState ,DraggablePanel draggablePanel) {
        DraggableState draggableState = null;
        if (draggablePanel.isMaximized()) {
            draggableState = DraggableState.MAXIMIZED;
        } else if (draggablePanel.isMinimized()) {
            draggableState = DraggableState.MINIMIZED;
        } else if (draggablePanel.isClosedAtLeft()) {
            draggableState = DraggableState.CLOSED_AT_LEFT;
        } else if (draggablePanel.isClosedAtRight()) {
            draggableState = DraggableState.CLOSED_AT_RIGHT;
        }
        outState.putSerializable(DRAGGABLE_PANEL_STATE, draggableState);
    }
    public static void recoverDraggablePanelState(Bundle savedInstanceState , DraggablePanel draggablePanel) {
        final DraggableState draggableState =
                (DraggableState) savedInstanceState.getSerializable(DRAGGABLE_PANEL_STATE);
        if (draggableState == null) {
            draggablePanel.setVisibility(View.GONE);
            return;
        }
        updateDraggablePanelStateDelayed(draggableState,draggablePanel);
    }
    private static void updateDraggablePanelStateDelayed(DraggableState draggableState,final DraggablePanel draggablePanel) {
        Handler handler = new Handler();
        switch (draggableState) {
            case MAXIMIZED:
                        draggablePanel.maximize();

                break;
            case MINIMIZED:

                        draggablePanel.minimize();

                break;
            case CLOSED_AT_LEFT:

                        draggablePanel.setVisibility(View.GONE);
                        draggablePanel.closeToLeft();

                break;
            case CLOSED_AT_RIGHT:
                        draggablePanel.setVisibility(View.GONE);
                        draggablePanel.closeToRight();
                break;
            default:
                draggablePanel.setVisibility(View.GONE);
                break;
        }
    }
}
