package com.zero.user.vo.dto;

/**
 * Created by HP on 2017/4/30.
 */
public class UserDto {

    @io.swagger.annotations.ApiModelProperty(value = "年龄")
    private Integer age;

    @io.swagger.annotations.ApiModelProperty(value = "名字")
    private String name;

    @io.swagger.annotations.ApiModelProperty(value = "手机号")
    private String phone;

    @io.swagger.annotations.ApiModelProperty(value = "密码")
    private String password;

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
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
