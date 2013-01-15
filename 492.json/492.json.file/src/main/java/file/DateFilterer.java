package file;

import beans.twitter.TwitterBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ProjectUtil;

import java.io.*;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * User: nyilmaz
 */
public class DateFilterer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(UserTweetsParser.class);


    private File inputFile;
    private File outputFile;
    private DateTime minDate;
    private DateTime maxDate;

    public DateFilterer(File inputFile, File outputFile, DateTime minDate, DateTime maxDate) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.minDate = minDate;
        this.maxDate = maxDate;
    }

    @Override
    public void run() {

        try {
            long start = System.currentTimeMillis();
            long lineCount = 0l;
            long ignoredCount = 0l;
            long nonlocationed = 0l;
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            FileOutputStream outputStream = new FileOutputStream(outputFile, true);

            String line;
            Gson gson = new Gson();
            Type collectionType = new TypeToken<List<TwitterBean>>(){}.getType();
            List<TwitterBean> userTweets;

            while((line = reader.readLine()) != null){
                userTweets = gson.fromJson(line, collectionType);
                for(TwitterBean twitterBean : userTweets){
                    long createdDate = ProjectUtil.TWITTER_DATE_FORMAT.get().parse(twitterBean.getCreated_at()).getTime();
                    if(twitterBean.getCoordinates() == null){
                        nonlocationed++;
                        continue;
                    }
                    if(minDate.isBefore(createdDate) && maxDate.isAfter(createdDate)){
                        outputStream.write(createCSVLine(twitterBean, createdDate).getBytes());
                        lineCount++;
                        continue;
                    }
                    ignoredCount++;
                }
            }
            outputStream.close();
            logger.info("File " + inputFile.getName() + " parsed and " +
                    lineCount +" lines written to CSV," +
                    ignoredCount + " tweets ignored, " +
                    nonlocationed + " tweets non-locationed, " +
                    " in[" + (System.currentTimeMillis()-start)/1000 + "] seconds.");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String createCSVLine(TwitterBean twitterBean, long dateCreated) throws ParseException {
        StringBuilder sb = new StringBuilder();

        return sb.append(twitterBean.getId_str())
                .append(",")
                .append(twitterBean.getUser().getId())
                .append(",")
                .append(twitterBean.getCoordinates().getCoordinates()[0])
                .append(",")
                .append(twitterBean.getCoordinates().getCoordinates()[1])
                .append(",")
                .append(ProjectUtil.ISO_DATE_FORMATTER.print(dateCreated))
                .append("\n")
                .toString();
    }

}