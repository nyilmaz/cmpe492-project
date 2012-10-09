import oauth.signpost.OAuth;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * User: nyilmaz
 * Date: 10/3/12
 * Time: 4:42 PM
 */
public class WebInterface {

   private static Logger logger = LoggerFactory.getLogger(ConfigurationLoader.class);

   Properties properties;

   WebInterface(Properties properties){
      this.properties = properties;
   }



   public InputStream getInputStream(){
      return null;
   }

   public static void main(String[] args) {
      ConfigurationLoader configurationLoader = new ConfigurationLoader();
      Properties properties = configurationLoader.initialize().getProperties();

      OAuthHeader header = new OAuthHeader(properties);


      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost("https://stream.twitter.com/1.1/statuses/filter.json");
      List<NameValuePair> pair = new ArrayList<NameValuePair>();

      try {
         httpPost.setHeader("Authorization", header.getAuthorizationHeaderString());
      } catch(Exception e) {
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }


      try {
         httpPost.setEntity(new UrlEncodedFormEntity(pair));
         HttpResponse httpResponse = httpClient.execute(httpPost);
//         httpResponse.getStatusLine().getStatusCode()
         System.out.println(httpResponse.getStatusLine());
         HttpEntity httpEntity = httpResponse.getEntity();
         ///
         BufferedReader br = new BufferedReader(new InputStreamReader(httpEntity.getContent()));
         String line;
         while((line=br.readLine())!=null){
            System.out.println(line);
         }
         ////
         EntityUtils.consume(httpEntity);

      } catch(UnsupportedEncodingException e) {
         e.printStackTrace();
      } catch(ClientProtocolException e) {
         e.printStackTrace();
      } catch(IOException e) {
         e.printStackTrace();
      }finally {
         httpPost.releaseConnection();
      }

   }

}
