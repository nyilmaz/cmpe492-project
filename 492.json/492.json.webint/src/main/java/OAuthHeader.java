import oauth.signpost.OAuth;
import org.apache.commons.codec.binary.Base64;
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
   private static final String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
   private static long timestamp = System.currentTimeMillis()/1000;
   private static String nonce = createNonce();

   OAuthHeader(Properties properties){

      this.properties = properties;
      parameterMap = new TreeMap<String, String>();
   }

   private static String createNonce(){
      Random rnd = new Random(System.currentTimeMillis());
      char[] nonce = new char[32];
      for(int i = 0; i < nonce.length ; i++){
         nonce[i] = characters.charAt(rnd.nextInt(characters.length()));
      }
      return new String(nonce);
   }

   private String createParameterString(Set<String> optionalParams){

      if(optionalParams == null){
         optionalParams = Collections.emptySet();
      }

      // put required parameters
      for(OauthConstants constant : OauthConstants.values()){
         String key = OAuth.percentEncode(constant.name());
         String value = OAuth.percentEncode(properties.getProperty(constant.name()));
         if(constant.equals(OauthConstants.oauth_nonce)){
            value = OAuth.percentEncode(nonce);
         }
         if(constant.equals(OauthConstants.oauth_timestamp)){
            value = OAuth.percentEncode(String.valueOf(timestamp));
         }
         parameterMap.put(key, value);
      }

      // put optional parameters
      for(String key_ : optionalParams){
         String key = OAuth.percentEncode(key_);
         String value = OAuth.percentEncode(properties.getProperty(key_));
         parameterMap.put(key, value);
      }


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
      return OAuth.percentEncode(properties.getProperty("consumer_secret")).concat("&")
         .concat(OAuth.percentEncode(properties.getProperty("oauth_token_secret")));

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

   public String getAuthorizationHeaderString(Set<String> optionalParams) throws Exception {
      StringBuilder builder = new StringBuilder();
      builder.append("OAuth ");
      for(OAuthHeaderParameters param : OAuthHeaderParameters.values()){
         String key = OAuth.percentEncode(param.name());
         String value = OAuth.percentEncode(properties.getProperty(param.name()));
         if(param.equals(OAuthHeaderParameters.oauth_timestamp)){
            value = OAuth.percentEncode(String.valueOf(timestamp));
         }
         if(param.equals(OAuthHeaderParameters.oauth_signature)){
            String method = properties.getProperty(ProgramConstants.http_method.name());
            String baseUrl = properties.getProperty(ProgramConstants.base_url.name());

            value = OAuth.percentEncode(calculateSigningKey(createSignatureBaseString(method, baseUrl, createParameterString(optionalParams)), createSigningKey()));
         }
         if(param.equals(OAuthHeaderParameters.oauth_nonce)){
            value = OAuth.percentEncode(nonce);
         }

         if(value == null)
            throw new Exception("Property \"" + key + "\" not found in config file, did you forget to add?");

         builder.append(key).append("=").append("\"").append(value).append("\"").append(", ");
      }
      builder.setLength(builder.lastIndexOf(","));
      return builder.toString();
   }

   public static void main(String[] args) {

      ConfigurationLoader cl = new ConfigurationLoader();
      Properties properties = cl.initialize().getProperties();
      OAuthHeader o = new OAuthHeader(properties);
      Set<String> map = new TreeSet<String>();
      map.add("track");

      try {
         //System.out.println(o.getAuthorizationHeaderString(map));
         String p = o.createSignatureBaseString("POST", "https://stream.twitter.com/1.1/statuses/filter.json", o.createParameterString(map));
         System.out.println(o.getAuthorizationHeaderString(map));
      } catch(Exception e) {

      }
      //System.out.println("include_entities=true&oauth_consumer_key=xvz1evFS4wEEPTGEFPHBog&oauth_nonce=kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1318622958&oauth_token=370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb&oauth_version=1.0&status=Hello%20Ladies%20%2B%20Gentlemen%2C%20a%20signed%20OAuth%20request%21");
//      System.out.println(o.createEncodedUrl("post", properties.getProperty("base_url"), o.createParameterString(null)));
//      OAuthHeader o = new OAuthHeader()

   }


}
