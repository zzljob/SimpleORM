package com.yepstudio.simpleorm.demo.entities;

import android.content.ContentValues;
import android.database.Cursor;

import com.yepstudio.simpleorm.BaseEntityRepository;
import com.yepstudio.simpleorm.Entity;

/**
 * 定义实体在数据库中的存储方式
 * <ol>
 * <li>指明City这个实体的数据存在哪个表里边</li>
 * <li>指明CIty每个字段对应着数据库哪个字段</li>
 * <li>指明表要建立的索引</li>
 * <li>指明数据是从数据库字段对应到实体City的</li>
 * </ol>
 * @author zzljob@gmail.com
 * @date 2013-2-23
 * 
 */
public class CityRepository extends BaseEntityRepository {

	private static CityRepository instance;

	public final static String KEY_NAME = "name";
	public final static String KEY_PINYIN = "pinyin";
	public final static String KEY_GROUP = "group_name";//字段不能为group，因为数据库保留字符

	private CityRepository() {
		super();
	}

	public static CityRepository getInstance() {
		if (instance == null) {
			instance = new CityRepository();
		}
		return instance;
	}

	@Override
	protected String[] getKeys() {
		return new String[] { KEY_NAME, KEY_PINYIN, KEY_GROUP };
	}

	@Override
	/**
	 * 每个字段对应的数据库字段类型
	 */
	protected String[] getKeyTypes() {
		return new String[] { "VARCHAR", "VARCHAR", "VARCHAR" };
	}

	@Override
	protected Entity buildEntity(Cursor cursor) {
		City c = new City();
		c.setName(this.getStringFromCursor(cursor, KEY_NAME));
		c.setPinyin(this.getStringFromCursor(cursor, KEY_PINYIN));
		c.setGroup(this.getStringFromCursor(cursor, KEY_GROUP));
		return c;
	}

	@Override
	protected ContentValues getValues(Entity entity) {
		City c = (City) entity;
		ContentValues v = new ContentValues();
		v.put(KEY_NAME, c.getName());
		v.put(KEY_PINYIN, c.getPinyin());
		v.put(KEY_GROUP, c.getGroup());
		return v;
	}

}
