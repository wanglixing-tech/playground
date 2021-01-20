package crs.fcl.integration.main;

import java.sql.Timestamp;

import crs.fcl.integration.db.DatabaseAdapter;
import crs.fcl.integration.db.DatabaseAdapter.PreparedStatement;
import crs.fcl.integration.db.SQLException;
import dro.util.Properties;

public class RichardDatabaseHandler {
       private DatabaseAdapter databaseAdapter;// = null;

       {
             databaseAdapter = null;
       }

       public RichardDatabaseHandler() {
             this.databaseAdapter = new DatabaseAdapter(Properties.properties());
       }

       public void ps1() {
             try {
                    PreparedStatement ps1 = null;
                    
                    if (null == ps1) {
                           ps1 = this.databaseAdapter
                                 .returns(DatabaseAdapter.Return.Connection)
                                 .prepareStatement(
                                        "INSERT INTO ESBDBID.IIB_STATUS(HOSTNAME, INTEGRATION_NODE, STATUS, STATUS_TIME) VALUES(?,?,?,?)",
                                          //new String[]{"ID"},
                                        DatabaseAdapter.Return.PreparedStatement
                                 );
                    }
                    final String _hostname = "fsr3vlesb";
                    final String _integration_node = "EFIB01";
                    final String _status = "running";
                    final Timestamp _status_time = new Timestamp(System.currentTimeMillis());
                    ps1.setString   (1, _hostname          , DatabaseAdapter.Return.PreparedStatement)
                       .setString   (2, _integration_node  , DatabaseAdapter.Return.PreparedStatement)
                       .setString   (4, _status            , DatabaseAdapter.Return.PreparedStatement)
                       .setTimestamp(5, _status_time       , DatabaseAdapter.Return.PreparedStatement)
                       .addBatch(DatabaseAdapter.Return.PreparedStatement);
                    
                    if (null != ps1) {
                           ps1.executeBatch(DatabaseAdapter.Return.PreparedStatement)
                           //.close()
                           ;
                           ps1 = null;
                    }
             } catch (final SQLException exc) {
               //throw new RuntimeException(exc);
             }
       }
       public void ps2() {
             try {
                    PreparedStatement ps2 = null;
                    
                    if (null == ps2) {
                           ps2 = this.databaseAdapter
                                 .returns(DatabaseAdapter.Return.Connection)
                                 .prepareStatement(
                                        "INSERT INTO ESBDBID.IIB_STATUS(HOSTNAME, INTEGRATION_NODE, INTEGRATION_SERVER, STATUS, STATUS_TIME) VALUES(?,?,?,?,?)",
                                          //new String[]{"ID"},
                                        DatabaseAdapter.Return.PreparedStatement
                                 );
                    }
                    final String _hostname = "fsr3vlesb";
                    final String _integration_node = "EFIB01";
                    final String _integration_server = "rwang";
                    final String _status = "running";
                    final Timestamp _status_time = new Timestamp(System.currentTimeMillis());
                    ps2.setString   (1, _hostname          , DatabaseAdapter.Return.PreparedStatement)
                       .setString   (2, _integration_node  , DatabaseAdapter.Return.PreparedStatement)
                       .setString   (3, _integration_server, DatabaseAdapter.Return.PreparedStatement)
                       .setString   (4, _status            , DatabaseAdapter.Return.PreparedStatement)
                       .setTimestamp(5, _status_time       , DatabaseAdapter.Return.PreparedStatement)
                       .addBatch(DatabaseAdapter.Return.PreparedStatement);
                    
                    if (null != ps2) {
                           ps2.executeBatch(DatabaseAdapter.Return.PreparedStatement)
                           //.close()
                           ;
                           ps2 = null;
                    }
             } catch (final SQLException exc) {
               //throw new RuntimeException(exc);
             }
       }
       public void ps3() {
             try {
                    PreparedStatement ps3 = null;
                    
                    if (null == ps3) {
                           ps3 = this.databaseAdapter
                                 .returns(DatabaseAdapter.Return.Connection)
                                 .prepareStatement(
                                        "INSERT INTO ESBDBID.IIB_STATUS(HOSTNAME, INTEGRATION_NODE, INTEGRATION_SERVER, APPLICATION, STATUS, STATUS_TIME) VALUES(?,?,?,?,?,?)",
                                          //new String[]{"ID"},
                                        DatabaseAdapter.Return.PreparedStatement
                                 );
                    }
                    final String _hostname = "fsr3vlesb";
                    final String _integration_node = "EFIB01";
                    final String _integration_server = "rwang";
                    final String _application = "MyApplication";
                    final String _status = "running";
                    final Timestamp _status_time = new Timestamp(System.currentTimeMillis());
                    ps3.setString   (1, _hostname          , DatabaseAdapter.Return.PreparedStatement)
                       .setString   (2, _integration_node  , DatabaseAdapter.Return.PreparedStatement)
                       .setString   (3, _integration_server, DatabaseAdapter.Return.PreparedStatement)
                       .setString   (4, _application       , DatabaseAdapter.Return.PreparedStatement)
                       .setString   (5, _status            , DatabaseAdapter.Return.PreparedStatement)
                       .setTimestamp(6, _status_time       , DatabaseAdapter.Return.PreparedStatement)
                       .addBatch(DatabaseAdapter.Return.PreparedStatement);
                    
                    if (null != ps3) {
                           ps3.executeBatch(DatabaseAdapter.Return.PreparedStatement)
                           //.close()
                           ;
                           ps3 = null;
                    }
             } catch (final SQLException exc) {
               //throw new RuntimeException(exc);
             }
       }
}
