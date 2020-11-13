package me.muphy.mapper;

import me.muphy.entity.ColumnEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TableMapper {

    @Select("select t.table_name as tableName, t.column_name as columnName, t.data_type as dataType" +
            " from information_schema.columns t where t.table_name = #{tableName} ")
    List<ColumnEntity> getTableDescribe(String tableName);

}
