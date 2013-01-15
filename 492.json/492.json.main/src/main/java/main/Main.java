package main;

import file.*;
import loader.ConfigurationLoader;
import org.joda.time.DateTime;
import web.WebInterface;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.*;


/**
 * User: nyilmaz
 * Date: 10/3/12
 * Time: 4:39 PM
 */
public class Main {


   private static ConfigurationLoader conf;

   int total = 0;
   int iteration = 0;
   int lastTotal = 0;

   public static void main(String[] args) {

      final Main main = new Main();

      //parseUserTweets();
      //filterErroneousTweets();
      //createCSV();
       partitionTweets();


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

    public static void parseUserTweets(){

        String dir = "C:\\Users\\px5x2\\data\\yilbasi\\tweets";
        final String fileNamePrefix = "tweets";
        String[] fileNames = new File(dir).list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(fileNamePrefix);
            }
        });

        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(fileNames.length, true);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 4, 1l, TimeUnit.SECONDS, queue);
//        ListMultimap<Integer, TwitterBean> tweets = Multimaps.synchronizedListMultimap(ArrayListMultimap.<Integer, TwitterBean>create(90000, 180));
        Set<Integer> tweets = Collections.synchronizedSet(new TreeSet<Integer>());
        for (int i = 2; i < 4; i++) {
            File file = new File(dir + "\\" + fileNamePrefix + i);
            executor.execute(new UserTweetsParser(file, tweets));

        }
//        System.out.println(tweets.size());
        executor.shutdown();
    }

    public static void filterErroneousTweets(){
        String inputDir = "D:\\data\\yilbasi";
        final String inputFileNamePrefix = "threaded_deneme";
        String[] inputFileNames = new File(inputDir).list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(inputFileNamePrefix);
            }
        });
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(inputFileNames.length, true);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 4, 1l, TimeUnit.SECONDS, queue);

        int counter = 1;

        for (String inputFileName : inputFileNames) {
            File inputFile = new File(inputDir + "\\" + inputFileName);
            File outputFile = new File("C:\\Users\\px5x2\\data\\yilbasi\\tweets\\tweets" + counter);
            counter++;
            executor.execute(new UnparsableTweetFilterer(inputFile, outputFile));
        }

        executor.shutdown();


    }

    public static void createCSV(){
        String inputDir = "C:\\Users\\px5x2\\data\\yilbasi\\tweets";
        final String inputFileNamePrefix = "tweets";
        File outputFile = new File("C:\\Users\\px5x2\\data\\yilbasi\\tweets\\csv\\tweets_27dec-5jan.csv");
        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] inputFileNames = new File(inputDir).list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(inputFileNamePrefix);
            }
        });

        DateTime minDate = new DateTime(2012, 12, 27, 0, 0);
        DateTime maxDate = new DateTime(2013, 1, 5, 0, 0);


        for(String fileName : inputFileNames){

            new DateFilterer(new File(inputDir+"\\"+fileName), outputFile, minDate, maxDate).run();
        }


    }

    public static void partitionTweets(){
        File inputFile = new File("C:\\Users\\px5x2\\data\\yilbasi\\tweets\\csv\\tweets_27dec-5jan.csv");
        new UserTweetsPartitioner(inputFile, "").run();
    }
}
