package com.ywq.tools.sqlTools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author ywq
 * 
 */
public class ConnectionManager {

	static Log log = LogFactory.getLog(ConnectionManager.class);
	private static ConnectionManager instance;

	public static ConnectionManager getInstance() {
		if (instance == null) {
			instance = new ConnectionManager();
		}
		return instance;
	}

	private ConnectionManager() {

	}

	public Connection getConnection() {		
		try {
//			Class.forName("org.gjt.mm.mysql.Driver");
			Class.forName("com.mysql.cj.jdbc.Driver");
			return DriverManager.getConnection(BeanCreator.url, BeanCreator.dbUser, BeanCreator.password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void close(Connection con, Statement stmt, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws SQLException {
		Connection con = ConnectionManager.getInstance().getConnection();
		con.close();
	}
}
