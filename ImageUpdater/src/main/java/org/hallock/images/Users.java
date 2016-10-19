package org.hallock.images;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class Users
{
	public static class User
	{
                public final int id;
		public String name;
		public String password;
		public boolean isAdmin;
		public boolean uploadPerm;
		public boolean deletePerm;
                
                public User(
                    int id,
                    String name,
                    String password,
                    boolean isAdmin,
                    boolean uploadPerm,
                    boolean deletePerm)
                {
                    this.id = id;
                    this.name = name;
                    this.password = password;
                    this.isAdmin = isAdmin;
                    this.uploadPerm = uploadPerm;
                    this.deletePerm = deletePerm;
                }
	}
        
        public static LinkedList<User> listUsers() throws SQLException
        {
                LinkedList<User> users = new LinkedList<>();
            
		String table    = SqlSettings.getDbDatabaseName();
		String user     = SqlSettings.getDbUsername();
		String password = SqlSettings.getDbPassword();
		
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + table 
				+ "?" + "user=" + user + "&password=" + password);)
		{
                    String query = "select U_ID, USERNAME, PASSWORD, IS_ADMIN, UPLOADPERM, DELETEPERM from USERS;";
                    try (PreparedStatement updateTotal = conn.prepareStatement(query);) {
                        try (ResultSet results = updateTotal.executeQuery();) {
                            while (results.next()) {
                                int ndx = 1;
                                int id = results.getInt(ndx++);
                                String username = results.getString(ndx++);
                                String passwordS = results.getString(ndx++);
                                boolean isAdmin = results.getBoolean(ndx++);
                                boolean updatePerm = results.getBoolean(ndx++);
                                boolean deletePerm = results.getBoolean(ndx++);
                                
                                User userEntry = new User(id, username, passwordS, isAdmin, updatePerm, deletePerm);
                                users.add(userEntry);
                            }
                        }
                    }
		}
                
            return users;
        }
	
	public static User getUser(String username, String password)
	{
		return null;
	}
	
	public static boolean isUsernameFree(String username)
	{
		String query = "select U_ID from USERS where USERNAME=?;";
		return true;
	}
	public static void updateUser(User user)
	
	{
		
	}
	public static void deleteUser(User user)
	{
		
	}
	public static void createUser(User user)
	{
		
	}
}
