package web;

import beans.twitter.TwitterStreamBean;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import constants.ProgramConstants;
import exceptions.InitializationException;
import exceptions.NullPropertiesException;
import interfaces.Initializable;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: nyilmaz
 * Date: 10/3/12
 * Time: 4:42 PM
 */
public abstract class WebInterface implements Runnable, Initializable{

   private static Logger logger = LoggerFactory.getLogger(WebInterface.class);

   protected Properties properties;
   protected Properties optionalParameters;
   private BufferedReader bufferedReader;
   private volatile List<TwitterStreamBean> twitterStreamBeans;
   protected boolean disconnectFlag = false;
   protected volatile BigInteger tweetCount = BigInteger.ZERO;

   protected WebInterface() {}


   @Override
   public abstract <T extends Initializable> T initialize(Properties... properties) throws InitializationException;


   protected abstract  <T extends WebInterface> T getInstance() throws NullPropertiesException;


   protected WebInterface(Properties properties, Properties optionalParameters){
      this.properties = properties;
      this.optionalParameters = optionalParameters;
   }

   public void connect() throws IOException {

      OAuthHeader header = new OAuthHeader(properties, optionalParameters);

      HttpClient httpClient2 = new DefaultHttpClient();
      HttpGet httpGet = new HttpGet();

      HttpClient httpClient = new DefaultHttpClient();
      String requestMethod = properties.getProperty(ProgramConstants.http_method.name());

      HttpPost httpPost = new HttpPost(properties.getProperty(ProgramConstants.base_url.name()));
      String line = "";

      List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

      for(String parameter : optionalParameters.stringPropertyNames()){
         params.add(new BasicNameValuePair(parameter, optionalParameters.getProperty(parameter)));
      }

      try {


         String oauth = header.getAuthorizationHeaderString();
         String oauthParamString = OAuthHeader.getOAuthParameter(oauth);
         httpGet.setURI(new URI("https://api.twitter.com/1.1/statuses/user_timeline.json?"+oauthParamString+"&user_id=183193188&count=3"));
         httpGet.setHeader("Authorization", oauth);
         httpPost.setHeader("Authorization", oauth);
         UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params);
         httpPost.setEntity(urlEncodedFormEntity);

      } catch(Exception e) {
         logger.error("Error occured while creating authorization header... Exiting", e);
         System.exit(-1);
      }

      twitterStreamBeans = Collections.synchronizedList(new ArrayList<TwitterStreamBean>());
      SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy_hh.mm");
      File f = new File("/home/px5x2/Documents/deneme");
//      File f = new File(properties.getProperty("data_file") + sdf.format(new Date()));
      FileOutputStream fos = null;
      try {
         f.createNewFile();
         fos = new FileOutputStream(f, true);
      } catch(IOException e) {
         e.printStackTrace();
      }

      try {
         HttpResponse httpResponse = httpClient2.execute(httpGet);
         HttpEntity httpEntity = httpResponse.getEntity();
         ///
         bufferedReader = new BufferedReader(new InputStreamReader(httpEntity.getContent()));

         Gson gson = new Gson();

         while((line = bufferedReader.readLine()) != null && !disconnectFlag){

            tweetCount = tweetCount.add(BigInteger.ONE);
            fos.write(line.concat("\n").getBytes());


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
         fos.close();
      }

   }

   public void disconnect(){
      disconnectFlag = true;
   }



   public synchronized BigInteger getTweetCount() {
      return tweetCount;
   }

   @Override
   public void run() {
      try {
         connect();
      } catch(IOException e) {
         e.printStackTrace();
      }
   }

//   public static void main(String[] args) throws IOException {
//      File f = new File("/media/SAMSUNG/data/data_usa2");
//      File f2 = new File("/home/px5x2/data/coordinates_usa2");
//      FileOutputStream fos = new FileOutputStream(f2, true);
//
//
//      BufferedReader br = new BufferedReader(new FileReader(f));
////      BufferedReader br = Files.newBufferedReader();
//      String line = "";
//      Gson gson = new Gson();
//      int ctr = 0;
//      long rate = 0l;
//      int rateCtr = 0;
//      long totalRate = 0l;
//      int malformed = 0;
//      long lastTime = 0l;
//      int obamaCount = 0;
//      int romneyCount = 0;
//      fos.write("longitude,latitude,tweet\n".getBytes());
//      final String ESC = "\033[";
//      while( (line = br.readLine()) != null ){
//         if(ctr == 100000)
//            break;
//         try{
//
//            TwitterStreamBean bean = gson.fromJson(line, TwitterStreamBean.class);
//            if(bean.getCoordinates() != null){
//               fos.write(bean.getCoordinates().getCoordinates2().concat(bean.getText()+"\n").getBytes());
//               ctr++;
//               rate++;
//
//               long curtime = System.currentTimeMillis();
//               if(curtime - lastTime > 1000){
//                  rateCtr++;
//                  totalRate += rate;
//                  rate = (totalRate)/rateCtr;
//                  System.out.println(rate + " Tweets/sec --> " + totalRate + " - " + rateCtr);
//
//                  lastTime = curtime;
//                  rate = 0;
//
//               }
//            }
//         }catch(JsonSyntaxException e){
//            malformed++;
//         }
//
//      }
//      System.out.println("total:"+ctr);
//      System.out.println("malformed:"+malformed);
//
//   }

   public static void main(String[] args) {
      Date date = new Date(1355349091000l);
      System.out.println(date);
      System.out.println(System.currentTimeMillis());
   }


}


