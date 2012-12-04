package beans.twitter;

/**
 * User: nyilmaz
 * Date: 10/29/12
 * Time: 8:42 AM
 */
public class Place {

   private String country_code;
   private String url;
   private String country;
   private String full_name;
   private String name;
   private String id;
   private BoundingBoxCoordinates bounding_box;

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("\n");
      sb.append("\tid : ").append("\t").append(id).append("\n");
      sb.append("\tcounty_code : ").append("\t").append(country_code).append("\n");
      sb.append("\tname : ").append("\t").append(name).append("\n");
      sb.append("\tbounding_box : ").append("\t").append(bounding_box).append("\n");
      return sb.toString();
   }
}
