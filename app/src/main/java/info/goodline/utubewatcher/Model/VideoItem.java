package info.goodline.utubewatcher.Model;


import java.io.Serializable;
import java.math.BigInteger;

public class VideoItem implements Serializable{
    private String title;
    private String description;
    private long date;
    private BigInteger viewCounts;
    private String duration;
    private String thumbnailURL;
    private String id;

    public VideoItem() {
        String title="";
        String description="";
        long date=0;
        BigInteger viewCounts=new BigInteger("0");
        String duration="";
        String thumbnailURL="";
        String id="";
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
