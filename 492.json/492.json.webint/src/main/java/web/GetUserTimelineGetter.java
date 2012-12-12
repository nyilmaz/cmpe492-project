package web;

import constants.ProgramConstants;
import exceptions.InitializationException;
import exceptions.NullPropertiesException;
import exceptions.PropertyNotFoundException;
import file.BasicFileReader;
import file.FileSupportConstants;
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
import java.util.List;
import java.util.Properties;

/**
 * User: nyilmaz
 * Date: 12/12/12
 * Time: 5:56 PM
 */
public class GetUserTimelineGetter extends WebInterface {

   private static Logger logger = LoggerFactory.getLogger(GetUserTimelineGetter.class);

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
      return this;
   }

   @Override
   @SuppressWarnings("unchecked")
   protected GetUserTimelineGetter getInstance() throws NullPropertiesException {
      if(properties == null || optionalParameters == null)
         throw new NullPropertiesException();
      return new GetUserTimelineGetter(properties, optionalParameters);
   }

   public void connect() throws IOException{

      int tweetCountPerUser = Integer.parseInt(properties.getProperty(ProgramConstants.tweet_count_per_user.name()));
      // get user ids from file
      List<String> userIds = BasicFileReader.getFileContentsByLine(new File(
         properties.getProperty(FileSupportConstants.user_id_file.name())));

      File outFile = new File("/home/px5x2/Documents/user_tweets");
      outFile.createNewFile();
      FileOutputStream outputStream = new FileOutputStream(outFile);


      // make get requests to retrieve user specific tweets in a for loop
      for(String userIdStr : userIds){
         if(disconnectFlag){
            break;
         }
         optionalParameters.put("count", properties.getProperty(ProgramConstants.tweet_count_per_user.name()));
         optionalParameters.put("user_id", userIdStr);

         int userId = Integer.parseInt(userIdStr);
         // prepare for HTTP Get request
         // create OAuth params and http client
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
            while((line = reader.readLine()) != null){
               outputStream.write((line + "\n").getBytes());
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
         }

      }
      // close the final output stream to which we have written user specific tweets
      outputStream.close();



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
