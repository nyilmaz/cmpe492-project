package beans.twitter;

import org.joda.time.DateTime;



/**
 * User: nyilmaz
 */
public class LightTwitterBean implements Comparable<LightTwitterBean>{

    private Long id;
    private Long userId;
    private Double lat;
    private Double lon;
    private DateTime createDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public DateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(DateTime createDate) {
        this.createDate = createDate;
    }


    @Override
    public int compareTo(LightTwitterBean o) {
        return createDate.compareTo(o.getCreateDate());
    }
}
