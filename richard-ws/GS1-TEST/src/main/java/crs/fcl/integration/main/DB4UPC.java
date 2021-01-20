package crs.fcl.integration.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class DB4UPC {
	private static String dbURL = "jdbc:db2://fsr2vllhstag1:50000/tcvland2";
	private static String uid = "esbdbid";
	private static String upw = "fsr2es3401#";
	private static String path2UPCGtinFile = "C:\\Users\\Richard.Wang\\Documents\\My_DailyWork\\GS1\\Data\\upc-gtinList.txt";	
	private static String path2GTINFile = "C:\\Users\\Richard.Wang\\Documents\\My_DailyWork\\GS1\\Data\\gtinList.txt";
	final static int GTIN_LENGTH=14;
	private static String sql4upc = "WITH initial_rs as (   " + 
			"SELECT i.isn,   " + 
			"       i.fim_no,   " + 
			"       i.fim_stat,   " + 
			"       i.fric_major_dpt_no,   " + 
			"       i.fric_minor_dpt_no,   " + 
			"       i.fric_major_categ_no,   " + 
			"       i.fric_grp_categ_no,    " + 
			"       i.fim_desc,   " + 
			"       i.fim_rtl_by_wght_cd  " + 
			"  FROM land.imf AS i   " + 
			" WHERE (  " + 
			"        (i.fric_major_dpt_no IN ( 1, 15, 20, 25, 30, 35, 40, 41, 45, 50)  " + 
			"        AND COALESCE(fim_store_coupon_cd, 'N') = 'N')  " + 
			"        OR (i.fric_major_dpt_no = 60 and i.fric_minor_dpt_no in (7, 8, 9, 10, 14, 16, 22, 24))  " + 
			"        OR (i.fric_major_dpt_no = 67 and i.fric_minor_dpt_no in (21, 23))  " + 
			"              )  " + 
			"               AND i.isn IN (SELECT isn   " + 
			"                             FROM   land.imfc_fim_rgn_high_use_tbl   " + 
			"                             WHERE  fim_stk_stat IN ( 'A', 'E', 'G', 'J', 'P', 'S' )))  " + 
			"SELECT u.fim_upc_no  " + 
			"  FROM initial_rs   " + 
			"       LEFT OUTER JOIN land.imfc_fim_upc_no AS u ON initial_rs.isn = u.isn   " + 
			" WHERE fric_major_dpt_no != 30 and fim_upc_no_tab_ind = 1  " + 
			"UNION  " + 
			"SELECT u.fim_case_upc_no  " + 
			"  FROM initial_rs   " + 
			"       LEFT OUTER JOIN land.imfc_fim_case_upc_no AS u ON initial_rs.isn = u.isn   " + 
			" WHERE fric_major_dpt_no != 30 and fim_case_upc_no_tab_ind = 1  " + 
			"UNION  " + 
			"SELECT u.fim_upc_no  " + 
			"  FROM initial_rs   " + 
			"       INNER JOIN land.imfc_fim_upc_no AS u ON initial_rs.isn = u.isn   " + 
			" WHERE fric_major_dpt_no = 30 and fim_upc_no_tab_ind = 1  " + 
			"order by 1 ";
	// jdbc Connection
	private static Connection conn = null;
	private static Statement stmt = null;

	public static void main(String[] args) throws IOException {
		createConnection();
		selectUPCs();
		disconn();
	}

	private static void createConnection() {
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
			// Get a connection
			conn = DriverManager.getConnection(dbURL, uid, upw);
		} catch (Exception except) {
			except.printStackTrace();
		}
	}

	private static void selectUPCs() throws IOException {
		try {
		    Path path1 = Paths.get(path2UPCGtinFile);
		    Path path2 = Paths.get(path2GTINFile);
			stmt = conn.createStatement();
			ResultSet results = stmt.executeQuery(sql4upc);
			ResultSetMetaData rsmd = results.getMetaData();
			int numberCols = rsmd.getColumnCount();
			for (int i = 1; i <= numberCols; i++) {
				// print Column Names
				System.out.print(rsmd.getColumnLabel(i) + "\t\t");
			}

			System.out.println("\n--------------------Start------------------------");

			while (results.next()) {
				String upcString = results.getString(1);
				if (upcString == null || ! upcString.matches("[0-9]*$")) {
					System.out.println("Invalid UPC code, return null.");
					break;
				} else {
					String gtinString = UPC2GTIN14(upcString) + "\n";
					if (gtinString != null) {
						Files.write(path1, (upcString + "<->" + gtinString).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
						Files.write(path2, gtinString.getBytes(), 
							StandardOpenOption.CREATE,
							StandardOpenOption.APPEND);
						System.out.print(gtinString);
					}
				}
			}
			results.close();
			stmt.close();
			System.out.println("\n--------------------End-------------------------");
			
		} catch (SQLException sqlExcept) {
			sqlExcept.printStackTrace();
		}
	}

	private static void disconn() {
		try {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException sqlExcept) {

		}

	}
	
	/**
	 * To convert a UPC to an GTIN
	 * @param upc 
	 * @return gtin
	 */
	public static String UPC2GTIN14(String upc) {
		StringBuilder codeWithCheckDigit = new StringBuilder(GTIN_LENGTH);
			
		char[] c_array = upc.toCharArray();
		int indexOf0 = GTIN_LENGTH - c_array.length - 1;
		char[] charZeros = new char[indexOf0];
		for (int i = 0; i < indexOf0; i++ ) {
			charZeros[i] = '0';
		}
		
		//System.out.println("Total fill zeros=" + Arrays.toString(charZeros));
		int result = 0;
		boolean f4m3 = true;
		for (int i=c_array.length - 1; i >= 0; i--) {
			int ic=Character.getNumericValue(c_array[i]);
			result = result + ic * (f4m3 ? 3 : 1);
			f4m3 = f4m3 ? false : true;
		}
		
    	codeWithCheckDigit.append(charZeros);
		//System.out.print("Total=" + result + ";");
		int checkDigit = (int)((Math.ceil(((double)result )/ 10) * 10) - result);
		String ccd = Integer.toString(checkDigit);
		System.out.println("CheckDigit: " +  checkDigit);
    	codeWithCheckDigit.append(upc);
	    codeWithCheckDigit.append(ccd);
		System.out.println("UPC:" + upc + "->GTIN:" + codeWithCheckDigit.toString());
	    return codeWithCheckDigit.toString();
	}
}
