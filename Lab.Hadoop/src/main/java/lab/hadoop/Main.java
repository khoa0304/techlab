package lab.hadoop;


import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.net.URI;
import java.util.logging.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
public class Main {

   private static final Logger logger = Logger.getLogger(Main.class.getName());

   public static void main(String[] args) throws Exception {
      //HDFS URI

	  Configuration conf = new Configuration();
      String hdfsuri = "hdfs://144.91.109.48:8020/";
      FileSystem fs = new DistributedFileSystem();

      fs.initialize(new URI(hdfsuri), conf);


      for (FileStatus f :fs.listStatus(new Path("/")))
      {
          System.out.println(f.getPath().getName());                  
      }

      fs.close();
   }
}