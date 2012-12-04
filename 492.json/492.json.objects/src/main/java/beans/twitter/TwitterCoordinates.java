package beans.twitter;

import java.util.Arrays;

/**
 * User: nyilmaz
 * Date: 10/30/12
 * Time: 9:45 PM
 */
public class TwitterCoordinates {

   String type;
   float[] coordinates;


   public String getType() {
      return type;
   }

   public float[] getCoordinates() {
      return coordinates;
   }
   public String getCoordinates2() {
      return coordinates[0] + "," + coordinates[1] + ",";
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("\n");
      sb.append("\t\ttype : ").append("\t").append(type).append("\n");
      sb.append("\t\tcoordinates : ").append("\t").append(Arrays.toString(coordinates)).append("\n");
      return sb.toString();
   }
}
