package exceptions;

/**
 * User: nyilmaz
 * Date: 10/17/12
 * Time: 1:28 AM
 */
public class NullPropertiesException extends InitializationException {

   public NullPropertiesException() {
      super("Properties are null, Please first initialize class...");
   }

   public NullPropertiesException(String message) {
      super(message);
   }

}
