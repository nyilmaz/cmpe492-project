package file;

import interfaces.Initializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Properties;

/**
 * User: nyilmaz
 * Date: 10/31/12
 * Time: 9:28 PM
 */
public abstract class FileOperations implements Initializable {

   private static Logger logger = LoggerFactory.getLogger(FileOperations.class);
   protected boolean fileOperationsEnabled;
   protected Properties properties;
   protected String directoryName;
   protected String fileNamePrefix;
   protected String fileExtension;



   protected void createDirectory(){
      File file = new File(directoryName);
      boolean success = false;
      if(!file.exists()){
         if(!file.isDirectory()){
            try{
               success = file.mkdirs();
               if(!success)
                  throw new IOException();
            }catch(SecurityException e){
               logger.error("Output file directory cannot be created, check read-write permissions.", e);
            } catch(IOException e) {
               logger.error("Output file directory cannot be created, an error occured", e);
            }
         }
      }
   }


   protected void createFile(){
      String[] files = new File(directoryName).list();
      Arrays.sort(files);
      String lastFileName = files[files.length - 1];
      int nextFileNo = Integer.parseInt(String.valueOf(lastFileName.charAt(lastFileName.lastIndexOf("-") + 1)));
      File f = new File(directoryName + File.pathSeparator + fileNamePrefix + "-" + nextFileNo + fileExtension);
      try {
         f.createNewFile();
      } catch(IOException e) {
         logger.warn("New data file cannot be created, disabling file io.", e);
      }


   }
   protected abstract void createFile(String directoryName, String fileNamePrefix);

   public static void main(String[] args) {
      String asd = "asd-3.zip";
      int i = asd.lastIndexOf("-");
      BigInteger bigInteger = BigInteger.ZERO;

      System.out.println(bigInteger);
      bigInteger = bigInteger.add(BigInteger.TEN);
      System.out.println(bigInteger);
   }

}
