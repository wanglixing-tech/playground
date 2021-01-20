package crs.fcl.integration.main;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;
 
/**
 * Create a custom HTML logging Formatter to show the entries
 * in a table with 3 columns: level, time-stamp and message.
 */
public class MyHtmlFormatter extends Formatter {
   // This method is called for every log records to be published.
   // Write an HTML row, with cells level, time-stamp and message
   @Override
   public String format(LogRecord record) {
      StringBuilder buf = new StringBuilder();
      buf.append("<tr>");
      // Level - Show in red for levels on or above WARNING
      if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
         buf.append("<td style='color:red'>");
         buf.append(record.getLevel());
      } else {
         buf.append("<td>");
         buf.append(record.getLevel());
      }
      buf.append("</td>");
      // Time stamp
      buf.append("<td>");
      buf.append(formatDateTime(record.getMillis()));
      buf.append("</td>");
      // Message
      buf.append("<td>");
      buf.append(formatMessage(record));
      buf.append("</td>");
      buf.append("</tr>");
      return buf.toString();
   }
 
   // Called when the handler opens the formatter - Write HTML starting
   @Override
   public String getHead(Handler h) {
      return "<html><body><h2>Log Entries</h2><table border='1'>"
           + "<tr><th>Level</th><th>Time</th><th>Message</th></tr>";
   }
 
   // Called when the handler closes the formatter - Write HTML ending
   @Override
   public String getTail(Handler h) {
      return "</table></body></html>";
   }
 
   // Helper method to format the time-stamp
   private String formatDateTime(long millisecs) {
      SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
      Date recordDate = new Date(millisecs);
      return dateFormat.format(recordDate);
   }
}
