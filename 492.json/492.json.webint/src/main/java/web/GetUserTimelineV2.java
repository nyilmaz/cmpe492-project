package web;

import constants.ProgramConstants;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: nyilmaz
 * Date: 1/1/13
 * Time: 9:41 PM
 */
public class GetUserTimelineV2 implements Runnable{

   private static Logger logger = LoggerFactory.getLogger(GetUserTimelineV2.class);

   private Properties properties;
   private Properties optionalParameters;
   private File outputFile;
   private Integer userId;
   private AtomicInteger integer;

   public GetUserTimelineV2(Properties properties, Properties optionalParameters, File outputFile, Integer userId, AtomicInteger integer){
      this.properties = properties;
      this.optionalParameters = optionalParameters;
      this.outputFile = outputFile;
      this.userId = userId;
      this.integer = integer;
   }



   @Override
   public void run() {

      if(integer.get() == 0){
         try {
            logger.info("Rate limit exceeded sleeping for 15 mins...");
            Thread.sleep(900000);
         } catch(InterruptedException e) {
            e.printStackTrace();
         }
      }
      long beginTime = System.currentTimeMillis();
      int tweetCountPerUser = Integer.parseInt(properties.getProperty(ProgramConstants.tweet_count_per_user.name()));

      synchronized (optionalParameters){
         optionalParameters.put("count", properties.getProperty(ProgramConstants.tweet_count_per_user.name()));
         optionalParameters.put("user_id", userId);
      }


      OAuthHeader header = new OAuthHeader(properties, optionalParameters);
      HttpClient httpClient = new DefaultHttpClient();

      try {
         String oauthHeader = header.getAuthorizationHeaderString();
         HttpGet httpGet = new HttpGet(new URI(createRequestURL(oauthHeader, userId, tweetCountPerUser)));
         httpGet.setHeader("Authorization", oauthHeader);
         HttpResponse httpResponse = httpClient.execute(httpGet);
         HttpEntity httpEntity = httpResponse.getEntity();

         BufferedReader reader = new BufferedReader(new InputStreamReader(httpEntity.getContent()));
         String line;
         FileOutputStream outputStream = new FileOutputStream(outputFile, true);

         while((line = reader.readLine()) != null){
            synchronized (outputStream){
               outputStream.write((line + "\n").getBytes());
            }
         }

         Header[] headers = httpResponse.getAllHeaders();
         for(Header header1 : headers){
            if(header1.getName().equals("X-Rate-Limit-Remaining")){
               integer.set(Integer.valueOf(header1.getValue()));
            }
         }
         reader.close();
         EntityUtils.consume(httpEntity);
         httpGet.releaseConnection();
         outputStream.close();
         logger.info("userId :" + userId + " tweets got in " + (System.currentTimeMillis() - beginTime)/1000 + " seconds.");


      }catch(Exception e){
         logger.error("Error occured", e);
      }


   }

   private String createRequestURL(String headerString, int userId, int count){

      StringBuilder url = new StringBuilder(properties.getProperty(ProgramConstants.base_url.name()));
      url.append("?");

      // set OAuth parameters
      url.append(OAuthHeader.getOAuthParameter(headerString));

      // set http GET parameters
      url.append("&");
      url.append("user_id=").append(userId).append("&");
      url.append("count=").append(count);

      return url.toString();
   }
}
