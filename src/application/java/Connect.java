package application.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;

public class Connect {

	public Connection con;
	public Statement st;
	public ResultSet rs;
	
	public Connect() {
		System.out.println("___________CONNECTION CREATED___________");
		//Connection: 
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con=DriverManager.getConnection(
					"jdbc:mysql://upw0dxpo8jbhxutr:5sbWqBapPMO5lAaznMG6@byiig0vngbvcenz9feed-mysql.services.clever-cloud.com:3306/byiig0vngbvcenz9feed?autoReconnect=true", "upw0dxpo8jbhxutr", "5sbWqBapPMO5lAaznMG6");
			st=con.createStatement();
		} catch (ClassNotFoundException e1) {e1.printStackTrace();} catch (SQLException e1) {e1.printStackTrace();}
	}
	
	int runSQL(String query) {
		if (query.indexOf("select") == 0) {
			try {
				rs = st.executeQuery(query);
				System.out.println("\"" + query + "\" was successful");
				return 0;
			} catch (CommunicationsException ex) {
			    System.out.println("Sorry, connection timed out. Will retry.");
			    runSQL(query);
			    return 0;
			} catch (SQLException ex) {ex.printStackTrace(); System.err.println(query + " failed"); return -1;}
		}
		else {
			try {
				st.executeUpdate(query);
				System.out.println("\"" + query + "\" was successful");
				return 0;
			} catch (SQLException e) {e.printStackTrace(); System.err.println(query + " failed"); return -1;}
		}
	}
}
