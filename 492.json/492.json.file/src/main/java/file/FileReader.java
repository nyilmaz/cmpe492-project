package file;

import beans.twitter.TwitterStreamBean;
import beans.twitter.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

/**
 * User: nyilmaz
 * Date: 12/4/12
 * Time: 6:23 PM
 */
public class FileReader implements Runnable {

   private static Logger logger = LoggerFactory.getLogger(FileReader.class);

   private Properties properties;
   private static String dataDirectory;
   private static String dataFilePrefix;

   public FileReader(Properties properties){
      this.properties = properties;
      dataDirectory = properties.getProperty(FileSupportConstants.data_dir.name());
      dataFilePrefix = properties.getProperty(FileSupportConstants.data_file_prefix.name());
   }

   @Override
   public void run() {
      File dataDir = new File(dataDirectory);
      if(!dataDir.isDirectory()){
         logger.error(FileSupportConstants.data_dir.name() + " is not a directory. Terminating...");
         return;
      }
      String[] fileNames = dataDir.list(new FilenameFilter() {
         @Override
         public boolean accept(File dir, String name) {
            return name.startsWith(dataFilePrefix);
         }
      });

      int totalTweetCount = 0;
      long totalLength = 0l;
      List<TwitterStreamBean> beans = new ArrayList<TwitterStreamBean>();
      Map<User, List<TwitterStreamBean>> map = new HashMap<User, List<TwitterStreamBean>>();

      for(String fileName : fileNames){
         File file = new File(dataDirectory + fileName);
         totalLength += file.length();
         try {
            List<TwitterStreamBean> streamBeans = DataFileOperations.countValidTweets(file, true);
            beans.addAll(streamBeans);
            totalTweetCount += streamBeans.size();
         } catch(IOException e) {
            logger.error("File cannot be opened, fileName: " + fileName, e);
         }
      }
      System.out.println(beans.size());
      for(TwitterStreamBean bean : beans){
         User user = bean.getUser();
         if(!map.containsKey(user)){
            List<TwitterStreamBean> list = new ArrayList<TwitterStreamBean>();
            list.add(bean);
            map.put(user, list);
         }else{
            map.get(user).add(bean);
         }
      }
      File outFile = new File(properties.getProperty(FileSupportConstants.osmxml_output_file.name()));
      try {
//         DataFileOperations.createOSMXML(map, outFile);
         DataFileOperations.createCSV(map ,outFile);
      } catch(IOException e) {
         e.printStackTrace();
      }


      System.out.println("In directory: " + dataDirectory + ", with prefix: \'" + dataFilePrefix
         + "\". Total valid tweets : [" + totalTweetCount + "], in [" + totalLength + "] bytes of data.");
      System.out.println("Map size (Sigle user count): [" + map.size() + "]");

   }


}
