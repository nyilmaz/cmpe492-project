import javax.persistence.EntityManager;
import javax.persistence.Persistence;

/**
 * User: nyilmaz
 * Date: 10/30/12
 * Time: 11:17 PM
 */
public class Test {


   public static void main(String[] args) {
      EntityManager em = Persistence.createEntityManagerFactory("492.persistence").createEntityManager();
      em.getTransaction().begin();
      em.persist(new TestEntity(1, "asd"));
      em.persist(new TestEntity(2, "sdfdsf"));
      em.getTransaction().commit();
      em.close();
   }
}
