package info.goodline.utubewatcher.listener;


import com.github.pedrovgs.DraggablePanel;

/**
 *  Class provides ability for calling method minimize of draggable panel in Activity
 *  Used in {@link info.goodline.utubewatcher.activity.VideoListActivity}
 *  @author  Sergey Baldin
 */
public class BaseBackPressedListener implements OnBackPressedListener {
    private final DraggablePanel panel;

    public BaseBackPressedListener(DraggablePanel panel) {
      this.panel = panel;
    }

    @Override
    public void doBack() {
            panel.minimize();
    }

    @Override
    public boolean allowBack() {
        return panel.isMaximized();
    }
}