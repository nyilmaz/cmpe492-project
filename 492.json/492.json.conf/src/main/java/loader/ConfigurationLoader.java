package loader;

import exceptions.InitializationException;
import interfaces.LoadableConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * User: nyilmaz
 * Date: 10/3/12
 * Time: 4:52 PM
 */
public class ConfigurationLoader implements LoadableConfiguration {

   private static Logger logger = LoggerFactory.getLogger(ConfigurationLoader.class);
   private static final String confFile = "./492.config";
   private static Properties properties;
   private static Properties optionalParameters;

   public Properties getProperties(){
      return properties;
   }

   @SuppressWarnings("unchecked")
   public ConfigurationLoader initializeConfiguration() throws InitializationException{

      properties = new Properties();
      optionalParameters = new Properties();

      try {
         properties.load(new FileInputStream(confFile));
         optionalParameters.load(new FileInputStream(properties.getProperty("parameters_file")));

      } catch(IOException e) {
         logger.error("Cannot load configuration file(s)... Exiting.", e);
         throw new InitializationException();

      }
      return this;
   }

   public Properties getOptionalParameters() {
      return optionalParameters;
   }

   public static void main(String[] args) {
      SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy_");
      System.out.println(sdf.format(new Date()));
   }
}
