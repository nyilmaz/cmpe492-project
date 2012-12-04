package beans.twitter;

import java.util.Arrays;

/**
 * User: nyilmaz
 * Date: 10/29/12
 * Time: 8:44 AM
 */
public class BoundingBoxCoordinates {


   String type;
   float[][][] coordinates;

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("\n");
      sb.append("\t\ttype : ").append("\t").append(type).append("\n");
      sb.append("\t\tcoordinates : ").append("\t").append(Arrays.deepToString(coordinates)).append("\n");
      return sb.toString();
   }
}
