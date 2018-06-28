package com.hejz.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;
import org.springframework.context.annotation.Configuration;

/**
 * 数据源
 */
@Configuration
public class HikariDataSourceFactory extends UnpooledDataSourceFactory {

  public HikariDataSourceFactory() {
    this.dataSource = new HikariDataSource();
  }
}
