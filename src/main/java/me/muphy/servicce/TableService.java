package me.muphy.servicce;

import me.muphy.entity.ColumnEntity;
import me.muphy.mapper.TableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;

@Service
public class TableService {

    @Autowired
    private TableMapper tableMapper;

    public List<ColumnEntity> getTableDescribe(String tableName){
        return tableMapper.getTableDescribe(tableName);
    }

    public String ConvertType(String dbType) {
        if (StringUtils.isEmpty(dbType)) {
            return "String";
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("varchar", "String");
        map.put("longtext", "String");
        map.put("text", "String");
        map.put("char", "String");
        map.put("longblob", "String");
        map.put("mediumtext", "String");
        map.put("blob", "String");
        map.put("set", "String");
        map.put("enum", "String");
        map.put("bigint", "int");
        map.put("int", "int");
        map.put("bit", "int");
        map.put("smallint", "int");
        map.put("tinyint", "int");
        map.put("decimal", "long");
        map.put("double", "double");
        map.put("time", "Date");
        map.put("timestamp", "Date");
        map.put("datetime", "Date");
        String type = map.get(dbType);
        if (StringUtils.isEmpty(type)) {
            return "String";
        }
        return type;
    }

    public String createEntity(String tableName) {
        List<ColumnEntity> entities = getTableDescribe(tableName);
        if (entities == null || entities.size() <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("package entity;\n\n");
        sb.append("import java.util.Date;\n\n");
        sb.append("public class " + getFistUpperCase(tableName) + " {\n\n");
        for (int i = 0; i < entities.size(); i++) {
            sb.append("\tprivate " + ConvertType(entities.get(i).getDataType()) + " " + entities.get(i).getColumnName() + ";\n");
        }
        sb.append("\n");
        for (int i = 0; i < entities.size(); i++) {
            String field = entities.get(i).getColumnName();
            sb.append("\tpublic " + ConvertType(entities.get(i).getDataType()) + " get" + getFistUpperCase(field) + "() {\n");
            sb.append("\t\treturn " + field + ";\n");
            sb.append("\t}\n");

            sb.append("\tpublic void set" + getFistUpperCase(field) + "(" + ConvertType(entities.get(i).getDataType()) + " " + field + ") {\n");
            sb.append("\t\tthis." + field + " = " + field + ";\n");
            sb.append("\t}\n");
        }
        sb.append("\n}\n");
        return sb.toString();
    }

    private static String getFistUpperCase(String name) {
        if (StringUtils.isEmpty(name)) {
            return "";
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

}
