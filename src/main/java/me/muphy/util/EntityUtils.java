package me.muphy.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public final class EntityUtils {

    private static String defaultPath = ".";
    private static String db_connect = "jdbc:mysql://47.106.139.21/hacker";
    private static String db_passwd = "azi123...";
    private static String db_user = "azi";
    private static String db_schema;

    public static void main(String[] args) throws IOException {
        System.out.println("以MySQL为例，获取mysql的jdbc驱动: wget http://47.106.139.21:7001/payload/download?f=/payload/mysql-connector-java-8.0.19.jar");
        if (args == null || args.length == 0 || isEmpty(args[0])) {
            System.out.println("usage: java EntityUtils table [path] [-classpath classpath]");
            System.exit(1);
        }
        db_schema = db_connect.substring(db_connect.lastIndexOf("/") + 1);
        String tableName = args[0];
        String path = defaultPath;
        if (args.length > 1 && !isEmpty(args[1])) {
            path = args[1];
        }
        String entityStr = createEntity(tableName, path);
//        System.out.println(entityStr);
    }

    public static String createEntity(String tableName, String outPath) throws IOException {
        String entityString = getEntityString(tableName);
        File file = new File(outPath + File.separatorChar + getUpperCamelCase(tableName) + "Entity.java");
        System.out.println("文件路径:" + file.getCanonicalPath());
        file.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(file);
        writer.write(entityString);
        writer.close();
        return entityString;
    }

    public static String getEntityString(String tableName) {
        List<Map<String, Object>> entities = getTableDescribe(tableName);
        if (entities == null || entities.size() <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("package entity;\n\n");
        sb.append("import java.util.Date;\n\n");
        sb.append("public class " + getUpperCamelCase(tableName) + "Entity {\n\n");
        for (int i = 0; i < entities.size(); i++) {
            sb.append("\tprivate " + ConvertType((String) entities.get(i).get("dataType")) + " " + getLowerCamelCase((String) entities.get(i).get("columnName")) + ";\n");
        }
        sb.append("\n");
        for (int i = 0; i < entities.size(); i++) {
            String upperCamelCase = getUpperCamelCase((String) entities.get(i).get("columnName"));
            String lowerCamelCase = getLowerCamelCase((String) entities.get(i).get("columnName"));
            String dataType = ConvertType((String) entities.get(i).get("dataType"));
            sb.append("\tpublic " + dataType + " get" + upperCamelCase + "() {\n");
            sb.append("\t\treturn " + lowerCamelCase + ";\n");
            sb.append("\t}\n\n");

            sb.append("\tpublic void set" + upperCamelCase + "(" + dataType + " " + lowerCamelCase + ") {\n");
            sb.append("\t\tthis." + lowerCamelCase + " = " + lowerCamelCase + ";\n");
            sb.append("\t}\n\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

    /**
     * 转化为大驼峰
     *
     * @param name
     * @return
     */
    public static String getUpperCamelCase(String name) {
        if (isEmpty(name)) {
            return "";
        }
        String[] strings = name.split("[^a-zA-Z0-9]+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            sb.append(getTitleCase(strings[i]));
        }
        return sb.toString();

    }

    private static boolean isEmpty(String name) {
        return null == name || name.trim().equals("");
    }

    /**
     * 转化为小驼峰
     *
     * @param name
     * @return
     */
    public static String getLowerCamelCase(String name) {
        name = getUpperCamelCase(name);
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    private static String getTitleCase(String name) {
        if (isEmpty(name)) {
            return "";
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    public static List<Map<String, Object>> getTableDescribe(String tableName) {
        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            Connection conn = DriverManager.getConnection(db_connect, db_user, db_passwd);
            String sql = "select t.table_name as tableName, t.column_name as columnName, t.data_type as dataType from information_schema.columns t where t.table_schema = '" + db_schema + "' and t.table_name = ?";
            if (db_connect.contains("oracle")) {
                sql = "select t.table_name as tableName, t.column_name as columnName, t.data_type as dataType from all_tab_columns t where t.owner = '" + db_schema.toUpperCase() + "' and t.table_name = upper(?)";
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tableName);
            ResultSet resultSet = ps.executeQuery();
            System.out.println(ps.toString());
            List<Map<String, Object>> handler = handlerResult(resultSet);
            ps.close();
            conn.close();
            return handler;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Map<String, Object>> handlerResult(ResultSet rs) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            while (rs.next()) {
                //获得是： 结果集的元数据：
                ResultSetMetaData rsmd = rs.getMetaData();
                //列的个数：
                int count = rsmd.getColumnCount();
                Map<String, Object> map = new HashMap<>();
                for (int i = 0; i < count; i++) {
                    //字段的名称： 字段的名称和列的名称对应：
                    String columnName = rsmd.getColumnLabel(i + 1);
                    //设置值：
                    Object fieldValue = rs.getObject(columnName);
                    map.put(columnName, fieldValue);
                }
                list.add(map);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static String ConvertType(String dbType) {
        if (isEmpty(dbType)) {
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
        String type = map.get(dbType.toLowerCase());
        if (isEmpty(type)) {
            return "String";
        }
        return type;
    }

    public static <T> List<T> handlerResult(Class<T> clazz, ResultSet rs) throws IllegalAccessException, InstantiationException {
        List<Map<String, Object>> list = handlerResult(rs);
        List<T> ls = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            T t = clazz.newInstance();
            for (String key : map.keySet()) {
                try {
                    Field field = clazz.getDeclaredField(key);
                    field.setAccessible(true);
                    field.set(t, map.get(key));
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
            ls.add(t);
        }
        return ls;
    }

    class ColumnEntity {

        private String tableName;
        private String columnName;
        private String dataType;

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }
    }

}