package com.mandr.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.mandr.game.Globals;
import com.mandr.game.ProjectileInfo;
import com.mandr.info.*;
import com.mandr.loading.AnimLoader;

public class DatabaseUtility {
	private static Connection m_Connection = null;
	private static HashMap<String, ArrayList<DatabaseRow>> m_TableMap = new HashMap<String, ArrayList<DatabaseRow>>();
	
	public static boolean initDatabase() {
		boolean success;
		success = load("database.db");
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
	private static boolean load(String databaseName) {
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
	private static boolean loadDatabase() {
		boolean success;
		success = loadTable("Animations");
		success = loadTable("Weapon");
		success = loadTable("Actor");
		success = loadTable("Item");
		success = loadTable("Projectile");
		return success;
	}
	
	private static ArrayList<DatabaseRow> getTable(String tableName) {
		return m_TableMap.get(tableName);
	}
	
	/** Load a table into the database.
	 * @param The name of the table to load.
	 * @returns Whether or not the load was successful. */
	private static boolean loadTable(String tableName) {
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
	public static void loadRow(String tableName, ResultSet rs, ResultSetMetaData meta, ArrayList<DatabaseRow> rows) throws SQLException {
		DatabaseRow row = new DatabaseRow();
		row.load(rs, meta);
		rows.add(row);
	}
	
	/** Returns an ID from type name (from the specified table).
	 * For example, if I send it typeName = "ENTITY_PLAYER", tableName = "Entities", it should return 0.
	 * It is up to the caller of this function to use this ID appropriately, since this method has no way of knowing what info you're looking for.
	 * @param typeName The type name to send (example: "ENTITY_PLAYER")
	 * @param tableName The table to look up.
	 * @return The ID of the type name, or -1 if none exists.
	 * */
	public static int getIDFromTypeName(String typeName, String tableName) {
		if(typeName == null) return -1;
		
		ArrayList<DatabaseRow> rows = m_TableMap.get(tableName);
		if(rows == null) return -1;
		
		for(DatabaseRow row : rows) {
			String type = row.getText("Type");
			if(type.equalsIgnoreCase(typeName)) {
				return row.getInt("ID");
			}
		}
		
		return -1;
	}
	
	/** Fetch the database.
	 * @return Whether or not fetching was successful. */
	private static boolean fetchDatabase() {
		boolean success;
		success = cacheAnimationTable(Globals.getAnimInfos(), "Animations");
		success = cacheActorTable(Globals.getActorInfos(), "Actor");
		success = cacheItemTable(Globals.getItemInfos(), "Item");
		success = cacheProjectileTable(Globals.getProjectileInfos(), "Projectile");
		success = cacheWeaponTable(Globals.getWeaponInfos(), "Weapon");
		return success;
	}
	
	// I'd rather avoid having to do ugly factory instantiation for generic types.
	// I'd like to do T info = new T();. But we can't do that in Java (praise C++!) so I'm going to just have a bunch of overriden methods
	// Terrible hack, but unavoidable.
	
	private static boolean cacheAnimationTable(ArrayList<HashMap<String, Animation>> collection, String tableName) {
		ArrayList<DatabaseRow> table = getTable(tableName);
		if(table != null) {
			for(DatabaseRow row : table) {
				AnimInfo info = new AnimInfo();
				info.cacheRow(row);
				
				// TODO Make this consistent with everything else!
				HashMap<String, Animation> anim = AnimLoader.parse(info.getFilePath());
				if(anim != null)
					collection.add(anim);
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	private static boolean cacheActorTable(ArrayList<ActorInfo> collection, String tableName) {
		ArrayList<DatabaseRow> table = getTable(tableName);
		if(table != null) {
			for(DatabaseRow row : table) {
				ActorInfo info = new ActorInfo();
				info.cacheRow(row);
				collection.add(info);
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	private static boolean cacheItemTable(ArrayList<ItemInfo> collection, String tableName) {
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
	
	private static boolean cacheProjectileTable(ArrayList<ProjectileInfo> collection, String tableName) {
		ArrayList<DatabaseRow> table = getTable(tableName);
		if(table != null) {
			for(DatabaseRow row : table) {
				ProjectileInfo info = new ProjectileInfo();
				info.cacheRow(row);
				collection.add(info);
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	private static boolean cacheWeaponTable(ArrayList<WeaponInfo> collection, String tableName) {
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
}
