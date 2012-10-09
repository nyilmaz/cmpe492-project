import oauth.signpost.OAuth;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * User: nyilmaz
 * Date: 10/4/12
 * Time: 12:55 AM
 */
public class OAuthHeader {

   private static Logger logger = LoggerFactory.getLogger(ConfigurationLoader.class);

   private final Properties properties;
   private SortedMap<String, String> parameterMap;

   OAuthHeader(Properties properties){

      this.properties = properties;
      parameterMap = new TreeMap<String, String>();
   }

   private String createParameterString(Map<String, String> optionalParams){

      if(optionalParams == null){
         optionalParams = Collections.emptyMap();
      }

      // put required parameters
      for(OauthConstants constant : OauthConstants.values()){
         String key = OAuth.percentEncode(constant.name());
         String value = OAuth.percentEncode(properties.getProperty(constant.name()));
         parameterMap.put(key, value);
      }

      // put optional parameters
      for(String key_ : optionalParams.keySet()){
         String key = OAuth.percentEncode(key_);
         String value = OAuth.percentEncode(properties.getProperty(key_));
         parameterMap.put(key, value);
      }

      parameterMap.putAll(optionalParams);

      StringBuilder builder = new StringBuilder();

      for(String s : parameterMap.keySet()){
         builder.append(s).append('=').append(parameterMap.get(s)).append("&");
      }
      builder.setLength(builder.lastIndexOf("&"));
      return builder.toString();

   }

   public String createSignatureBaseString(String requestMethod, String baseUrl, String parameterString){

      StringBuilder outputString = new StringBuilder(requestMethod.toUpperCase());
      outputString.append("&").append(OAuth.percentEncode(baseUrl)).append("&").append(OAuth.percentEncode(parameterString));

      return outputString.toString();
   }

   private String createSigningKey(){
      return OAuth.percentEncode(properties.getProperty("consumer_secret"))
         .concat(OAuth.percentEncode("oauth_token_secret"));

   }

   private String calculateSigningKey(String signatureBaseString, String signingKey){
      byte[] result = null;
      try {
         String algoritm = properties.getProperty("enc_algorithm");
         Mac mac = Mac.getInstance(algoritm);
         SecretKeySpec keySpec = new SecretKeySpec(signingKey.getBytes(), algoritm);
         mac.init(keySpec);

         result = Base64.encodeBase64(mac.doFinal(signatureBaseString.getBytes()));
      } catch(NoSuchAlgorithmException e) {
         logger.error("No such algoritm: HmacSHA1", e);
      } catch(InvalidKeyException e) {
         logger.error("Error while initializing key spec, signingKey:" + signingKey, e);
      }

      return result == null ? null : new String(result);
   }

   public String getAuthorizationHeaderString() throws Exception {
      StringBuilder builder = new StringBuilder();
      builder.append("OAuth ");
      for(HeaderParameters param : HeaderParameters.values()){
         String key = OAuth.percentEncode(param.name());
         String value = OAuth.percentEncode(properties.getProperty(param.name()));
         if(param.equals(HeaderParameters.oauth_timestamp)){
            value = OAuth.percentEncode(String.valueOf(System.currentTimeMillis()));
         }
         if(param.equals(HeaderParameters.oauth_signature)){
            String method = properties.getProperty(ProgramConstants.http_method.name());
            String baseUrl = properties.getProperty(ProgramConstants.base_url.name());

            value = OAuth.percentEncode(calculateSigningKey(createSignatureBaseString(method, baseUrl, createParameterString(null)), createSigningKey()));
         }

         if(value == null)
            throw new Exception("Property \"" + key + "\" not found in config file, did you forget to add?");

         builder.append(key).append("=").append("\"").append(value).append("\"").append(", ");
      }
      builder.setLength(builder.lastIndexOf(","));
      return builder.toString();
   }

   public static void main(String[] args) {

//      ConfigurationLoader cl = new ConfigurationLoader();
//      Properties properties = cl.initialize().getProperties();
//      OAuthHeader o = new OAuthHeader(properties);
//      System.out.println(o.createEncodedUrl("post", properties.getProperty("base_url"), o.createParameterString(null)));
      byte [] asd = null;
      System.out.println(new String(asd));

   }


}
