package file;

import beans.twitter.TwitterBean;
import beans.twitter.User;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
      List<TwitterBean> beans = new ArrayList<TwitterBean>();
      Map<User, List<TwitterBean>> map = new HashMap<User, List<TwitterBean>>();

      for(String fileName : fileNames){
         File file = new File(dataDirectory + fileName);
         totalLength += file.length();
         try {
            List<TwitterBean> streamBeans = DataFileOperations.countValidTweets(file, true);
            beans.addAll(streamBeans);
            totalTweetCount += streamBeans.size();
         } catch(IOException e) {
            logger.error("File cannot be opened, fileName: " + fileName, e);
         }
      }
      System.out.println(beans.size());
      for(TwitterBean bean : beans){
         User user = bean.getUser();
         if(!map.containsKey(user)){
            List<TwitterBean> list = new ArrayList<TwitterBean>();
            list.add(bean);
            map.put(user, list);
         }else{
            map.get(user).add(bean);
         }
      }

      try {
          Set<User> userSet = map.keySet();
          Set<Integer> userIdSet = FluentIterable.from(userSet).transform(new Function<User, Integer>() {
              @Override
              public Integer apply(User input) {
                  return input.getId();
              }
          }).toImmutableSet();
          DataFileOperations.createUserIdFile(userIdSet, new File("/media/SAMSUNG/data/yilbasi/yilbasi_user_ids"));
      } catch(IOException e) {
         e.printStackTrace();
      }


      System.out.println("In directory: " + dataDirectory + ", with prefix: \'" + dataFilePrefix
         + "\". Total valid tweets : [" + totalTweetCount + "], in [" + totalLength + "] bytes of data.");
      System.out.println("Map size (Sigle user count): [" + map.size() + "]");

   }


   public static void main(String[] args) throws IOException, ParseException {

       String dir = "C:\\Users\\px5x2\\data\\yilbasi\\";

       String[] fileNames = new File(dir).list(new FilenameFilter() {
           @Override
           public boolean accept(File dir, String name) {
               return name.startsWith("threaded_deneme");
           }
       });


       for(String fileName : fileNames){

           long start = System.currentTimeMillis();
           BufferedReader br = new BufferedReader(new java.io.FileReader(dir + fileName));
           String line;

           Gson gson = new Gson();
           while((line = br.readLine()) != null){

               Type collectionType = new TypeToken<List<TwitterBean>>(){}.getType();
               List<TwitterBean> userTweets;
               userTweets = gson.fromJson(line, collectionType);

               for(TwitterBean bean : userTweets){
                   if(bean.getCoordinates() == null){
                       continue;
                   }

               }

           }

           logger.info("File read for \"" + fileName + "\" has last for [" + (System.currentTimeMillis()-start)/1000 + "] seconds.");
       }

   }

   /*
   calculate distance between (in meters) two points on Earth's surface
    */
   public static double checkInBounds(double lat, double lng, double tweetLat, double tweetLong){
      double earthRadius = 6371;
      double dLat = Math.toRadians(lat - tweetLat);
      double dLong = Math.toRadians(lng - tweetLong);
      double _tLat = Math.toRadians(tweetLat);
      double _tLong = Math.toRadians(tweetLong);

      double a = Math.pow(Math.sin(dLat/2), 2) + Math.cos(_tLat)*Math.cos(_tLong)*Math.pow(Math.sin(dLong/2), 2);
      double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));


      return (earthRadius*c)*1000;
   }


}
