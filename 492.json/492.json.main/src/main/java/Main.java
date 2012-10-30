/**
 * User: nyilmaz
 * Date: 10/3/12
 * Time: 4:39 PM
 */
public class Main {


   private static ConfigurationLoader conf;

   public static void main(String[] args) {
      conf = new ConfigurationLoader().initialize();

      WebInterface webInterface = WebInterface.getInstance(conf.getProperties(), conf.getOptionalParameters());

      webInterface.connect();
      //BufferedReader bufferedReader = webInterface.getBufferedReader();






   }
}
