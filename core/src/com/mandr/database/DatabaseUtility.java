package com.mandr.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.mandr.game.GameGlobals;
import com.mandr.info.*;

public class DatabaseUtility {
	private Connection m_Connection;
	private HashMap<String, ArrayList<DatabaseRow>> m_TableMap;
	
	public DatabaseUtility() {
		m_Connection = null;
		m_TableMap = new HashMap<String, ArrayList<DatabaseRow>>();
	}
	
	public boolean initDatabase() {
		boolean success;
		success = load("database");
		if(success) {
			success = loadDatabase();
			success = fetchDatabase();
		}
		return success;
	}
	
	/** Establish a connection with the database.
	 * @param databaseName The name of the database file.
	 * @return Whether the connection was successful or not
	 * */
	private boolean load(String databaseName) {
		try {
			Class.forName("org.sqlite.JDBC");
			m_Connection = DriverManager.getConnection("jdbc:sqlite:" + Gdx.files.internal("bin/database/" + databaseName));
		}
		catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return false;
		}
		return true;
	}
	
	/** Load data from the tables in the database into hashmaps, which will be accessed by fetchDatabase() to put into game-readable format
	 * @return Whether or not loading the tables was successful.
	 * */
	private boolean loadDatabase() {
		boolean success;
		success = loadTable("Weapons");
		success = loadTable("Items");
		return success;
	}
	
	private ArrayList<DatabaseRow> getTable(String tableName) {
		return m_TableMap.get(tableName);
	}
	
	/** Load a table into the database.
	 * @param The name of the table to load.
	 * @returns Whether or not the load was successful. */
	private boolean loadTable(String tableName) {
		Statement statement = null;
		try {
			// Create the SQL statement
			statement = m_Connection.createStatement();
			
			// Create the SQL command and execute it on the statement
			ResultSet rs = statement.executeQuery("SELECT * FROM " + tableName + ";");
			
			// Insert the table name (key) and list of rows (values) into the TableMap
			ArrayList<DatabaseRow> rows = new ArrayList<DatabaseRow>();
			m_TableMap.put(tableName, rows);
			
			// Loop through each table row and load them
			ResultSetMetaData meta = rs.getMetaData();
			while(rs.next()) {
				loadRow(tableName, rs, meta, rows);
			}
			
			statement.close();
			return true;
		}
		catch(Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	/** Load a row into the database.
	 * @param tableName The name of the table we are putting the row in
	 * @param rs The ResultSet (Do not modify the ResultSet in this function!
	 * @param meta the metadata of the ResultSet
	 * @param rows The ArrayList storing all the rows
	 * @throws SQLException 
	 */
	public void loadRow(String tableName, ResultSet rs, ResultSetMetaData meta, ArrayList<DatabaseRow> rows) throws SQLException {
		DatabaseRow row = new DatabaseRow();
		row.load(rs, meta);
		rows.add(row);
	}
	
	/** Fetch the database.
	 * @return Whether or not fetching was successful. */
	private boolean fetchDatabase() {
		boolean success;
		success = cacheWeaponTable(GameGlobals.getWeaponStats(), "Weapons");
		success = cacheItemTable(GameGlobals.getItemInfos(), "Items");
		return success;
	}
	
	// I'd rather avoid having to do ugly factor instantiation for generic types. I'd like to do T info = new T();. But we can't do that in Java (praise C++!) so I'm going to just have a bunch of overriden methods
	// Terrible hack, but unavoidable.
	
	private boolean cacheWeaponTable(ArrayList<WeaponInfo> collection, String tableName) {
		ArrayList<DatabaseRow> table = getTable(tableName);
		if(table != null) {
			for(DatabaseRow row : table) {
				WeaponInfo info = new WeaponInfo();
				info.cacheRow(row);
				collection.add(info);
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	private boolean cacheItemTable(ArrayList<ItemInfo> collection, String tableName) {
		ArrayList<DatabaseRow> table = getTable(tableName);
		if(table != null) {
			for(DatabaseRow row : table) {
				ItemInfo info = new ItemInfo();
				info.cacheRow(row);
				collection.add(info);
			}
			return true;
		}
		else {
			return false;
		}
	}
}
