package info.goodline.utubewatcher.data;


import java.io.Serializable;
import java.math.BigInteger;

/**
 *  Describes instance of video providing by YouTube Api v3
 *  @author  Sergey Baldin
 */
public class VideoItem implements Serializable{
    /**
     * Title of video
     */
    private String title;
    /**
     * Description of video
     */
    private String description;
    /**
     * Publish dates of video
     */
    private long date;
    /**
     * Counts of view
     */
    private BigInteger viewCounts;
    /**
     * Time duration of video
     */
    private String duration;
    /**
     * Url to image thumbnail which displays in list view
     */
    private String thumbnailURL;
    /**
     * Id of video for getting stream or description
     */
    private String id;

    public VideoItem() {
        String title="";
        String description="";
        String duration="";
        String thumbnailURL="";
        String id="";
        long date=0;
        BigInteger viewCounts=new BigInteger("0");
    }

    public BigInteger getViewCounts() {
        return viewCounts;
    }

    public void setViewCounts(BigInteger viewCounts) {
        this.viewCounts = viewCounts;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnail) {
        this.thumbnailURL = thumbnail;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getDate() {
        return date;
    }
}
