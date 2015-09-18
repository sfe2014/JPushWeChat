package com.mzywx.liao.android.db;

import org.litepal.tablemanager.Connector;

public class DbQueryHelper {
	static class DbHelperHolder {
		static DbQueryHelper dbHelper = new DbQueryHelper();
	}

	public static DbQueryHelper getInstance() {
		return DbHelperHolder.dbHelper;
	}

	private DbQueryHelper() {
		checkDatabase();
	}

	private void checkDatabase() {
		Connector.getDatabase();
	}
}
