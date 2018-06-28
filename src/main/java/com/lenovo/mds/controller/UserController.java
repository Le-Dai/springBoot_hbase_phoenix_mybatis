package com.lenovo.mds.controller;

import com.lenovo.mds.dao.UserInfoMapper;
import com.lenovo.mds.entity.UserInfo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequestMapping("user")
@RestController
public class UserController {

    @Autowired
    UserInfoMapper userInfoMapper;

    @RequestMapping("getAll")
    public List<UserInfo> getAll(){
        return userInfoMapper.getUsers();
    }

    @GetMapping("getById/{id}")
    public UserInfo getById(@PathVariable("id") int id){
        return userInfoMapper.findById(id);
    }

    @GetMapping("page/{pageNum}/{pageSize}")
    public List<UserInfo> page(@PathVariable("pageNum") int pageNum,@PathVariable("pageSize") int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<UserInfo> users = userInfoMapper.getUsers();
        PageInfo page=new PageInfo(users);
        return page.getList();
    }

}
