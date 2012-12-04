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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: nyilmaz
 * Date: 10/3/12
 * Time: 4:42 PM
 */
public class WebInterface implements Runnable, Initializable{

   private static Logger logger = LoggerFactory.getLogger(WebInterface.class);

   private Properties properties;
   private Properties optionalParameters;
   private BufferedReader bufferedReader;
   private volatile List<TwitterStreamBean> twitterStreamBeans;
   private boolean disconnectFlag = false;
   protected volatile BigInteger tweetCount;



   @Override
   @SuppressWarnings("unchecked")
   public WebInterface initialize(Properties... properties) throws InitializationException {
      if(properties == null || properties.length < 2 || properties[0] == null || properties[1] == null)
         throw new NullPropertiesException();
      this.properties = properties[0];
      this.optionalParameters = properties[1];
      return this;
   }

   public WebInterface(){

   }


   public WebInterface getInstance() throws NullPropertiesException{
      if(properties == null || optionalParameters == null)
         throw new NullPropertiesException();
      return new WebInterface(properties, optionalParameters);
   }

   private WebInterface(Properties properties, Properties optionalParameters){
      this.properties = properties;
      this.optionalParameters = optionalParameters;
   }

   public void connect() throws IOException {

      tweetCount = BigInteger.ZERO;
      OAuthHeader header = new OAuthHeader(properties, optionalParameters);

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
         httpPost.setHeader("Authorization", oauth);
         UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params);
         httpPost.setEntity(urlEncodedFormEntity);

      } catch(Exception e) {
         logger.error("Error occured while creating authorization header... Exiting", e);
         System.exit(-1);
      }

      twitterStreamBeans = Collections.synchronizedList(new ArrayList<TwitterStreamBean>());
      SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy_hh.mm");
      File f = new File(properties.getProperty("data_file") + sdf.format(new Date()));
      FileOutputStream fos = null;
      try {
         f.createNewFile();
         fos = new FileOutputStream(f, true);
      } catch(IOException e) {
         e.printStackTrace();
      }

      try {
         HttpResponse httpResponse = httpClient.execute(httpPost);
         HttpEntity httpEntity = httpResponse.getEntity();
         ///
         bufferedReader = new BufferedReader(new InputStreamReader(httpEntity.getContent()));

         Gson gson = new Gson();
         //fos = new FileOutputStream(new File("./data/data"), true);

         while((line = bufferedReader.readLine()) != null && !disconnectFlag){
            //System.out.println(line);
//            twitterStreamBeans.add(gson.fromJson(line, TwitterStreamBean.class));
//            if(twitterStreamBeans.size() > 10)
//               break;
            tweetCount = tweetCount.add(BigInteger.ONE);
            fos.write(line.concat("\n").getBytes());
            //System.out.println(line);


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

   public BufferedReader getBufferedReader(){
      if(bufferedReader == null)
         throw new NullPointerException("Please first connect, before getting reader.");
      return bufferedReader;
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

   public static void main(String[] args) throws IOException {
      File f = new File("/media/SAMSUNG/data/data_usa2");
      File f2 = new File("/home/px5x2/data/coordinates_usa2");
      FileOutputStream fos = new FileOutputStream(f2, true);


      BufferedReader br = new BufferedReader(new FileReader(f));
//      BufferedReader br = Files.newBufferedReader();
      String line = "";
      Gson gson = new Gson();
      int ctr = 0;
      long rate = 0l;
      int rateCtr = 0;
      long totalRate = 0l;
      int malformed = 0;
      long lastTime = 0l;
      int obamaCount = 0;
      int romneyCount = 0;
      fos.write("longitude,latitude,tweet\n".getBytes());
      final String ESC = "\033[";
      while( (line = br.readLine()) != null ){
         if(ctr == 100000)
            break;
         try{

            TwitterStreamBean bean = gson.fromJson(line, TwitterStreamBean.class);
            if(bean.getCoordinates() != null){
               fos.write(bean.getCoordinates().getCoordinates2().concat(bean.getText()+"\n").getBytes());
               ctr++;
               rate++;

               long curtime = System.currentTimeMillis();
               if(curtime - lastTime > 1000){
                  rateCtr++;
                  totalRate += rate;
                  rate = (totalRate)/rateCtr;
                  System.out.println(rate + " Tweets/sec --> " + totalRate + " - " + rateCtr);

                  lastTime = curtime;
                  rate = 0;

               }
            }
         }catch(JsonSyntaxException e){
            malformed++;
         }

      }
      System.out.println("total:"+ctr);
      System.out.println("malformed:"+malformed);

   }

   /*public static void main(String[] args) throws IOException {
      File f = new File("/media/SAMSUNG/data/istanbul");
      BufferedReader br = new BufferedReader(new FileReader(f));
      String line = "";
      Set<Integer> set = new HashSet<Integer>();
      Set<String> set2 = new HashSet<String>();

      Gson gson = new Gson();

      int ctr = 0;
      while( (line = br.readLine()) != null ){
         TwitterStreamBean tsb = gson.fromJson(line, TwitterStreamBean.class);
         Integer userId = tsb.getUser().getId();
         String twId = tsb.getId();
         if(!set2.add(twId)){
            System.out.println("Duplicate tweet found!");
         }
         if(!set.add(userId)){
            System.out.println("Duplicate user found!");

         }
         ctr++;

      }
      System.out.println(set.size());
      System.out.println(set2.size());
      System.out.println(ctr);
   }*/


}


