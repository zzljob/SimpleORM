package com.yepstudio.simpleorm;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * 实体类要实现的接口
 * @author zzljob@gmail.com
 * @date 2013-2-22
 *
 */
public interface EntityRepository {

	public String getTableName();
    public String getKeyId();
    public String[] getAllKeys();

    public String getSQLCreateTable();
    public String[] getSQLsCreateIndex();

    public ContentValues getContentValues(Entity entity, boolean isCreate);
    public Entity buildEntityFromCursorData(Cursor cursor);

}
