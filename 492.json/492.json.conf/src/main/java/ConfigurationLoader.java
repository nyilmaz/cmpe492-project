import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * User: nyilmaz
 * Date: 10/3/12
 * Time: 4:52 PM
 */
public class ConfigurationLoader {

   private static Logger logger = LoggerFactory.getLogger(ConfigurationLoader.class);
   private static final String confFile = "./492.config";
   private static Properties properties;
   private static OauthConstants constants;

   public Properties getProperties(){
      return properties;
   }

   public ConfigurationLoader initialize(){
      boolean success = false;
      properties = new Properties();

      try {
         properties.load(new FileInputStream(confFile));


      } catch(IOException e) {
         logger.error("Cannot load configuration file... Exiting.", e);
         System.exit(-1);

      }
      return this;

   }


}
