package com.yepstudio.simpleorm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库操作的方法
 * @author zzljob@gmail.com
 * @date 2013-2-22
 *
 */
public abstract class EntityManager extends SQLiteOpenHelper {
	
	private static Logger logger = Logger.getLogger(EntityManager.class);
	
	/**
	 * 将需要创建或者需要重新创建的数据库的Repository注册进来
	 * @return
	 */
	public abstract List<EntityRepository> getRepositories();
    
    public EntityManager(Context context, String name, int version) {
    	super(context, name, null, version);
    }
    
    public EntityManager(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
    public void onCreate(SQLiteDatabase db) {
        Iterator<EntityRepository> it = getRepositories().listIterator();
        while (it.hasNext()) {
        	EntityRepository repository = it.next();
        	logger.debug(String.format("db execSQL to create tables:%s", repository.getSQLCreateTable()));
            db.execSQL(repository.getSQLCreateTable());
            String[] SQLs = repository.getSQLsCreateIndex();
            if(SQLs != null && SQLs.length > 0){
            	for (String SQL : SQLs) {
            		logger.debug(String.format("db execSQL to create index :%s", SQL));
            		db.execSQL(SQL);
            	}
            }
        }
    }

    @Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Iterator<EntityRepository> it = getRepositories().listIterator();
		while (it.hasNext()) {
			String SQL = String.format("DROP TABLE IF EXISTS %s", it.next().getTableName());
			logger.debug(String.format("db execSQL to upgrade tables:%s", SQL));
			db.execSQL(SQL);
		}
		onCreate(db);
	}
    
    /**
     * 执行一个SQL语句，不要执行Select语句就好 
     * @param sql 
     */
    public void execSQL(String sql){
    	logger.debug(String.format("db execSQL:%s", sql));
    	getWritableDatabase().execSQL(sql);
    }

    /**
     * <p>插入数据库记录</p>
     * <ui>
     * 	<li>如果只在Link字段上建立了索引，那么遇到Link相同的会插入失败</li>
     * 	<li>插入后自增主键会自动赋值</li>
     * </ul>
     * @param entity 实体
     * @return 插入是否成功
     */
	public boolean persist(Entity entity) {
		SQLiteDatabase db = this.getWritableDatabase();
		EntityRepository repository = entity.getRepository();
		ContentValues values = repository.getContentValues(entity, true);
		long result = db.insert(repository.getTableName(), null, values);
		if (result == -1) {
			logger.debug(String.format("persist entity fail:%s", entity.toString()));
		} else {
			logger.debug(String.format("persist %s success:%s", repository.getTableName(), entity.toString()));
			entity.setId(result);
		}
		db.close();
		return result != -1;
	}

    /**
     * <p>更新数据库记录</p>
     * <ul>
     * 	<li>根据主键来更新</li>
     * 	<li>null是一个有效的值,数据库将直接被更新成为空</li>
     * </ul>
     * @param entity 实体
     * @return 更新的条数
     */
	public int update(Entity entity) {
		EntityRepository repository = entity.getRepository();
		String where = String.format("%s = ?", repository.getKeyId());
		String[] whereArgs = new String[] { String.valueOf(entity.getId()) };
		ContentValues values = repository.getContentValues(entity, false);
		return update(entity.getRepository(), values, where, whereArgs);
	}
	
	/**
	 * <p>更新数据库记录</p>
     * <ul>
     * 	<li>根据主键来更新</li>
     * 	<li>null是一个有效的值,数据库将直接被更新成为空</li>
     * 	<li>更新指定的字段</li>
     * </ul>
	 * @param entity 实体
	 * @param updateKey 要更新的字段数组
	 * @return 更新的条数
	 */
	public int update(Entity entity, Set<String> updateKey) {
		EntityRepository repository = entity.getRepository();
		ContentValues values = repository.getContentValues(entity, false);
		for (String key : repository.getAllKeys()) {
			if (!updateKey.contains(key)) {
				values.remove(key);
			}
		}
		String where = String.format("%s = ? ", repository.getKeyId());
		String[] whereArgs = new String[] { String.valueOf(entity.getId()) };
		return update(entity.getRepository(), values, where, whereArgs);
	}
    
	/**
	 * 更新数据库记录
	 * @param repository EntityRepository
	 * @param values ContentValues字段的值
	 * @param where where条件，如：_id=?
	 * @param whereArgs where条件里边的参数
	 * @return 更新的条数
	 */
	public int update(EntityRepository repository, ContentValues values, String where, String[] whereArgs) {
		SQLiteDatabase db = this.getWritableDatabase();
		logger.debug(String.format("update entity:%s, values:%s, where:%s, whereArgs:[%s].", repository.getTableName(), values, where, StringUtils.join(whereArgs, ",")));
		int result = db.update(repository.getTableName(), values, where, whereArgs);
		db.close();
		return result;
	}

    /**
     * <p>删除记录</p>
     * <ol>
     * <li>根据主键来删除数据</li>
     * </ol>
     * @param entity
     */
	public void remove(Entity entity) {
		EntityRepository repository = entity.getRepository();
		String where = String.format("%s = ? ", repository.getKeyId());
		String[] whereArgs = new String[] { String.valueOf(entity.getId()) };
		remove(repository, where, whereArgs);
	}
    
