package file;

import beans.twitter.TwitterStreamBean;
import beans.twitter.User;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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

    private static ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat>(){
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
        }

        @Override
        public DateFormat get() {
            return super.get();
        }

        @Override
        public void set(DateFormat value) {
            super.set(value);
        }

        @Override
        public void remove() {
            super.remove();
        }
    };
   public static void main(String[] args) throws IOException, ParseException {

       long[] countBesiktas = new long[12];
       long[] countTaksim = new long[12];
       long[] countKadikoy = new long[12];
       long count = 0l;
       long userCount = 0l;
       long error = 0l;
       long withCoordinates = 0l;
       Date mindate = new Date();
       Date maxdate = new Date(0l);
       String dir = "/media/SAMSUNG/data/yilbasi/";

       String[] fileNames = new File(dir).list(new FilenameFilter() {
           @Override
           public boolean accept(File dir, String name) {
               return name.startsWith("threaded_deneme");
           }
       });


       for(String fileName : fileNames){


       BufferedReader br = new BufferedReader(new java.io.FileReader(dir + fileName));
       String line;




       Gson gson = new Gson();
       List<TwitterStreamBean> list = new ArrayList<TwitterStreamBean>();
       while((line = br.readLine()) != null){

           Type collectionType = new TypeToken<List<TwitterStreamBean>>(){}.getType();
           List<TwitterStreamBean> tweets;
           try{

               tweets = gson.fromJson(line, collectionType);
           }catch (JsonSyntaxException ex){
               logger.info("Unparsable line :" + line);
               error++;
               continue;
           }
           count++;
           userCount++;
           for(TwitterStreamBean bean : tweets){
               count++;
               Date tweetDate = df.get().parse(bean.getCreated_at());
               if(bean.getCoordinates() == null){
                   continue;
               }
               withCoordinates++;
//               if(tweetDate.after(maxdate)){
//                   maxdate = tweetDate;
//               }
//               if(tweetDate.before(mindate)){
//                   mindate = tweetDate;
//               }
               list.add(bean);
           }
       }
       FileOutputStream fileOutputStream = new FileOutputStream("/media/SAMSUNG/data/yilbasi/yilbasi_coordinates_200_per_user", true);
       for(TwitterStreamBean bean : list){

//           Calendar cal = Calendar.getInstance();
//           cal.setTime(df.get().parse(bean.getCreated_at()));
//           int month = cal.get(Calendar.MONTH);
           double lat = bean.getCoordinates().getCoordinates()[1];
           double lng = bean.getCoordinates().getCoordinates()[0];

           fileOutputStream.write(("\""+ bean.getId_str() + "\"," + lat + "," + lng + "\n").getBytes());

//           if(checkInBounds(41.0423,29.00656, lat, lng) < 500){
//               countBesiktas[month]++;
//           }else if(checkInBounds(41.037736,28.984759, lat, lng) < 500){
//               countTaksim[month]++;
//           }else if(checkInBounds(40.991496,29.022088, lat, lng) < 500){
//               countKadikoy[month]++;
//           }

       }
       fileOutputStream.close();
       }
       System.out.println("Mindate: " + mindate);
       System.out.println("Maxdate: " + maxdate);
       System.out.println("Count: " + count);
       System.out.println("User Count: " + userCount);
       System.out.println("Errors: " + error);
       System.out.println("With coordinates: " + withCoordinates);
       System.out.println();
       System.out.println("Besiktas : " + Arrays.toString(countBesiktas));
       System.out.println("Taksim : " + Arrays.toString(countTaksim));
       System.out.println("Kadikoy : " + Arrays.toString(countKadikoy));
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
