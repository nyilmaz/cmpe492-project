package main;

import exceptions.InitializationException;
import file.BasicFileReader;
import file.DataFileOperations;
import loader.ConfigurationLoader;
import web.PostStreamGetter;
import web.UserTimelineGetter;
import web.WebInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * User: nyilmaz
 * Date: 10/3/12
 * Time: 4:39 PM
 */
public class Main {


   private static ConfigurationLoader conf;

   double rate = 0.0;
   int total = 0;
   int iteration = 0;
   int lastTotal = 0;

   public static void main(String[] args) {

      final Main main = new Main();
      try {
         conf = new ConfigurationLoader().initializeConfiguration();
         final WebInterface webInterface = new PostStreamGetter().initialize(conf.getProperties(), conf.getOptionalParameters());
//         final GetUserTimelineGetter webInterface = new GetUserTimelineGetter().initialize(conf.getProperties(), conf.getOptionalParameters());
//         Timer timer = new Timer();
//         timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                 main.printTweetCount(webInterface);
////               if(!webInterface.isSleeping())
////                  System.out.println(webInterface.getTotalUsersGot());
//            }
//         }, 5000, 10000);
//         webInterface.connect();
            //createUserIdFile();
//         FileReader fileReader = new FileReader(conf.getProperties());
//         fileReader.run();
//         GetUserTimelineGetter getUserTimelineGetter = new GetUserTimelineGetter()
//            .initialize(conf.getProperties(), conf.getOptionalParameters());
//         getUserTimelineGetter.connect();

         List<Integer> userIdList = Collections.synchronizedList(new ArrayList<Integer>());
         BufferedReader br  = new BufferedReader(new java.io.FileReader("/media/SAMSUNG/data/yilbasi/yilbasi_user_ids"));
         String line;
         while((line = br.readLine()) != null){
            userIdList.add(Integer.valueOf(line));
         }
         br.close();
         UserTimelineGetter userTimelineGetter = new UserTimelineGetter(new File("/media/SAMSUNG/data/yilbasi/threaded_deneme"),
                                                                        userIdList,
                                                                        conf.getProperties(),
                                                                        conf.getOptionalParameters());
         userTimelineGetter.run();

      } catch(InitializationException e) {
         e.printStackTrace();
      } /*catch(IOException e) {
         e.printStackTrace();
      }*/ catch(FileNotFoundException e) {
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      } catch(IOException e) {
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      } catch(InterruptedException e) {
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }


   }
   public synchronized void printTweetCount(WebInterface webInterface){
      total = webInterface.getTweetCount().intValue();
      if(total == lastTotal){
         webInterface.disconnect();
      }
      iteration++;
      System.out.println("Total :" + total);
      System.out.println("Rate :" + (double)total/(double)(iteration*10) + "tweets/sec");
      System.out.println("----------------");
   }

   public static void createUserIdFile() throws IOException {
      File inputFile = new File("/media/SAMSUNG/data/derbi/istanbul_16.12.2012_04.21");
      DataFileOperations.createUserIdFile(BasicFileReader.readTweetsFromFile(inputFile), new File("/home/px5x2/Documents/derbi_user_ids"));
   }
}
