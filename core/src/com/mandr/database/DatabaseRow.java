package com.mandr.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

public class DatabaseRow {
	private HashMap<String, String> m_DataMap;
	
	public DatabaseRow() {
		m_DataMap = new HashMap<String, String>();
	}
	
	/** Load a row of a table
	 * @param rs The ResultSet of the row.
	 * @param meta The metadata of the ResultSet
	 * @throws SQLException
	 * */
	public void load(ResultSet rs, ResultSetMetaData meta) throws SQLException {
		// Loop through the columns in the row, and store them in a hash map with columnname (key), columnvalue (value)
		for(int i = 1; i <= meta.getColumnCount(); i++) {
			m_DataMap.put(meta.getColumnName(i), rs.getString(i));
		}
	}
	
	public int getInt(String column) 		{ return Integer.valueOf(m_DataMap.get(column)); }
	public boolean getBool(String column) 	{ return Boolean.valueOf(m_DataMap.get(column)); }
	public float getFloat(String column) 	{ return Float.valueOf(m_DataMap.get(column)); }
	public String getText(String column) 	{ return m_DataMap.get(column); }
}
