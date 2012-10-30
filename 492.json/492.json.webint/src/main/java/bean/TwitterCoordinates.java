package bean;

import java.util.Arrays;

/**
 * User: nyilmaz
 * Date: 10/30/12
 * Time: 9:45 PM
 */
public class TwitterCoordinates {

   String type;
   float[] coordinates;

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("\n");
      sb.append("\t\ttype : ").append("\t").append(type).append("\n");
      sb.append("\t\tcoordinates : ").append("\t").append(Arrays.toString(coordinates)).append("\n");
      return sb.toString();
   }
}
