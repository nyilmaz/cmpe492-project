package file;

import beans.twitter.TwitterStreamBean;
import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: nyilmaz
 * Date: 12/12/12
 * Time: 6:58 PM
 */
public class BasicFileReader {

   // we are using this method, if file contents are not very large (ie small enough not to overflow memory)
   public static List<String> getFileContentsByLine(File file) throws IOException {

      BufferedReader reader = new BufferedReader(new java.io.FileReader(file));
      String line;
      List<String> contents = new ArrayList<String>();

      while((line = reader.readLine()) != null){
         contents.add(line);
      }

      return contents;
   }

   /**
    * reads tweets one by one at a line and creates userid set (not duplicate userids)
    * @return
    */
   public static Set<Integer> readTweetsFromFile(File inputFile) throws IOException{

      BufferedReader reader = new BufferedReader(new java.io.FileReader(inputFile));
      String line;
      Gson gson = new Gson();
      Set<Integer> userIdSet = new HashSet<Integer>();
      while((line = reader.readLine()) != null){
         TwitterStreamBean bean = gson.fromJson(line, TwitterStreamBean.class);
         userIdSet.add(bean.getUser().getId());
      }
      return userIdSet;
   }

   public static void main(String[] args) throws IOException {
      File f = new File("/media/SAMSUNG/data/yilbasi/threaded_deneme");
      BufferedReader br = new BufferedReader(new java.io.FileReader(f));
      String line = null;
      long count = 0;
      while((line = br.readLine())!=null){
         count++;
      }
      System.out.println(count);
   }


}
