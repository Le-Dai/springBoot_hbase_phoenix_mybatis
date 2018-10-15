package com.wxstc.bigdata.controller;

import com.wxstc.bigdata.mqmapper.school_countMapperMq;
import com.wxstc.bigdata.phoenixdao.SCHOOL_COUNTMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PhoenixController {
    @Autowired
    private SCHOOL_COUNTMapper school_countMapper;

    @Autowired
    private school_countMapperMq school_countMapperMq;

    @RequestMapping("/phoenix/test")
    @ResponseBody
    public Object testphoenix(int type){
        if (type==1)
            return school_countMapper.findById(20);
        else if(type==2)
            return school_countMapper.getSchool_countHBByName("无锡科技职业学院");
        else if(type==3)
            return school_countMapper.getSchool_countHBs();
        else if(type==4)
            return school_countMapperMq.schoolCountByProvince(0,9999999);
        return "null";
    }
}
