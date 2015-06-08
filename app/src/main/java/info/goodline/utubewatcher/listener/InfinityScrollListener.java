package info.goodline.utubewatcher.listener;

import android.widget.AbsListView;

/**
 *  abstract Listener for loading data while user scrolling listView
 */
public abstract class InfinityScrollListener  implements AbsListView.OnScrollListener
{
    /**
     * Offset while method {@link #loadMore(int, int)} will call
     */
    private int itemsOffset      = 5;
    /**
     * items by one load operation
     */
    private long bufferItemCount  = 10;
    private int currentPage   = 0;
    private int itemCount    = 0;
    /**
     * flag indicates current state of loading process
     */
    private boolean isLoading   = true;


    public InfinityScrollListener(long bufferItemCount)
    {
        this.bufferItemCount = bufferItemCount;
    }

    public abstract void loadMore(int page, int totalItemsCount);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        // Do Nothing
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        if (totalItemCount < itemCount)
        {
            this.itemCount = totalItemCount;
            if (totalItemCount == 0)
            {
                this.isLoading = true;
            }
        }

        if (isLoading && (totalItemCount > itemCount))
        {
            isLoading = false;
            itemCount = totalItemCount;
            currentPage++;
        }

        if (!isLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + bufferItemCount + itemsOffset))
        {
            loadMore(currentPage + 1, totalItemCount);
            isLoading = true;
        }
    }
}