package exceptions;

/**
 * User: nyilmaz
 * Date: 10/31/12
 * Time: 9:55 PM
 */
public class InitializationException extends Exception {
   public InitializationException() {
      super();
   }

   public InitializationException(String message) {
      super(message);
   }
}
