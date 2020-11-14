package me.muphy.servicce;

import me.muphy.entity.ColumnEntity;
import me.muphy.mapper.TableMapper;
import me.muphy.util.NamingUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TableService {

    @Autowired
    private TableMapper tableMapper;
    @Value("${app.autogenerate.entity-path:.}")
    private String entityPath;
    private Map<String, String> typeMap;
    private final Logger logger = LoggerFactory.getLogger(TableService.class);

    public List<ColumnEntity> getTableDescribe(String tableName, String schema) {
        return tableMapper.getTableDescribe(tableName, schema);
    }

    public boolean createAllEntity(String schema) {
        List<String> tables = tableMapper.getTablesFromSchema(schema);
        String currentPath = NamingUtils.getCurrentPath(entityPath);
        File parentFile = new File(currentPath);
        if (tables != null && parentFile.exists() && parentFile.isDirectory()) {
            File[] files = parentFile.listFiles();
            for (File file : files) {
                String fileName = file.getName().toLowerCase().replace("entity.java", "");
                for (int i = 0; i < tables.size(); i++) {
                    if (fileName.equals(tables.get(i).replaceAll("[^a-zA-Z0-9]+", ""))) {
                        logger.info("移除已创建的表：" + tables.get(i));
                        tables.remove(i);
                        break;
                    }
                }
            }
        } else {
            parentFile.mkdirs();
        }
        logger.info("待创建的表：" + Strings.join(tables, ','));
        for (String table : tables) {
            createEntity(table, schema);
        }
        return true;
    }

    public boolean createEntity(String tableName, String schema) {
        return createEntity(tableName, schema, null);
    }

    public boolean createEntity(String tableName, String schema, String path) {
        if (StringUtils.isEmpty(path)) {
            path = this.entityPath;
        }
        String filePath = NamingUtils.getCurrentPath(path, NamingUtils.getUpperCamelCase(tableName) + "Entity.java");
        File file = new File(filePath);
        if (file.exists()) {
            logger.info("文件已经存在：" + filePath);
            return false;
        }
        String pkgName = path.replaceAll(".+/src/main/java/(.+)$", "$1");
        if (pkgName.equals(path)) {
            pkgName = "entity";
        } else {
            pkgName = pkgName.replaceAll("/$", "").replaceAll("/", ".");
        }
        String entityString = getEntityString(tableName, schema, pkgName);
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(entityString);
            fileWriter.close();
        } catch (IOException e) {
            logger.info("创建文件失败：" + e.getMessage());
            return false;
        }
        logger.info("创建文件成功：" + filePath);
        return true;
    }

    public String getEntityString(String tableName, String schema, String pkgName) {
        if (StringUtils.isEmpty(pkgName)) {
            pkgName = "entity";
        }
        List<ColumnEntity> entities = getTableDescribe(tableName, schema);
        if (entities == null || entities.size() <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        StringBuilder msb = new StringBuilder();
        sb.append("package " + pkgName + ";\n\n");
        sb.append("import java.util.Date;\n\n");
        sb.append("public class " + NamingUtils.getUpperCamelCase(tableName) + "Entity {\n\n");
        for (int i = 0; i < entities.size(); i++) {
            String fieldUpperCamelCase = NamingUtils.getUpperCamelCase(entities.get(i).getColumnName());
            String fieldLowerCamelCase = NamingUtils.getLowerCamelCase(entities.get(i).getColumnName());
            String type = ConvertType(entities.get(i).getDataType());
            sb.append("\tprivate " + type + " " + fieldLowerCamelCase + ";\n");
            msb.append("\tpublic " + type + " get" + fieldUpperCamelCase + "() {\n");
            msb.append("\t\treturn " + fieldLowerCamelCase + ";\n");
            msb.append("\t}\n\n");
            msb.append("\tpublic void set" + fieldUpperCamelCase + "(" + type + " " + fieldLowerCamelCase + ") {\n");
            msb.append("\t\tthis." + fieldLowerCamelCase + " = " + fieldLowerCamelCase + ";\n");
            msb.append("\t}\n\n");
        }
        sb.append("\n");
        sb.append(msb.toString());
        sb.append("}\n");
        return sb.toString();
    }

    private String ConvertType(String dbType) {
        if (StringUtils.isEmpty(dbType)) {
            return "String";
        }
        if (this.typeMap == null) {
            this.typeMap = new HashMap<>();
            this.typeMap.put("varchar", "String");
            this.typeMap.put("longtext", "String");
            this.typeMap.put("text", "String");
            this.typeMap.put("char", "String");
            this.typeMap.put("longblob", "String");
            this.typeMap.put("mediumtext", "String");
            this.typeMap.put("blob", "String");
            this.typeMap.put("set", "String");
            this.typeMap.put("enum", "String");
            this.typeMap.put("bigint", "int");
            this.typeMap.put("int", "int");
            this.typeMap.put("bit", "int");
            this.typeMap.put("smallint", "int");
            this.typeMap.put("tinyint", "int");
            this.typeMap.put("decimal", "long");
            this.typeMap.put("double", "double");
            this.typeMap.put("time", "Date");
            this.typeMap.put("timestamp", "Date");
            this.typeMap.put("datetime", "Date");
        }
        String type = typeMap.get(dbType);
        if (StringUtils.isEmpty(type)) {
            return "String";
        }
        return type;
    }

}
