package info.goodline.utubewatcher.listener;

/**
 * Interface for providing click hardware back button to fragment
 */
public interface OnBackPressedListener {
    public void doBack();
    public boolean allowBack();
}
