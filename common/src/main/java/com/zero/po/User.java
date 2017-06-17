package com.zero.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zero.util.TimeZone;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class User {

	private static final String FORMAT = "yyyy-MM-dd HH:mm";
	private Integer id;

	@ApiModelProperty(value = "年龄") private Integer age;

	@ApiModelProperty(value = "名字") private String name;

	@ApiModelProperty(value = "手机号") private String phone;

	@ApiModelProperty(value = "密码") private String password;

	@ApiModelProperty(value = "邮箱") private String email;

	private String headImg;

	@ApiModelProperty(value = "最后登陆时间") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FORMAT, timezone = TimeZone.TIMEZONE) private Date lastLoginTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone == null ? null : phone.trim();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password == null ? null : password.trim();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email == null ? null : email.trim();
	}

	public String getHeadImg() {
		return headImg;
	}

	public void setHeadImg(String headImg) {
		this.headImg = headImg == null ? null : headImg.trim();
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
}