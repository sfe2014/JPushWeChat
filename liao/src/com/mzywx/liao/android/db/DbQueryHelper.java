package com.mzywx.liao.android.db;

import java.util.List;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import com.mzywx.liao.android.bean.ChatMessage;

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

    public List<ChatMessage> queryChatMessage(int limit, int offset) {
        return DataSupport.order("messageDate desc").limit(limit)
                .offset(offset).find(ChatMessage.class);
    }
}
