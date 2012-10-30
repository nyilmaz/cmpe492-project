package deserializer;

import bean.BoundingBoxCoordinates;
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * User: nyilmaz
 * Date: 10/30/12
 * Time: 9:28 PM
 */
public class CoordinatesDeserializer implements JsonDeserializer<BoundingBoxCoordinates> {
   @Override
   public BoundingBoxCoordinates deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {

      String _type = jsonElement.getAsJsonObject().get("type").getAsString();
      if(_type != null){
         if(_type.equals("Point")){

         }
      }
      return null;
   }
}
