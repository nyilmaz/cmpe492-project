import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * User: nyilmaz
 * Date: 10/30/12
 * Time: 11:17 PM
 */

@Entity
@Table(name = "test")
public class TestEntity {

   private Integer id;
   private String deneme;

   TestEntity(){}

   TestEntity(Integer id, String deneme){
      this.id = id;
      this.deneme = deneme;
   }

   @Id
   @Column(name = "id")
   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   @Column(name = "deneme")
   public String getDeneme() {
      return deneme;
   }

   public void setDeneme(String deneme) {
      this.deneme = deneme;
   }
}
