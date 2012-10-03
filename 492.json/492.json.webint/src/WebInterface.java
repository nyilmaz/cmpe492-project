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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: nyilmaz
 * Date: 10/3/12
 * Time: 4:42 PM
 */
public class WebInterface {

   WebInterface(ConfigurationLoader configurationLoader){

   }



   public InputStream getInputStream(){
      return null;
   }

   public static void main(String[] args) {
      ConfigurationLoader configurationLoader = new ConfigurationLoader();
      WebInterface webInterface = new WebInterface(configurationLoader);

      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost("https://twitter.com/sessions");
      List<NameValuePair> pair = new ArrayList<NameValuePair>();
      pair.add(new BasicNameValuePair("session[username_or_email]", "px2"));
      pair.add(new BasicNameValuePair("session[password]", "cxxolwz"));
      try {
         httpPost.setEntity(new UrlEncodedFormEntity(pair));
         HttpResponse httpResponse = httpClient.execute(httpPost);
//         httpResponse.getStatusLine().getStatusCode()
         System.out.println(httpResponse.getStatusLine());
         HttpEntity httpEntity = httpResponse.getEntity();
         ///
//         BufferedReader br = new BufferedReader(new InputStreamReader(httpEntity.getContent()));
//         String line;
//         while((line=br.readLine())!=null){
//            System.out.println(line);
//         }
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
