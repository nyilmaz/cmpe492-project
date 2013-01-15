package beans.twitter;

import util.ProjectUtil;

import java.text.ParseException;

/**
 * User: nyilmaz
 * Date: 10/11/12
 * Time: 1:56 AM
 */
public class TwitterBean implements Comparable{

   private String id_str;

   private TwitterCoordinates coordinates;
   private String text;
   private String created_at;
   private String source;
   private Integer in_reply_to_user_id_str;
   private Entities entities;
   private Boolean retweeted;
   private Place place;
   private User user;

   public String getText() {
      return text;
   }

   public void setText(String text) {
      this.text = text;
   }

   public TwitterCoordinates getCoordinates() {
      return coordinates;
   }

   public void setCoordinates(TwitterCoordinates coordinates) {
      this.coordinates = coordinates;
   }

   public String getCreated_at() {
      return created_at;
   }

   public void setCreated_at(String created_at) {
      this.created_at = created_at;
   }

   @Override
   public String toString(){
      StringBuilder sb = new StringBuilder();
      sb.append("-------------TwitterBean--------------\n");
      sb.append("id : ").append("\t").append(id_str).append("\n");
      sb.append("coordinates : ").append("\t").append(coordinates).append("\n");
      sb.append("text : ").append("\t").append(text).append("\n");
      sb.append("created_at : ").append("\t").append(created_at).append("\n");
      sb.append("place : ").append("\t").append(place);
      return sb.toString();
   }

   public User getUser() {
      return user;
   }

   public void setUser(User user) {
      this.user = user;
   }

   public String getId_str() {
      return id_str;
   }

   public void setId_str(String id_str) {
      this.id_str = id_str;
   }

    @Override
    public int compareTo(Object o) {
        if(((TwitterBean)o).getCoordinates() == null)
            return -1;

        try {
            return ProjectUtil.TWITTER_DATE_FORMAT.get().parse(this.getCreated_at())
                    .before(ProjectUtil.TWITTER_DATE_FORMAT.get().parse(((TwitterBean) o).getCreated_at()))
                    ? -1 : 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


}
