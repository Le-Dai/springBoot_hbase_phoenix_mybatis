package com.wxstc.bigdata.phoenixdao;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 *
 */
@Mapper
public interface SCHOOL_COUNTMapper {

  @Insert("upsert into OUTPUT_SCHOOL_COUNT (ID,SCHOOLNAME) VALUES (#{scool_count.id},#{scool_count.schoolName})")
  public void addUser(@Param("scool_count") School_countHB userInfo);

  @Delete("delete from OUTPUT_SCHOOL_COUNT WHERE ID=#{id}")
  public void deleteUser(@Param("id") int id);

  @Results( value = {
          @Result(property = "id", column = "ID", id = true),
          @Result(property = "schoolName", column = "SCHOOLNAME")
  })
  @Select("select * from OUTPUT_SCHOOL_COUNT WHERE ID=#{id}")
  public School_countHB findById(int id);

  @Results( value = {
          @Result(property = "id", column = "ID", id = true),
          @Result(property = "name", column = "NAME")
  })
  @Select("select * from OUTPUT_SCHOOL_COUNT WHERE SCHOOLNAME=#{name}")
  public School_countHB getSchool_countHBByName(@Param("name") String name);

  @Results( value = {
          @Result(property = "id", column = "ID", id = true),
          @Result(property = "name", column = "NAME")
  })
  @Select("select * from OUTPUT_SCHOOL_COUNT")
  public List<School_countHB> getSchool_countHBs();

 @Results( value = {
         @Result(property = "id", column = "ID", id = true),
         @Result(property = "name", column = "NAME")
 })
 @Select("select * from OUTPUT_SCHOOL_COUNT left join bj on user.id= bj.user_id where id=3")
 public Map getUsers1();
}
