package zip;

import exceptions.InitializationException;
import file.FileOperations;

import java.util.Properties;

/**
 * User: nyilmaz
 * Date: 10/31/12
 * Time: 9:29 PM
 */
public class ZipFileOperations extends FileOperations {

   public ZipFileOperations(){

   }



   @Override
   @SuppressWarnings("unchecked")
   public ZipFileOperations initialize(Properties... properties) throws InitializationException {
      this.properties = properties[0];
      return this;
   }

   @Override
   protected void createFile(String directoryName, String fileNamePrefix) {
      //To change body of implemented methods use File | Settings | File Templates.
   }
}