	/**
	 * <p>删除记录</p>
	 * @param repository EntityRepository
	 * @param where where条件，如：_id=?
	 * @param whereArgs where条件里边的参数
	 * @return 更新的条数
	 */
    public int remove(EntityRepository repository, String where, String[] whereArgs) {
		SQLiteDatabase db = this.getWritableDatabase();
		logger.debug(String.format("delete entity:%s, where:%s, whereArgs:[%s].", repository.getTableName(), where, StringUtils.join(whereArgs, ",")));
		int result = db.delete(repository.getTableName(), where, whereArgs);
		db.close();
		return result;
	}

    /**
     * <p>根据主键ID查询</p>
     * @param repository
     * @param id
     * @return
     */
	public Entity findById(EntityRepository repository, int id) {
		String where = String.format(" %s = ? ", repository.getKeyId());
		String[] whereArgs = new String[] { String.valueOf(id) };
		return findOne(repository, where, whereArgs);
	}

   /**
    * 根据某一个字段来查询一个实体
    * @param repository
    * @param key 字段名 
    * @param value 字段值
    * @return Entity
    */
    public Entity findOne(EntityRepository repository, String key, String value) {
    	String where = String.format(" %s = ? ", key);
		String[] whereArgs = new String[] { value };
    	return findOne(repository, where, whereArgs);
    }
    
    /**
     * 根据条件来查询一个实体
     * @param repository
     * @param where
     * @param whereArgs
     * @return Entity
     */
	public Entity findOne(EntityRepository repository, String where, String[] whereArgs) {
		String orderBy = String.format(" %s DESC ", repository.getKeyId());
		List<? extends Entity> list = find(repository, where, whereArgs, orderBy, 0, 1);
		if (list == null || list.size() < 1) {
			return null;
		} else {
			return list.get(0);
		}
	}
    
	/**
	 * 根据条件来查询一批实体
	 * @param repository
	 * @param key
	 * @param value
	 * @return
	 */
    public List<? extends Entity> findBy(EntityRepository repository, String key, String value) {
    	String where = String.format(" %s = ? ", key);
		String[] whereArgs = new String[] { value };
    	String orderBy = String.format(" %s ASC ", repository.getKeyId());
    	return find(repository, where, whereArgs, orderBy, 0, -1);
    }
    
    /**
     * 根据条件来查询一批实体
     * @param repository
     * @param where
     * @param whereArgs
     * @return
     */
    public List<? extends Entity> findByAnd(EntityRepository repository, String where, String[] whereArgs) {
        String orderBy = String.format(" %s ASC ", repository.getKeyId());
    	return find(repository, where, whereArgs, orderBy, 0, -1);
    }

    /**
     * 不带条件的分页查询
     * @param repository
     * @param start 开始的位置
     * @param size 长度 -1表示没有限制
     * @return
     */
	public List<? extends Entity> find(EntityRepository repository, int start, int size) {
		String orderBy = String.format("%s ASC ", repository.getKeyId());
		return find(repository, null, null, orderBy, start, size);
	}
    
    /**
     * 带条件的分页查询
     * @param repository
     * @param where
     * @param whereArgs
     * @param orderBy
     * @param start
     * @param size
     * @return
     */
	public List<? extends Entity> find(EntityRepository repository, String where, String[] whereArgs, String orderBy, int start, int size) {
		SQLiteDatabase db = this.getReadableDatabase();
		String table = repository.getTableName();
		String limit = size < 0 ? null : String.format("%s,%s", start, size);
		logger.debug(String.format("find entity:%s, where:%s, whereArgs:[%s], orderBy:%s, limit:%s.", table, where, StringUtils.join(whereArgs, ","), orderBy, limit));
		Cursor cursor = db.query(table, repository.getAllKeys(), where, whereArgs, null, null, orderBy, limit);
		List<Entity> result = new ArrayList<Entity>();
		if (cursor.moveToFirst()) {
			do {
				result.add(repository.buildEntityFromCursorData(cursor));
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return result;
	}
    
	/**
	 * 根据自定义SQL来查询
	 * @param sql
	 * @param selectionArgs
	 * @return
	 */
	public Cursor findBySQL(String sql, String[] selectionArgs) {
		SQLiteDatabase db = this.getReadableDatabase();
		logger.debug(String.format("findBySQL :%s, whereArgs:[%s].", sql, StringUtils.join(selectionArgs, ",")));
		return db.rawQuery(sql, selectionArgs);
	}

    /**
     * 进行Count查询
     * @param repository
     * @return
     */
    public int count(EntityRepository repository) {
        String orderBy = String.format("%s ASC", repository.getKeyId());
        return count(repository, null, null, orderBy);
    }
    
    /**
     * 进行Count查询
     * @param repository
     * @param where
     * @param whereArgs
     * @param orderBy
     * @return 
     */
    public int count(EntityRepository repository, String where, String[] whereArgs, String orderBy) {
    	SQLiteDatabase db = this.getReadableDatabase();
    	String table = repository.getTableName();
    	logger.debug(String.format("count entity:%s, where:%s, whereArgs:[%s], orderBy:%s.", table, where, StringUtils.join(whereArgs, ","), orderBy));
    	Cursor cursor = db.query(table, repository.getAllKeys(), where, whereArgs, null, null, orderBy, null);
    	int result = cursor.getCount();
    	cursor.close();
    	db.close();
    	return result;
    }
}
