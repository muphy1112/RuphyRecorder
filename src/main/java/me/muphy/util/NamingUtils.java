package me.muphy.util;

public final class NamingUtils {
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

    public static boolean isEmpty(String name) {
        return null == name || name.trim().equals("");
    }

    public static String getCurrentPath(String... paths) {
        if (paths == null || paths.length == 0) {
            return ".";
        }
        String currentPath = "";
        for (String path : paths) {
            currentPath += "/" + path.replaceAll("\\\\+", "/")
                    .replaceAll("/+", "/");
        }
        return currentPath.substring(1)
                .replaceAll("/[^/]+/\\.\\./", "/")
                .replaceAll("/\\./", "");
    }

    private static String getTitleCase(String name) {
        if (isEmpty(name)) {
            return "";
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

}
