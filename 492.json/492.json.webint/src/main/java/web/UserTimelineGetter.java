package web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: nyilmaz
 * Date: 1/1/13
 * Time: 9:30 PM
 */
public class UserTimelineGetter {

   private static Logger logger = LoggerFactory.getLogger(UserTimelineGetter.class);


   private File outputFile;
   private List<Integer> userIdList;
   private Properties properties;
   private Properties optionalParameters;
   private AtomicInteger integer;

   private List<Runnable> runnables;

   public UserTimelineGetter(File outputFile, List<Integer> userIdList, Properties properties, Properties optionalParameters) {
      this.outputFile = outputFile;
      this.userIdList = userIdList;
      this.properties = properties;
      this.optionalParameters = optionalParameters;
      integer = new AtomicInteger(180);

   }

   public void run() throws InterruptedException {

      BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(100000, true);
      ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 15*60+1, TimeUnit.SECONDS, queue);
      File[] files = new File[15];
      for(int i = 0; i < 15 ; i++) {
         files[i] = new File("/media/SAMSUNG/data/yilbasi/threaded_deneme"+i);
      }


      Thread monitorThread = new Thread(new Monitor(threadPoolExecutor));
      monitorThread.setDaemon(true);
      monitorThread.start();

      for(int i = 9100 + 49481 + 10989 + 581 + 389; i < userIdList.size(); i++) {
         threadPoolExecutor.execute(new GetUserTimelineV2(properties, optionalParameters, files[i%15], userIdList.get(i), integer));
      }


   }

   public static void main(String[] args) {

   }



}
