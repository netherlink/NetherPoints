/*
	Copyright (C) 2011 by NetherLink
	
	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:
	
	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.
	
	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.
*/

package netherlink.minecraft.netherpoints;

import java.io.File;
import java.sql.*;
import org.bukkit.event.*;
import org.bukkit.plugin.java.*;

public class netherpoints extends JavaPlugin{
	
	/*
	 * Variable Declarations
	 */
	
	Connection sqlConnection; // This is the connection for the SQL related things.
	NPBlockListener npBlockListener; // This is the block listener.
	NPPlayerListener npPlayerListener; // This is the player listener.
	TeleportManager teleportManager; // This manages all of the teleporting related stuff.
	
	/*
	 * Enable / Disable
	 */
	
	public void onDisable(){
		if(teleportManager != null) // Finalize anything related to the teleport manager.
			teleportManager.Finalize();
		if(isSqlEnabled()) // Make sure to run this LAST.
			disableSql();
	}
	
	public void onEnable(){
		sqlConnection = null;
		npBlockListener = null;
		npPlayerListener = null;
		teleportManager = null;
		try{ // ALL OF THIS IS SQL RELATED STUFF.
			Class.forName("org.sqlite.JDBC");
		}catch(ClassNotFoundException e){
			getServer().getLogger().severe("NetherPoints: SQLITE PLUGIN NOT FOUND. PLUGIN WILL NOT INITIALISE!");
			return; // Do not load the plugin if SQLite is not found.
		} // END THE SQL RELATED STUFF.
		checkPluginDataFolder(); // Make sure the plugin data folder has been created / create plugin data folder.
		teleportManager = new TeleportManager(this);
		npBlockListener = new NPBlockListener(this, teleportManager);
		npPlayerListener = new NPPlayerListener(this, teleportManager);
		// Because we have registered to listen on the Monitor level, WE ARE NOT ALLOWED TO CANCEL EVENTS! DON'T THINK ABOUT IT BRO!
		getServer().getPluginManager().registerEvent(Event.Type.BLOCK_BREAK, npBlockListener, Event.Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.SIGN_CHANGE, npBlockListener, Event.Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, npPlayerListener, Event.Priority.Highest, this);
		output("Startup complete. Plugin enabled!");
	}
	
	/*
	 * SQL Related Stuff
	 */
	
	public PreparedStatement createPreparedStatement(String statement){
		PreparedStatement preparedStatement = null; // Initialise the PreparedStatement.
		if(!isSqlEnabled()) // Check if SQL is enabled.
			return null;
		try{
			preparedStatement = sqlConnection.prepareStatement(statement);
		}catch(SQLException e){
			warning("SQLite Error (" + e.getErrorCode() + ": " + e.getMessage() + ")");
		}
		return preparedStatement;
	}
	
	public void disableSql(){
		if(!isSqlEnabled())
			return;
		try{
			sqlConnection.close();
			sqlConnection = null;
		}catch(SQLException e){
			warning("SQLite Error (" + e.getErrorCode() + ": " + e.getMessage() + ")");
		}
	}
	
	// THIS FUNCTION WAS INSPIRED BY TAYLOR KELLY MODIFIED BY TECHNOBULLDOG.
	public boolean doesTableExist(String name){
		ResultSet resultSet = null;
		DatabaseMetaData databaseMetadata = null;
		if(!isSqlEnabled())
			return false;
		try{
			boolean found = false;
			databaseMetadata = sqlConnection.getMetaData(); // Grab the metadata.
			resultSet = databaseMetadata.getTables(null, null, name, null); // Get the tables.
			if(resultSet.next()) // If it is unable to get a table return false.
				found = true;
			resultSet.close();
			return found;
		}catch(SQLException e){
			warning("SQLite Error (" + e.getErrorCode() + ": " + e.getMessage() + ")");
			return false;
		}
	}
	
	public void enableSql(String dbname){
		if(isSqlEnabled()){
			disableSql();
			warning("Database in use. Disabling old one and enabling new one.");
		}
		try{
			sqlConnection = DriverManager.getConnection("jdbc:sqlite:" + getDataFolder().getAbsolutePath() + File.separator + dbname);
			sqlConnection.setAutoCommit(true);
		}catch(SQLException e){
			warning("SQLite Error (" + e.getErrorCode() + ": " + e.getMessage() + ")");
			warning("Database inaccessible. Waypoints will not load or save.");
		}
	}
	
	public ResultSet executeQuery(String query){
		ResultSet resultSet = null; // Initialise the ResultSet.
		if(!isSqlEnabled()) // If SQL isn't running exit.
			return null;
		try{
			resultSet = sqlConnection.createStatement().executeQuery(query);
		}catch(SQLException e){
			warning("SQLite Error (" + e.getErrorCode() + ": " + e.getMessage() + ")");
		}
		return resultSet;
	}
	
	public void executeUpdate(String query){
		if(!isSqlEnabled()) // If SQL isn't running exit.
			return;
		try{
			sqlConnection.createStatement().executeUpdate(query);
		}catch(SQLException e){
			warning("SQLite Error (" + e.getErrorCode() + ": " + e.getMessage() + ")");
		}
	}
	
	public boolean isSqlEnabled(){
		if(sqlConnection == null)
			return false;
		return true;
	}
	
	/*
	 * Utilities
	 */
	
	public void checkPluginDataFolder(){
		File f = new File(getDataFolder().getAbsolutePath() + File.separator);
		if(!f.exists())
			f.mkdir();
	}
	
	public void output(String output){
		System.out.println("NetherPoints: " + output);
	}
	
	public void warning(String warning){
		getServer().getLogger().warning("NetherPoints: " + warning);
	}
	
}
