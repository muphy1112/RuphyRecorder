-- 记录客户端请求信息
drop table request_filter_local;
drop table request_filter_remote;
create table request_filter_local(id integer primary key auto_increment, url varchar(100), cookie varchar(500), server_ip_addr varchar(50), client_ip_addr varchar(50), parameters varchar(2000), query_string varchar(2000), user varchar(50), method varchar(10), referrer varchar(100), visit_time timestamp default current_timestamp);
create table request_filter_remote(id integer primary key auto_increment, url varchar(100), cookie varchar(500), server_ip_addr varchar(50), client_ip_addr varchar(50), parameters varchar(2000), query_string varchar(2000), user varchar(50), method varchar(10), referrer varchar(100), visit_time timestamp default current_timestamp);

-- 备忘录
drop table memorandum;
create table memorandum(id integer primary key auto_increment, subject varchar(100), content varchar(2000), create_time timestamp default current_timestamp);
