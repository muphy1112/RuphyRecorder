package me.muphy.mapper;

import me.muphy.annotation.Pageable;
import me.muphy.entity.ClientRequestEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RemoteHostMapper {
    @Insert("insert into request_filter_remote(url, cookie, server_ip_addr, client_ip_addr, parameters, query_string, user, method, referrer) " +
            "values (#{url}, #{cookie}, #{serverIpAddr}, #{clientIpAddr}, #{parameters}, #{queryString}, #{user}, #{method}, #{referrer})")
    int SaveRemoteFilterInfo(ClientRequestEntity clientRequestEntity);

    @Insert("insert into request_filter_local(url, cookie, server_ip_addr, client_ip_addr, parameters, query_string, user, method, referrer) " +
            "values (#{url}, #{cookie}, #{serverIpAddr}, #{clientIpAddr}, #{parameters}, #{queryString}, #{user}, #{method}, #{referrer})")
    int SaveLocalFilterInfo(ClientRequestEntity clientRequestEntity);

    @Pageable
    @Select("select id, url, cookie, server_ip_addr, client_ip_addr, parameters, query_string, user, method, referrer, visit_time " +
            "from request_filter_remote where server_ip_addr like '%${ip}%' or client_ip_addr like '%${ip}%' order by id desc limit #{skip}, #{size}")
    @Results({@Result(property = "serverIpAddr", column = "server_ip_addr"), @Result(property = "clientIpAddr", column = "client_ip_addr")
            , @Result(property = "queryString", column = "query_string"), @Result(property = "visitTime", column = "visit_time")})
    List<ClientRequestEntity> queryRemoteFilterInfo(int skip, int size, String ip);

    @Pageable
    @Select("select id, url, cookie, server_ip_addr as serverIpAddr, client_ip_addr as clientIpAddr, parameters, query_string as queryString, user, method, referrer, visit_time as visitTime " +
            "from request_filter_local where server_ip_addr like '%${ip}%' or client_ip_addr like '%${ip}%' order by id desc limit ${skip}, #{size}")
    List<ClientRequestEntity> queryLocalFilterInfo(int skip, int size, String ip);


}