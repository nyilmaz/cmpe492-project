package web;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * User: nyilmaz
 * Date: 1/2/13
 * Time: 10:02 PM
 */
public class Monitor implements Runnable {

   private ThreadPoolExecutor executor;

   public Monitor(ThreadPoolExecutor executor){
      this.executor = executor;
   }

   @Override
   public void run() {
      try{

         while(!executor.isShutdown()){
            System.out.println(
               String.format("[monitor] [%d/%d] Active: %d, Completed: %d of %d",
               	                        this.executor.getPoolSize(),
               	                        this.executor.getCorePoolSize(),
               	                        this.executor.getActiveCount(),
               	                        this.executor.getCompletedTaskCount(),
               	                        this.executor.getTaskCount()));
            	                Thread.sleep(10000);
         }
      }catch(Exception ex){

      }

   }
}
