package crs.fcl.integration.main;

//STEP 1. Import required packages
import java.sql.*;

public class MariaDB {
  // JDBC driver name and database URL

  static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
  static final String DB_URL = "jdbc:mariadb://localhost:3306/db1";

  //  Database credentials
  static final String USER = "rwang";
  static final String PASS = "Stamina168";

  public static void main(String[] args) {
      Connection conn = null;
      Statement stmt = null;
      try {
          //STEP 2: Register JDBC driver
          Class.forName("org.mariadb.jdbc.Driver");

          //STEP 3: Open a connection
          System.out.println("Connecting to a selected database...");
          conn = DriverManager.getConnection(DB_URL, USER, PASS);
          System.out.println("Connected database successfully...");
          DatabaseMetaData dbMetaData = conn.getMetaData();
          System.out.println("JDBC DRIVER NAME" + dbMetaData.getDriverName());
          //STEP 4: Execute a query
          System.out.println("Creating table in given database...");
          stmt = conn.createStatement();

          String sql = "CREATE TABLE REGISTRATION "
                  + "(id INTEGER not NULL, "
                  + " first VARCHAR(255), "
                  + " last VARCHAR(255), "
                  + " age INTEGER, "
                  + " PRIMARY KEY ( id ))";

          stmt.executeUpdate(sql);
          System.out.println("Created table in given database...");
      } catch (SQLException se) {
          //Handle errors for JDBC
          se.printStackTrace();
      } catch (Exception e) {
          //Handle errors for Class.forName
          e.printStackTrace();
      } finally {
          //finally block used to close resources
          try {
              if (stmt != null) {
                  conn.close();
              }
          } catch (SQLException se) {
          }// do nothing
          try {
              if (conn != null) {
                  conn.close();
              }
          } catch (SQLException se) {
              se.printStackTrace();
          }//end finally try
      }//end try
      System.out.println("Goodbye!");
  }//end main
}//end JDBCExample