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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

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

      HttpPost httpPost = new HttpPost(properties.getProperty(ProgramConstants.base_url.name()));

      List<NameValuePair> pair = new ArrayList<NameValuePair>();
      BasicNameValuePair[] params = {new BasicNameValuePair("track", "twitter")};

      Set<String> set = new TreeSet<String>();
      set.add("track");

      try {
         String oauth = header.getAuthorizationHeaderString(set);
         httpPost.setHeader("Authorization", oauth);
         UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(Arrays.asList(params));
         httpPost.setEntity(urlEncodedFormEntity);

      } catch(Exception e) {
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }


      try {
         HttpResponse httpResponse = httpClient.execute(httpPost);
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
