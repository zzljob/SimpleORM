package com.yepstudio.simpleorm.demo.entities;

import com.yepstudio.simpleorm.Entity;
import com.yepstudio.simpleorm.EntityRepository;

/**
 * 铁道部上扒下来的城市数据
 * @author zzljob@gmail.com
 * @date 2013-4-26
 *
 */
public class City extends Entity {
	
	private static final long serialVersionUID = 6917975920582161498L;
	
	private String name;
	private String pinyin;
	private String simplePinyin;
	private String group;

	@Override
	public EntityRepository getRepository() {
		return CityRepository.getInstance();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getSimplePinyin() {
		return simplePinyin;
	}

	public void setSimplePinyin(String simplePinyin) {
		this.simplePinyin = simplePinyin;
	}

	@Override
	public String toString() {
		return "City [name=" + name + ", pinyin=" + pinyin + ", simplePinyin="
				+ simplePinyin + ", group=" + group + "]";
	}
}
