package file;

import java.io.*;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * User: nyilmaz
 * Date: 12/12/12
 * Time: 6:58 PM
 */
public class BasicFileReader {

   // we are using this method, if file contents are not very large (ie small enough not to overflow memory)
   public static List<String> getFileContentsByLine(File file) throws IOException {

      BufferedReader reader = new BufferedReader(new FileReader(file));
      String line;
      List<String> contents = new ArrayList<String>();

      while((line = reader.readLine()) != null){
         contents.add(line);
      }

      return contents;
   }


}
