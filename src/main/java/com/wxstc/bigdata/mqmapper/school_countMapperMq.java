package com.wxstc.bigdata.mqmapper;

import com.wxstc.bigdata.entity.School_count;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface school_countMapperMq{
    @Select("SELECT * FROM ( SELECT SUM(number) AS value, province as name FROM school_count WHERE province <> '' GROUP BY province ) a WHERE a.value > #{min} AND a.value <= #{max}")
    public List<School_count> schoolCountByProvince(@Param("min") long min,@Param("max") long max);
}
