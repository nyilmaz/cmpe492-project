package main;

import exceptions.InitializationException;
import loader.ConfigurationLoader;
import web.GetUserTimelineGetter;
import web.WebInterface;

import java.io.IOException;


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
         //final WebInterface webInterface = new WebInterface().initialize(conf.getProperties(), conf.getOptionalParameters());
//         Timer timer = new Timer();
//         timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//               main.printTweetCount(webInterface);
//            }
//         }, 5000, 10000);
         //webInterface.connect();
//         FileReader fileReader = new FileReader(conf.getProperties());
//         fileReader.run();
         GetUserTimelineGetter getUserTimelineGetter = new GetUserTimelineGetter()
            .initialize(conf.getProperties(), conf.getOptionalParameters());
         getUserTimelineGetter.connect();


      } catch(InitializationException e) {
         e.printStackTrace();
      } catch(IOException e) {
         e.printStackTrace();
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
}
