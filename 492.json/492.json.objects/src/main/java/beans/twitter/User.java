package beans.twitter;

/**
 * User: nyilmaz
 * Date: 10/30/12
 * Time: 4:06 PM
 */
public class User {
   Integer id;
   String name;

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public boolean equals(Object o) {
      if(this == o) return true;
      if(o == null || getClass() != o.getClass()) return false;

      User user = (User) o;

      if(!id.equals(user.id)) return false;

      return true;
   }

   @Override
   public int hashCode() {
      return id.hashCode();
   }
}
