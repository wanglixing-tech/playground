package crs.fcl.integration.main;

import java.io.IOException;
import java.util.logging.*;
 
public class TestMyHtmlFormatter {
   private final static Logger logger = Logger.getLogger(TestMyHtmlFormatter.class.getName());
 
   // Create a FileHandler and attach it to the root logger.
   // Create a MyHtmlFormatter and attach to the FileHandler.
   public static void setupLogger() throws IOException {
      Logger rootLogger = Logger.getLogger("");
      Handler htmlFileHandler = new FileHandler("%h/log.html");
      Formatter htmlFormatter = new MyHtmlFormatter();
      rootLogger.addHandler(htmlFileHandler);
      htmlFileHandler.setFormatter(htmlFormatter);
   }
 
   public void writeLog() {
      logger.setLevel(Level.ALL);
      logger.severe("This is a SEVERE-level log");
      logger.warning("This is a WARNING-level log");
      logger.info("This is a INFO-level log");
      logger.finest("This is a FINEST-level log");
      try {
         // Simulating Exceptions
         throw new Exception("Simulating an exception");
      } catch (Exception ex){
         logger.log(Level.SEVERE, ex.getMessage(), ex);
      }
   }
 
   public static void main(String[] args) throws IOException {
      setupLogger();
      TestMyHtmlFormatter m = new TestMyHtmlFormatter();
      m.writeLog();
   }
}