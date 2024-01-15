package com.amdocs.bankingapp;
import java.sql.*;

public class BankingProject {
	public static void main(String args [])
	   {
		   try
		   {
			   Class.forName("oracle.jdbc.driver.OracleDriver");

			   Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "scott", "tiger");

		       Statement stmt = con.createStatement();
		       
		       ResultSet rs = stmt.executeQuery("SELECT TransID, AcctNo, OldBal, TransType, TransAmt, NewBal, TransStat FROM BankTrans"); 
		       
		       int j = 0;
		       while (rs.next()) 
		       {
		    	   int transID = rs.getInt(1);
		    	   double oldBal = rs.getDouble(3);
		           String transType = rs.getString(4);
		           double transAmt = rs.getDouble(5);		          
		           String transStat = rs.getString(7);
		           double newBal;
		           if( transStat == null) {
		        	   newBal = oldBal + (transType.equals("D") ? transAmt : -transAmt);
		        	   if(newBal >= 0) {
		        		   transStat = "Valid";
		        	   }
		        	   else {
		        		   transStat = "Invalid";
		        	   }
			           updateNewBal(con, transID, newBal, transStat);
			           insertValidity(con, transID, transType, transAmt, transStat);
			           j++;
		           }
		            
		        }
		       if( j == 0) {
		    	   System.out.println("\n \t All Transactions are up to Date");
		       }
			   
		       con.close();
		   }
		   catch(Exception E){
			   
			   E.printStackTrace();
		   }
	    }

	private static void updateNewBal(Connection con, int transID, double newBal, String transStat)
         throws SQLException {
     
		String updateBankTrans = "UPDATE BankTrans SET NewBal = ?, TransStat = ? WHERE TransID = ?";
     try (PreparedStatement stmt = con.prepareStatement(updateBankTrans)) {
         stmt.setDouble(1, newBal);
         stmt.setString(2, transStat);
         stmt.setInt(3, transID);
         int i = stmt.executeUpdate();
			System.out.println("\n \t" + i + " Row(s) Updated in BankTrans");
     }
 }
	
	
	
	private static void insertValidity(Connection con, int transID, String transType, double transAmt,String transStat) 
			throws SQLException {
		String chooseTable;
		if (transStat == "Valid") {
			chooseTable = "ValidTrans";
		}
		else {
			chooseTable = "InValidTrans";
		}
		String insertValidity = "INSERT INTO " + chooseTable + "(TransID, TransType, TransAmt, Validity) VALUES (?, ?, ?, ?)";
		try (PreparedStatement stmt = con.prepareStatement(insertValidity)) {
			stmt.setInt(1, transID);
			stmt.setString(2, transType);
			stmt.setDouble(3, transAmt);
			stmt.setString(4, transStat);
			int i = stmt.executeUpdate();
			System.out.println("\n \t" + i + " Row(s) Updated in Table" + chooseTable);
		}
	}
}



