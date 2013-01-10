package web;

import constants.ProgramConstants;
import exceptions.InitializationException;
import exceptions.NullPropertiesException;
import file.FileSupportConstants;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * User: nyilmaz
 * Date: 12/12/12
 * Time: 5:55 PM
 */
public class PostStreamGetter extends WebInterface{

   private static Logger logger = LoggerFactory.getLogger(PostStreamGetter.class);

   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy_hh.mm");

   public PostStreamGetter(){}

   private PostStreamGetter(Properties properties, Properties optionalParameters) {
      super(properties, optionalParameters);
   }

   @Override
   @SuppressWarnings("unchecked")
   public PostStreamGetter initialize(Properties... properties) throws InitializationException {

      if(properties == null || properties.length < 2 || properties[0] == null || properties[1] == null)
         throw new NullPropertiesException();
      this.properties = properties[0];
      this.optionalParameters = properties[1];
      return this;
   }

   @Override
   @SuppressWarnings("unchecked")
   protected PostStreamGetter getInstance() throws NullPropertiesException {
         if(properties == null || optionalParameters == null)
            throw new NullPropertiesException();
         return new PostStreamGetter(properties, optionalParameters);
   }

   public void connect() throws IOException{

      // prepare HTTP post requirements
      // create a client and a post request
      OAuthHeader header = new OAuthHeader(properties, optionalParameters);
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(properties.getProperty(ProgramConstants.base_url.name()));

      // add optional parameters
      List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
      for(String parameter : optionalParameters.stringPropertyNames()){
         params.add(new BasicNameValuePair(parameter, optionalParameters.getProperty(parameter)));
      }

      try{
         // set OAuth header and post parameters
         String oauth = header.getAuthorizationHeaderString();
         httpPost.setHeader("Authorization", oauth);
         httpPost.setEntity(new UrlEncodedFormEntity(params));


      }catch(Exception e){
         logger.error("Error occured while creating authorization header... Exiting", e);
         System.exit(-1);
      }

      // prepare file output
      // file name suffix is date_now
      File outputFile = new File(properties.getProperty(FileSupportConstants.data_file.name()) + DATE_FORMAT.format(new Date()));
      FileOutputStream outputStream;
      outputFile.createNewFile();
      outputStream = new FileOutputStream(outputFile, true);

      HttpResponse httpResponse = httpClient.execute(httpPost);
      HttpEntity httpEntity = httpResponse.getEntity();

      BufferedReader reader = new BufferedReader(new InputStreamReader(httpEntity.getContent()));
      String line;

      // read stream from httpEntity and write tweets to outputFile
      while((line = reader.readLine()) != null && !disconnectFlag){
         outputStream.write(line.concat("\n").getBytes());
         tweetCount = tweetCount.add(BigInteger.ONE);
      }

      // release resources
      EntityUtils.consume(httpEntity);
      reader.close();
      outputStream.close();
      httpPost.releaseConnection();

   }

}
