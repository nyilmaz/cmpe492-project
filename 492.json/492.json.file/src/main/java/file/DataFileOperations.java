package file;

import beans.twitter.TwitterStreamBean;
import beans.twitter.User;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: nyilmaz
 * Date: 12/4/12
 * Time: 6:40 PM
 */
public class DataFileOperations {

   private static Logger logger = LoggerFactory.getLogger(DataFileOperations.class);

   private static final String OSM_TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
   private static final String TWITTER_TIMESTAMP_FORMAT = "EEE MMM dd HH:mm:ss '+0000' yyyy";


   public static List<TwitterStreamBean> countValidTweets(File file, boolean getLocationedTweets) throws IOException {

      BufferedReader bReader = new BufferedReader(new java.io.FileReader(file));
      String line;
      Gson gson = new Gson();
      TwitterStreamBean bean;
      List<TwitterStreamBean> beans = new ArrayList<TwitterStreamBean>();
      while((line = bReader.readLine()) != null){
         try{

            bean = gson.fromJson(line, TwitterStreamBean.class);

         }catch(JsonSyntaxException ex){
            continue;
         }
         if(getLocationedTweets){
            if(bean.getCoordinates() == null){
               continue;
            }
         }
         beans.add(bean);
      }
      return beans;
   }

   public static void createOSMXML(Map<User, List<TwitterStreamBean>> infoMap, File outfile) throws IOException {

      if(!outfile.createNewFile()){
         logger.error("Cannot create xml output file. Create osm xml failed.");
         return;
      }
      FileOutputStream outputStream = new FileOutputStream(outfile);
      outputStream.write(getOSMXMLHeader().getBytes());
      Collection<List<TwitterStreamBean>> allTweets = infoMap.values();
      for(List<TwitterStreamBean> nodeList : allTweets){
         for(TwitterStreamBean bean : nodeList){
            outputStream.write(createNode(bean).getBytes());
         }
         outputStream.flush();
      }

      Collection<User> users = infoMap.keySet();
      for(User user : users){
         outputStream.write(createWay(user, infoMap.get(user)).getBytes());
         outputStream.flush();
      }
      outputStream.write("</osm>".getBytes());

      outputStream.close();


   }

   public static String getOSMXMLHeader(){
      return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
         "<osm version=\"0.6\" generator=\"CGImap 0.0.2\">\n";
   }

   private static String createNode(TwitterStreamBean tweet){
      try {
         Date date = new SimpleDateFormat(TWITTER_TIMESTAMP_FORMAT).parse(tweet.getCreated_at());
         String formattedDate = new SimpleDateFormat(OSM_TIMESTAMP_FORMAT).format(date);
         return "<node id=\"" + tweet.getId_str() + "\" " +
            "lat=\"" + tweet.getCoordinates().getCoordinates()[1]+ "\" " +
            "lon=\"" + tweet.getCoordinates().getCoordinates()[0] + "\" " +
            "visible=\"true\" " +
            "timestamp=\"" + formattedDate + "\"" + "/>\n";
      } catch(ParseException e) {
         logger.warn("Cannot parse created_date:" + tweet.getCreated_at());
      }
      return "";

   }

   private static String createWay(User user, List<TwitterStreamBean> tweets){
      StringBuilder builder = new StringBuilder("<way id=\"" + user.getId() + "\" " +
         "name=\"" + user.getName() + "\" " +
         "visible=\"true\" " +
         ">\n");

      for(TwitterStreamBean tweet : tweets){
         builder.append("<nd ref=\"").append(tweet.getId_str()).append("\"/>\n");
      }
      builder.append("</way>");
      return builder.toString();
   }

   public static void createCSV(Map<User, List<TwitterStreamBean>> infoMap, File outfile) throws IOException {
      if(!outfile.createNewFile()){
         logger.error("Cannot create csv output file. Create csv failed.");
         return;
      }
      FileOutputStream outputStream = new FileOutputStream(outfile);

      for(User user : infoMap.keySet()){
         StringBuilder builder = new StringBuilder();
         builder.append(user.getId()).append(",\"").append(user.getName()).append("\",");
         for(TwitterStreamBean bean : infoMap.get(user)){

            builder.append(bean.getCoordinates().getCoordinates()[0])
               .append(",")
               .append(bean.getCoordinates().getCoordinates()[1])
               .append(",");
         }
         builder.setLength(builder.length() - 1);
         builder.append("\n");
         outputStream.write(builder.toString().getBytes());
         outputStream.flush();
      }
      outputStream.close();


   }

   public static void createUserIdFile(Map<User, List<TwitterStreamBean>> infoMap, File outFile) throws IOException{
      if(!outFile.createNewFile()){
         logger.error("Cannot create user_ids file.");
         return;
      }

      FileOutputStream outputStream = new FileOutputStream(outFile);
      for(User user : infoMap.keySet()){
         String line = user.getId() + "\n";
         outputStream.write(line.getBytes());
      }
      outputStream.close();
   }

   public static void main(String[] args) throws ParseException {
      StringBuilder builder = new StringBuilder("1234567");
      builder.setLength(builder.length()-1);
      System.out.println(builder.toString());

   }
}
