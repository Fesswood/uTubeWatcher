package info.goodline.utubewatcher.animation;


import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Drop Down Animation for search video input
 */
public class DropDownAnim extends Animation {
    /**
     * Height which View will have after animation
     */
    private final int targetHeight;
    /**
     *  Animated view
     */
    private final View view;
    /**
     * Direction of animation
     */
    private  boolean down;

    public DropDownAnim(View view, int targetHeight, boolean down) {
        this.view = view;
        this.targetHeight = targetHeight;
        this.down = down;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newHeight;
        if (down) {
            newHeight = (int) (targetHeight * interpolatedTime);
        } else {
            newHeight = (int) (view.getLayoutParams().height * (1 - interpolatedTime));
        }
        view.getLayoutParams().height = newHeight;
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth,
                           int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }


    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }
}
