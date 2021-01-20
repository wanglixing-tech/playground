package crs.fcl.integration.main;

import java.util.logging.*;
 
public class PrintLoggerInfo {
   private static final Logger logger = Logger.getLogger(PrintLoggerInfo.class.getName());
 
   public static void main(String[] args) {
      System.out.println("This logger's level is " + logger.getLevel());   // null
      System.out.println("This logger's filter is " + logger.getFilter()); // null
      System.out.println("Parent class is " + logger.getParent());         // RootLogger
      System.out.println("Parent classname is " + logger.getParent().getName()); // ""
 
      Logger root = Logger.getLogger("");
      System.out.println("Root logger's level is " + root.getLevel());   // INFO
      System.out.println("Root logger's filter is " + root.getFilter()); // null
      Handler[] handlers = root.getHandlers();
      for (Handler h : handlers) {
         System.out.println("Handler is " + h);                     // ConsoleHandler
         System.out.println("Handler's level is " + h.getLevel());  // INFO
         System.out.println("Handler's filter is " + h.getFilter()); // null
         System.out.println("Handler's formatter is " + h.getFormatter()); // SimpleFormatter
      }
   }
}