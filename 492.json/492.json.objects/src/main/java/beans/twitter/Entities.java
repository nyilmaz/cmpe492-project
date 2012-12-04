package beans.twitter;

/**
 * User: nyilmaz
 * Date: 10/29/12
 * Time: 8:40 AM
 */
public class Entities {

   private URL[] urls;
   private UserMentions[] user_mentions;
   private HashTag[] hashtags;

   public URL[] getUrls() {
      return urls;
   }

   public void setUrls(URL[] urls) {
      this.urls = urls;
   }

   public UserMentions[] getUser_mentions() {
      return user_mentions;
   }

   public void setUser_mentions(UserMentions[] user_mentions) {
      this.user_mentions = user_mentions;
   }

   public HashTag[] getHashtags() {
      return hashtags;
   }

   public void setHashtags(HashTag[] hashtags) {
      this.hashtags = hashtags;
   }
}
