package com.mandr.info;

import com.mandr.database.DatabaseRow;

public class AnimInfo extends Info {
	private String name;
	private String filepath;
	
	@Override
	public boolean cacheRow(DatabaseRow row) {
		if(row == null) return false;
		if(!super.cacheRow(row)) return false;
		
		name	 = row.getText("Name");
		filepath = row.getText("Filepath");
		
		return true;
	}
	
	public String getName() {
		return name;
	}
	
	public String getFilePath() {
		return filepath;
	}
}
