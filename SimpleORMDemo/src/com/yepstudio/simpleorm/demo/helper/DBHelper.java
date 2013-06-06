package com.yepstudio.simpleorm.demo.helper;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import android.content.Context;
import android.os.Environment;

import com.yepstudio.simpleorm.EntityManager;
import com.yepstudio.simpleorm.EntityRepository;
import com.yepstudio.simpleorm.demo.entities.CityRepository;

/**
 * 数据库操作类
 * <ol>
 * <li>这个类必须要有的,使用的时候就是使用这个类去增删查改数据</li>
 * <li>指定数据库版本</li>
 * <li>数据库版本更新的策略是删除旧表再建立新表，如果想自己去迁移数据的话，重写onUpgrade方法</li>
 * <li>指定数据库文件存放的地方</li>
 * <li>有那几个EntityRepository要注册进来</li>
 * </ol>
 * @author zzljob@gmail.com
 * @date 2013-2-22
 * 
 */
public class DBHelper extends EntityManager {
	
	private static Logger logger = Logger.getLogger(DBHelper.class);
	public static final String DATABASE_NAME;
	private static final int DATABASE_VERSION = 12;
	
	private static DBHelper instance;
	
	static {
		String path = Environment.getDataDirectory().getPath();
		DATABASE_NAME = path + "/data/com.yepstudio.simpleorm/db";
		logger.debug(String.format("database path:%s, version:%s.", DATABASE_NAME, DATABASE_VERSION));
	}

	private DBHelper(Context context) {
		super(context, DATABASE_NAME, DATABASE_VERSION);
	}

	public static DBHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DBHelper(context);
		}
		return instance;
	}

	@Override
	/**
	 * 将EntityRepository类注册进来,有几个用途:
	 * <ol>
	 * <li>当数据库内没有这个表的时候要依靠EntityRepository来建立表</li>
	 * <li>当数据库要更新的时候要依靠EntityRepository来更新表</li>
	 * <li>数据库版本更新的时候某些EntityRepository可以不注册进来，那样那些表将不会被更新</li>
	 * </ol>
	 */
	public List<EntityRepository> getRepositories() {
		List<EntityRepository> list = new ArrayList<EntityRepository>();
		list.add(CityRepository.getInstance());
		for (EntityRepository er : list) {
			logger.debug(String.format("register EntityRepository :%s, tableName:%s.", er.getClass().getSimpleName(), er.getTableName()));
		}
		return list;
	}
}
