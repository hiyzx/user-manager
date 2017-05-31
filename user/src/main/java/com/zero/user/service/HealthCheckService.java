package com.zero.user.service;

import com.zaxxer.hikari.HikariDataSource;
import com.zero.user.util.HttpClient;
import com.zero.util.JsonHelper;
import com.zero.util.RedisHelper;
import com.zero.vo.HealthCheckVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/5/18
 */
@Service
public class HealthCheckService {

    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckService.class);
    @Resource
    private HikariDataSource masterDataSource;
    @Resource
    private HttpClient mailHttpClient;

    private HealthCheckVo checkDBConnection() {
        String url = masterDataSource.getJdbcUrl();
        String driver = masterDataSource.getDriverClassName();
        String user = masterDataSource.getUsername();
        String password = masterDataSource.getPassword();
        Connection conn = null;
        Statement statement = null;
        HealthCheckVo model = new HealthCheckVo();
        model.setServiceName(url);
        try {
            long e = System.currentTimeMillis();
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
            statement = conn.createStatement();
            statement.executeQuery("select 1");
            model.setCostTime(String.valueOf(System.currentTimeMillis() - e));
            model.setNormal(true);
        } catch (Exception e) {
            LOG.error("[checkDB]发生异常", e);
            model.setNormal(false);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                LOG.error("[checkDB]关闭资源发生异常", e);
            }
        }
        return model;
    }

    public void quartzHealthCheck() throws ParseException {
        System.out.println(JsonHelper.toJSon(commonHealthCheck()));
    }

    public List<HealthCheckVo> healthCheck() throws ParseException {
        return commonHealthCheck();
    }

    private List<HealthCheckVo> commonHealthCheck() throws ParseException {
        List<HealthCheckVo> healthCheckVos = new ArrayList<>();
        healthCheckVos.add(mailHttpClient.healthCheck());
        healthCheckVos.add(RedisHelper.checkRedisConnection());
        healthCheckVos.add(checkDBConnection());
        return healthCheckVos;
    }
}
