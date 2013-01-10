package web;

import constants.ProgramConstants;
import exceptions.InitializationException;
import exceptions.NullPropertiesException;
import exceptions.PropertyNotFoundException;
import file.BasicFileReader;
import file.FileSupportConstants;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * User: nyilmaz
 * Date: 12/12/12
 * Time: 5:56 PM
 */
public class GetUserTimelineGetter extends WebInterface {

   private static Logger logger = LoggerFactory.getLogger(GetUserTimelineGetter.class);

   private Integer totalUsersGot;
   private boolean isSleeping;

   public GetUserTimelineGetter(){}


   protected GetUserTimelineGetter(Properties properties, Properties optionalParameters) {
      super(properties, optionalParameters);
   }

   @Override
   @SuppressWarnings("unchecked")
   public GetUserTimelineGetter initialize(Properties... properties) throws InitializationException {
      if(properties == null || properties.length < 2 || properties[0] == null || properties[1] == null)
         throw new NullPropertiesException();
      this.properties = properties[0];
      this.optionalParameters = properties[1];
      totalUsersGot = 0;
      isSleeping = false;
      return this;
   }

   @Override
   @SuppressWarnings("unchecked")
   protected GetUserTimelineGetter getInstance() throws NullPropertiesException {
      if(properties == null || optionalParameters == null)
         throw new NullPropertiesException();
      return new GetUserTimelineGetter(properties, optionalParameters);
   }

   public synchronized void connect() throws IOException{

      int tweetCountPerUser = Integer.parseInt(properties.getProperty(ProgramConstants.tweet_count_per_user.name()));
      // get user ids from file
      List<String> userIds = BasicFileReader.getFileContentsByLine(new File(
         properties.getProperty(FileSupportConstants.user_id_file.name())));

      File outFile = new File("/media/SAMSUNG/data/yilbasi/yilbasi_user_tweets_200_per_user");
      outFile.createNewFile();
      FileOutputStream outputStream = new FileOutputStream(outFile);

      // make get requests to retrieve user specific tweets in a for loop
      for(String userIdStr : userIds){
         long begintime = System.currentTimeMillis();
         Integer limitLeft = 0;
         if(disconnectFlag){
            break;
         }
         optionalParameters.put("count", properties.getProperty(ProgramConstants.tweet_count_per_user.name()));
         optionalParameters.put("user_id", userIdStr);

         int userId = Integer.parseInt(userIdStr);
         // prepare for HTTP Get request
         // create OAuth params and http client

         HttpClient httpClient = new DefaultHttpClient();
         try {
            OAuthHeader header = new OAuthHeader(properties, optionalParameters);
            String oauthHeader = header.getAuthorizationHeaderString();
            HttpGet httpGet = new HttpGet(new URI(createRequestURL(oauthHeader, userId, tweetCountPerUser)));
            httpGet.setHeader("Authorization", oauthHeader);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();

            BufferedReader reader = new BufferedReader(new InputStreamReader(httpEntity.getContent()));
            String line;
            while((line = reader.readLine()) != null){
               outputStream.write((line + "\n").getBytes());
            }

            Header[] headers = httpResponse.getAllHeaders();
            for(Header header1 : headers){
               if(header1.getName().equals("X-Rate-Limit-Remaining")){
                  limitLeft = Integer.valueOf(header1.getValue());
               }
            }

            // release user specific http get resources
            reader.close();
            EntityUtils.consume(httpEntity);
            httpGet.releaseConnection();

         } catch(URISyntaxException e) {
            logger.error("HTTP GET Url cannot be constructed. Exiting...", e);
            System.exit(-1);
         } catch(PropertyNotFoundException e) {
            e.printStackTrace();
         } catch(UnknownHostException e){
            FileOutputStream fos = new FileOutputStream(new File("/media/SAMSUNG/data/derbi/remaining_userIds"), true);
            fos.write((userIdStr + "\n").getBytes());
            logger.warn("Connection failed, userId:" + userIdStr + " added to remaining list.");
            continue;
         }
         logger.info("userId :" + userIdStr + " tweets got in " + (System.currentTimeMillis() - begintime)/1000 + "seconds.");


         // check the rate limit, since we do not want to be banned from twitter API
         totalUsersGot++;
         if(limitLeft == 0){
            try {
               isSleeping = true;
               System.out.println("Rate limit exceeded, Sleeping for 15 minutes...");
               System.out.println("Next wake up will be at: " + new Date(System.currentTimeMillis() + 15l*60l*1000l));
               Thread.sleep(15l*60l*1000l);
            } catch(InterruptedException e) {
               e.printStackTrace();
            }
            isSleeping = false;
         }


      }
      // close the final output stream to which we have written user specific tweets
      outputStream.close();





   }

   public synchronized Integer getTotalUsersGot() {
      return totalUsersGot;
   }

   public void setTotalUsersGot(Integer totalUsersGot) {
      this.totalUsersGot = totalUsersGot;
   }

   public synchronized boolean isSleeping() {
      return isSleeping;
   }

   public void setSleeping(boolean sleeping) {
      isSleeping = sleeping;
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
