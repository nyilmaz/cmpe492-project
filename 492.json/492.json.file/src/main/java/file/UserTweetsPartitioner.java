package file;

import beans.twitter.LightTwitterBean;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ProjectUtil;

import java.io.*;
import java.io.FileReader;

/**
 * User: nyilmaz
 */
public class UserTweetsPartitioner implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(UserTweetsPartitioner.class);


    File inputFile;
    String outputFilePrefix;

    public UserTweetsPartitioner(File inputFile, String outputFilePrefix) {
        this.inputFile = inputFile;
        this.outputFilePrefix = outputFilePrefix;
    }

    @Override
    public void run() {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line;
            SortedSetMultimap<Long, LightTwitterBean> tweets = TreeMultimap.create();

            while((line = reader.readLine()) != null){
                LightTwitterBean bean = createLightTwitterBeanFromCSV(line);
                tweets.put(bean.getUserId(), bean);
            }

            logger.info("UserCount: {}", tweets.keySet().size());
            logger.info("TweetCount: {}", tweets.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private LightTwitterBean createLightTwitterBeanFromCSV(String csvLine){
        String[] parts = csvLine.split(",");
        LightTwitterBean twitterBean = new LightTwitterBean();

        twitterBean.setId(Long.valueOf(parts[0]));
        twitterBean.setUserId(Long.valueOf(parts[1]));
        twitterBean.setLat(Double.valueOf(parts[2]));
        twitterBean.setLon(Double.valueOf(parts[3]));
        twitterBean.setCreateDate(ProjectUtil.ISO_DATE_FORMATTER.parseDateTime(parts[4]));
        return twitterBean;
    }
}
