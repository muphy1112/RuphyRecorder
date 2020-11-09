package me.muphy.mapper;

import me.muphy.annotation.Pageable;
import me.muphy.entity.MemorandumEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@Pageable
public interface MemorandumMapper {

    @Insert("insert into memorandum(subject, content) values( #{subject}, #{content}")
    int saveMemorandum(MemorandumEntity entity);

    @Select("select id, subject, content, create_time as createTable from memorandum where subject like '%${subject}%' limit #{skip}, #{size}")
    @Pageable
    List<MemorandumEntity> queryMemorandum(int skip, int size, String subject);

}
