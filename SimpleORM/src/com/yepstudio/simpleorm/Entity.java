package com.yepstudio.simpleorm;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据库实体基类,这个基类会帮实体自动建立主键,创建日期，还有备注这些字段
 * @author zzljob@gmail.com
 * @date 2013-5-29
 *
 */
public abstract class Entity implements Serializable {
	
	private static final long serialVersionUID = -7328147694197695006L;
	
	private Long id;
	private Date createDate;
	private String remark;
	
	public abstract EntityRepository getRepository();
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
