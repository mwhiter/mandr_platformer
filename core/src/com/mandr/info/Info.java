package com.mandr.info;

import com.mandr.database.DatabaseRow;

public interface Info {
	boolean cacheRow(DatabaseRow result);
}