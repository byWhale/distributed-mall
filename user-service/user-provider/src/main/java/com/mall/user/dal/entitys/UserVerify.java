package com.mall.user.dal.entitys;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_user_verify")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserVerify {
    @Id
    private Long id;

    private String username;

    //注册时生成的唯一号
    private String uuid;

    //注册时间
    @Column(name = "register_date")
    private Date registerDate;

    //是否验证过期
    @Column(name = "is_verify")
    private String isVerify;

    //是否过期
    @Column(name = "is_expire")
    private String isExpire;


}