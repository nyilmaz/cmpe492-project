import bean.TwitterStreamBean;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * User: nyilmaz
 * Date: 10/3/12
 * Time: 4:42 PM
 */
public class WebInterface implements Runnable{

   private static Logger logger = LoggerFactory.getLogger(WebInterface.class);

   private Properties properties;
   private Properties optionalParameters;
   private BufferedReader bufferedReader;
   private volatile List<TwitterStreamBean> twitterStreamBeans;
   private boolean disconnectFlag = false;

   public static WebInterface getInstance(Properties properties, Properties optionalParameters){
      return new WebInterface(properties, optionalParameters);
   }

   private WebInterface(Properties properties, Properties optionalParameters){
      this.properties = properties;
      this.optionalParameters = optionalParameters;
   }

   public void connect(){
      OAuthHeader header = new OAuthHeader(properties, optionalParameters);

      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(properties.getProperty(ProgramConstants.base_url.name()));
      String line = "";

      List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

      for(String parameter : optionalParameters.stringPropertyNames()){
         params.add(new BasicNameValuePair(parameter, optionalParameters.getProperty(parameter)));
      }



      try {
         String oauth = header.getAuthorizationHeaderString();
         httpPost.setHeader("Authorization", oauth);
         UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params);
         httpPost.setEntity(urlEncodedFormEntity);

      } catch(Exception e) {
         logger.error("Error occured while creating authorization header... Exiting", e);
         System.exit(-1);
      }

      twitterStreamBeans = Collections.synchronizedList(new ArrayList<TwitterStreamBean>());

      try {
         HttpResponse httpResponse = httpClient.execute(httpPost);
         HttpEntity httpEntity = httpResponse.getEntity();
         ///
         bufferedReader = new BufferedReader(new InputStreamReader(httpEntity.getContent()));

         Gson gson = new Gson();


         while((line = bufferedReader.readLine()) != null && !disconnectFlag){
            //System.out.println(line);
            twitterStreamBeans.add(gson.fromJson(line, TwitterStreamBean.class));
            if(twitterStreamBeans.size() > 10)
               break;
         }
         ////
         for(TwitterStreamBean tsb : twitterStreamBeans){
            System.out.println(tsb);
         }
         EntityUtils.consume(httpEntity);
         bufferedReader.close();

      } catch(UnsupportedEncodingException e) {
         e.printStackTrace();
      } catch(ClientProtocolException e) {
         e.printStackTrace();
      } catch(IOException e) {
         e.printStackTrace();
      }catch(JsonSyntaxException e){
         logger.error("Json parsing failed, line : " + line, e);
      }finally {
         httpPost.releaseConnection();
      }

   }

   public void disconnect(){
      disconnectFlag = true;
   }

   public BufferedReader getBufferedReader(){
      if(bufferedReader == null)
         throw new NullPointerException("Please first connect, before getting reader.");
      return bufferedReader;
   }


   @Override
   public void run() {
      connect();
   }
}


