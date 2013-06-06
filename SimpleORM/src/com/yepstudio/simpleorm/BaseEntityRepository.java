package com.yepstudio.simpleorm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

/**
 * <p>
 * 数据库字段的定义类，这里实现了EntityRepository接口
 * </p>
 * <p>
 * 开发中只需要指定一些数据库的表和字段的名字就可以了
 * </p>
 * 
 * @author zzljob@gmail.com
 * @date 2012-12-22
 * 
 */
public abstract class BaseEntityRepository implements EntityRepository {

	protected final static String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

	protected final static String EKEY_ID = "_id";
	protected final static String EKEY_CREATE_DATE = "create_time";
	protected final static String EKEY_REMARK = "remark";

	private final static boolean USE_EXTENSION_KEYS = true;

	private String tableName;
	private SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAT, Locale.getDefault());

	/**
	 * 获取数据库表名，如果想自定义的话可以在子类覆盖该方法
	 * 
	 * @return
	 */
	@Override
	public String getTableName() {
		if (TextUtils.isEmpty(tableName)) {
			tableName = getClass().getSimpleName();
			tableName = tableName.replaceAll("Repository", "").toLowerCase(Locale.getDefault());
		}
		return tableName;
	}

	/**
	 * 获取数据库表的主键
	 * 
	 * @return
	 */
	@Override
	public String getKeyId() {
		return EKEY_ID;
	}

	/**
	 * 获取数据字段名称
	 * 
	 * @return
	 */
	protected abstract String[] getKeys();

	/**
	 * 自动扩展字段的名称
	 * 
	 * @return
	 */
	protected String[] getExtensionKeys() {
		return new String[] { EKEY_ID, EKEY_CREATE_DATE, EKEY_REMARK };
	}

	/**
	 * 获取数据库字段存储类型,如:varchar
	 * 
	 * @return
	 */
	protected abstract String[] getKeyTypes();

	/**
	 * 自动扩展字段的存储类型
	 * 
	 * @return
	 */
	protected String[] getExtensionKeyTypes() {
		return new String[] { "INTEGER PRIMARY KEY AUTOINCREMENT", "TIMESTAMP", "VARCHAR" };
	}

	/**
	 * 是否使用自动扩展字段
	 * 
	 * @return
	 */
	protected boolean useExtensionKeys() {
		return USE_EXTENSION_KEYS;
	}

	/**
	 * 获取所有字段名称
	 * 
	 * @return
	 */
	@Override
	public String[] getAllKeys() {
		return useExtensionKeys() ? mergeArray(getKeys(), getExtensionKeys()) : getKeys();
	}

	/**
	 * 获取所有字段的存储类型
	 * 
	 * @return
	 */
	protected String[] getAllKeyTypes() {
		return useExtensionKeys() ? mergeArray(getKeyTypes(), getExtensionKeyTypes()) : getKeyTypes();
	}

	/**
	 * 查询的时候查到了一条记录，建立一个实体
	 * 
	 * @param cursor
	 * @return
	 */
	protected abstract Entity buildEntity(Cursor cursor);

	/**
	 * 建立实体
	 * 
	 * @param cursor
	 * @return
	 */
	@Override
	public Entity buildEntityFromCursorData(Cursor cursor) {
		Entity entity = buildEntity(cursor);
		if (entity != null) {
			entity.setId(getLongFromCursor(cursor, EKEY_ID));
			entity.setCreateDate(getDateFromCursor(cursor, EKEY_CREATE_DATE));
			entity.setRemark(getStringFromCursor(cursor, EKEY_REMARK));
		}
		return entity;
	}

	/**
	 * 获得字段名的值
	 * 
	 * @param cursor
	 * @param name
	 * @return
	 */
	protected String getStringFromCursor(Cursor cursor, String name) {
		int index = cursor.getColumnIndex(name);
		if (index < 0) {
			return null;
		}
		return cursor.getString(index);
	}

	/**
	 * 
	 * @param cursor
	 * @param name
	 * @return
	 */
	protected Integer getIntegerFromCursor(Cursor cursor, String name) {
		int index = cursor.getColumnIndex(name);
		if (index < 0) {
			return null;
		}
		return cursor.getInt(index);
	}

	protected Long getLongFromCursor(Cursor cursor, String name) {
		int index = cursor.getColumnIndex(name);
		if (index < 0) {
			return null;
		}
		return cursor.getLong(index);
	}

	/**
	 * 
	 * @param cursor
	 * @param name
	 * @param format
	 * @return
	 */
	protected Date getDateFromCursor(Cursor cursor, String name) {
		String str = getStringFromCursor(cursor, name);
		if (TextUtils.isEmpty(str)) {
			return null;
		}
		try {
			return sdf.parse(str);
		} catch (ParseException e) {

		}
		return null;
	}

	/**
	 * 获取插入数据库的值集合
	 * 
	 * @param entity
	 * @return
	 */
	protected abstract ContentValues getValues(Entity entity);

	@Override
	public ContentValues getContentValues(Entity entity, boolean isCreate) {
		ContentValues values = getValues(entity);
		values.put(EKEY_ID, entity.getId());
		values.put(EKEY_REMARK, entity.getRemark());
		if (isCreate) {
			values.put(EKEY_CREATE_DATE, sdf.format(entity.getCreateDate()));
		}
		return values;
	}

	/**
	 * 生成创建数据库表的SQL
	 * 
	 * @return
	 */
	@Override
	public String getSQLCreateTable() {
		StringBuilder SQL = new StringBuilder();
		SQL.append("CREATE TABLE ").append(getTableName()).append(" (");

		String[] keys = getAllKeys();
		String[] keyTypes = getAllKeyTypes();
		int length = keys.length;
		String[] fields = new String[length];
		for (int i = 0; i < length; i++) {
			fields[i] = String.format("'%s' %s", keys[i], keyTypes[i]);
		}
		SQL.append(StringUtils.join(fields, ", ")).append(")");

		return SQL.toString();
	};

	/**
	 * 多个索引的字段字符串
	 * 
	 * @return
	 */
	protected String[] getIndexs() {
		return null;
	}

	/**
	 * 根据getIndexs生成多条建立索引的SQL
	 * 
	 * @return
	 */
	@Override
	public String[] getSQLsCreateIndex() {
		String[] index = getIndexs();
		if (index == null || index.length < 1) {
			return null;
		}
		int length = index.length;
		String[] SQLs = new String[length];
		String format = "index_%s_%s";
		for (int i = 0; i < length; i++) {
			String name = String.format(format, getTableName(), i);
			SQLs[i] = getSQLCreateIndex(name, index[i]);
		}
		return SQLs;
	}

	/**
	 * 生成建立索引的SQL
	 * 
	 * @param indexName
	 * @param index
	 * @return
	 */
	private String getSQLCreateIndex(String indexName, String index) {
		String f = "CREATE Unique INDEX %s ON  %s ( %s ) ";
		return String.format(f, indexName, getTableName(), index);
	}

	/**
	 * 合并字符串数组
	 * 
	 * @param args0
	 * @param args1
	 * @return
	 */
	private static String[] mergeArray(String[] args0, String[] args1) {
		int length0 = args0 != null ? args0.length : 0;
		int length1 = args1 != null ? args1.length : 0;
		String[] result = new String[length0 + length1];
		if (args0 != null) {
			for (int i = 0; i < args0.length; i++) {
				result[i] = args0[i];
			}
		}
		if (args1 != null) {
			for (int i = 0; i < args0.length; i++) {
				result[length0 + i] = args0[i];
			}
		}
		return result;
	}
}
