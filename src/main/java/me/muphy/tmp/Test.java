package me.muphy.tmp;

import me.muphy.config.ApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class Test {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/test")
    public String test(String s) {
        String str = "s:" + s;
        System.out.println(str);
        return str;
    }

    @RequestMapping("/dbStatus")
    public List<Map<String, Object>> dbTest(String s) {
        String sql = StringUtils.isEmpty(s) ? "select 'up' status" : s;
        return jdbcTemplate.queryForList(sql);
    }

    public void CongigTest(){
        Date date = new Date();
        double d = 1.0;
        int i = 0;
        long l = 1l;
        String a = "ddd";
    }

}
