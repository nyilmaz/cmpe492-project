package bean;

/**
 * User: nyilmaz
 * Date: 10/11/12
 * Time: 1:56 AM
 */
public class TwitterStreamBean {

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




   @Override
   public String toString(){
      StringBuilder sb = new StringBuilder();
      sb.append("-------------TwitterStreamBean--------------\n");
      sb.append("id_str : ").append("\t").append(id_str).append("\n");
      sb.append("coordinates : ").append("\t").append(coordinates).append("\n");
      sb.append("text : ").append("\t").append(text).append("\n");
      sb.append("created_at : ").append("\t").append(created_at).append("\n");
      sb.append("place : ").append("\t").append(place);
      return sb.toString();
   }
}
