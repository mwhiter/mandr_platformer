package com.mandr.info;

import com.mandr.database.DatabaseRow;

public abstract class Info {
	private int id;
	
	public boolean cacheRow(DatabaseRow row) {
		if(row == null) return false;
		id = row.getInt("ID");
		return true;
	}
	
	public int getID() {
		return id;
	}
};