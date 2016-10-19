package org.hallock.images;

public class Users
{
	private static class User
	{
		String name;
		String password;
		boolean isAdmin;
		boolean deletePerm;
		boolean uploadPerm;
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
