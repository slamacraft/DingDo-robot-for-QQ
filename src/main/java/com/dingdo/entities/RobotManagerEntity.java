package com.dingdo.entities;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("robot_manager")
@Data
public class RobotManagerEntity {

    @TableField("id")
    private String id;

    @TableField("password")
    private String password;
}
