package info.goodline.utubewatcher.listener;


import com.github.pedrovgs.DraggablePanel;

/**
 * Created by sergeyb on 29.05.15.
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