package file;

import beans.twitter.TwitterBean;
import com.google.common.base.Function;
import com.google.common.collect.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;

/**
 * User: nyilmaz
 */
public class UserTweetsParser implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(UserTweetsParser.class);
    private File inputFile;
    Set<Integer> wholeList;


    public UserTweetsParser(File inputFile, Set<Integer> wholeList) {

        this.inputFile = inputFile;
        this.wholeList = wholeList;
    }

    @Override
    public void run() {


        try {
            long start = System.currentTimeMillis();
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));

            Gson gson = new Gson();
            Type collectionType = new TypeToken<List<TwitterBean>>(){}.getType();
            List<TwitterBean> userTweets = null;
            ListMultimap<Integer, TwitterBean> tempMap = ArrayListMultimap.create(6000, 180);

            Set<Integer> tempSet = new TreeSet<Integer>();

            String line;

            while((line = reader.readLine()) != null){
                userTweets = gson.fromJson(line, collectionType);
//                tempMap.putAll(userTweets.get(0).getUser().getId(), FluentIterable.from(userTweets).toSortedImmutableList(Ordering.<TwitterBean>natural()));
//                if(!userTweets.isEmpty())
//                    tempMap.putAll(userTweets.get(0).getUser().getId(), userTweets);
                if(!userTweets.isEmpty()){

                    if(!tempSet.add(userTweets.get(0).getUser().getId())){
                        logger.info("Duplicate detected.");
                    }
                }
            }


            synchronized (wholeList){
                wholeList.addAll(tempSet);
            }
            logger.info(tempSet.size()+"");
//            logger.info("File " + inputFile.getName() + " finished, "
//                    + tempMap.size() + " users put into main map in [" + (System.currentTimeMillis() - start)/1000 + "] seconds.");

        }catch (Exception ex){
            logger.error("Exception occured! ", ex);
        }

    }

    public static void main(String[] args) {
        Multimap<Integer, String> map = ArrayListMultimap.create();
        map.put(1, "23");
        map.put(1, "34");
        map.put(2, "22");
        System.out.println(map.toString());
    }
}
