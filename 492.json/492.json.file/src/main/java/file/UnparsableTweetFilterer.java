package file;

import beans.twitter.TwitterBean;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;

/**
 * User: nyilmaz
 */
public class UnparsableTweetFilterer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(UserTweetsParser.class);


    private File inputFile;
    private File outputFile;

    public UnparsableTweetFilterer(File inputFile, File outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    @Override
    public void run() {

        long start = System.currentTimeMillis();
        BufferedReader reader = null;
        FileOutputStream fileOutputStream = null;
        try {
            reader = new BufferedReader(new FileReader(inputFile));
            fileOutputStream = new FileOutputStream(outputFile, true);
            long erroneous = 0l;
            long total = 0l;
            String line;
            Gson gson = new Gson();
            Type collectionType = new TypeToken<List<TwitterBean>>(){}.getType();

            while((line = reader.readLine()) != null){
                total++;
                try{
                    gson.fromJson(line, collectionType);
                }catch (JsonParseException ex){
                    erroneous++;
                    continue;
                }

                fileOutputStream.write(line.getBytes());
                fileOutputStream.write("\n".getBytes());
            }
            fileOutputStream.close();
            logger.info(inputFile.getName() + " finished, total -> [" + total + "], " +
                                "erroneous -> [" + erroneous + "], " +
                                "duration -> [" + (System.currentTimeMillis()-start)/1000 + "] seconds.\n");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
