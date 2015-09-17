package com.mzywx.liao.android.db;

import java.util.List;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import com.mzywx.liao.android.model.ChatMessage;

import android.database.sqlite.SQLiteDatabase;

public class DbQueryHelper {
	private static final String DB_NAME = "liao";

	private SQLiteDatabase database;

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
		database = Connector.getDatabase();
	}

	public List<ChatMessage> query(String userName) {
		return DataSupport.findAll(ChatMessage.class);
	}

	public int insert(Object object) {
		int index = -1;
		if (object instanceof ChatMessage) {
			((ChatMessage) object).save();
			index = ((ChatMessage) object).getMessageId();
		}
		return index;
	}

	public int delete(int messageId) {
		return DataSupport.delete(ChatMessage.class, messageId);
	}
}
