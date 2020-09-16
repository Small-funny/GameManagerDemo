package Server.DatabaseHelper;

import org.jdom2.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;

/**
 * GM管理系统数据库基本操作帮助类
 */
public class ManagerDatabaseHelper {

    /**
     * 查询所有用户信息
     *
     * @return
     */
    public static HashMap<String, String> allManagerInfo() {
        HashMap<String, String> result = new HashMap<>();
        List<List<String>> tableBody = new ArrayList<>();
        List<Element> data = DatabaseConstants.loadDatabase().getChildren();
        int endIndex = DatabaseConstants.INDEX_OF_PASSWORD + 1;
        List<String> colName = new ArrayList<>(DatabaseConstants.HEADER_LIST.subList(0, endIndex));
        try {
            result.put("colName", JSON.toJSONString(colName));
            for (Element record : data) {
                List<String> rowData = new ArrayList<>();
                for (int index = 0; index <= DatabaseConstants.INDEX_OF_PASSWORD; index ++) {
                    String row = record.getChildren().get(index).getAttributeValue("value");
                    if (!"root".equals(row)) {
                        rowData.add(row);
                    }
                }
                tableBody.add(rowData);
            }
            result.put("tableBody", JSON.toJSONString(tableBody));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除用户权限
     *
     * @param username
     * @param server
     * @param auth
     */
    public static void deleteAuth(String username, String server, String auth) {
        Element rootData = DatabaseConstants.loadDatabase();
        try {
            for (Element record : rootData.getChildren()) {
                Element authElement = record.getChildren().get(DatabaseConstants.INDEX_OF_AUTH);
                Element unameElement = record.getChildren().get(DatabaseConstants.INDEX_OF_USERNAME);
                if (username.equals(unameElement.getAttributeValue("value"))) {
                    for (Element serverAuth : authElement.getChildren()) {
                        if (server.equals(serverAuth.getAttributeValue("value", server))) {
                            for (Element auth2 : serverAuth.getChildren()) {
                                if (auth.equals(auth2.getAttributeValue("value"))) {
                                    serverAuth.removeContent(auth2);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
            }
            DatabaseConstants.saveXml(rootData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加用户权限
     *
     * @param username
     * @param server
     * @param auth
     */
    public static void addAuth(String username, String server, String auth) {
        Element newAuth = new Element("auth2");
        newAuth.setAttribute("name", "list");
        newAuth.setAttribute("value", auth);
        Element rootData = DatabaseConstants.loadDatabase();
        try {
            for (Element record : rootData.getChildren()) {
                Element authElement = record.getChildren().get(DatabaseConstants.INDEX_OF_AUTH);
                Element unameElement = record.getChildren().get(DatabaseConstants.INDEX_OF_USERNAME);
                if (username.equals(unameElement.getAttributeValue("value"))) {
                    for (Element serverAuth : authElement.getChildren()) {
                        if (server.equals(serverAuth.getAttributeValue("value"))) {
                            serverAuth.addContent(newAuth);
                            break;
                        }
                    }
                    break;
                }
            }
            DatabaseConstants.saveXml(rootData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除用户所有相关记录
     *
     * @param username
     */
    public static void deleteUser(String username) {
        Element rootData = DatabaseConstants.loadDatabase();
        try {
            for (Element record : rootData.getChildren()) {
                Element unameElement = record.getChildren().get(DatabaseConstants.INDEX_OF_USERNAME);
                if (username.equals(unameElement.getAttributeValue("value"))) {
                    rootData.removeContent(record);
                }
            }
            DatabaseConstants.saveXml(rootData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 增加新用户
     *
     * @param userInfo
     */
    public static void addUser(List<String> userInfo) {
        Element newUser = new Element("record");
        Element rootData = DatabaseConstants.loadDatabase();
        try {
            for (int colIndex = 0; colIndex < DatabaseConstants.HEADER_LIST.size(); colIndex++) {
                Element column = new Element("cloumn");
                column.setAttribute("name", DatabaseConstants.HEADER_LIST.get(colIndex));
                if (!"auth".equals(DatabaseConstants.HEADER_LIST.get(colIndex))) {
                    column.setAttribute("value", userInfo.get(colIndex));
                }
                newUser.addContent(column);
            }
            for (String server : DatabaseConstants.SERVER_LIST) {
                Element auth = new Element("auth1");
                auth.setAttribute("name", "server");
                auth.setAttribute("value", server);
                newUser.getChildren().get(DatabaseConstants.INDEX_OF_AUTH).addContent(auth);
            }
            rootData.addContent(newUser);
            DatabaseConstants.saveXml(rootData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改用户密码
     *
     * @param username
     * @param password
     */
    public static void updateUserInfo(String username, String password) {
        Element rootData = DatabaseConstants.loadDatabase();
        try {
            for (Element record : rootData.getChildren()) {
                Element unameElement = record.getChildren().get(DatabaseConstants.INDEX_OF_USERNAME);
                if (username.equals(unameElement.getAttributeValue("value"))) {
                    record.getChildren().get(DatabaseConstants.INDEX_OF_PASSWORD).setAttribute("value", password);
                    break;
                }
            }
            DatabaseConstants.saveXml(rootData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 按类型获取权限(列表)
     * 
     * @param username
     * @param server
     * @param type
     * @return
     */
    public static List<String> selectAuthList(String username, String type, String server) {
        List<String> result = new ArrayList<>();
        List<Element> data = DatabaseConstants.loadDatabase().getChildren();
        try {
            for (Element record : data) {
                Element unameElement = record.getChildren().get(DatabaseConstants.INDEX_OF_USERNAME);
                Element authElement = record.getChildren().get(DatabaseConstants.INDEX_OF_AUTH);
                Element serverElement = authElement.getChildren().get(DatabaseConstants.SERVER_LIST.indexOf(server));
                if (username.equals(unameElement.getAttributeValue("value"))) {
                    for (Element auth : serverElement.getChildren()) {
                        if (type.equals(auth.getAttributeValue("name"))) {
                            result.add(auth.getAttributeValue("value"));
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取所有权限(表格)
     *
     * @param username
     * @param server
     * @return
     */
    public static HashMap<String, String> selectAuthTable(String username, String server) {
        HashMap<String, String> result = new HashMap<>();
        List<String> colName = Arrays.asList("权限", "类型");
        List<List<String>> body = new ArrayList<>();
        List<Element> data = DatabaseConstants.loadDatabase().getChildren();
        try {
            for (Element record : data) {
                Element unameElement = record.getChildren().get(DatabaseConstants.INDEX_OF_USERNAME);
                Element authElement = record.getChildren().get(DatabaseConstants.INDEX_OF_AUTH);
                Element serverElement = authElement.getChildren().get(DatabaseConstants.SERVER_LIST.indexOf(server));
                if (username.equals(unameElement.getAttributeValue("value"))) {
                    for (Element auth : serverElement.getChildren()) {
                        List<String> row = new ArrayList<>();
                        row.add(auth.getAttributeValue("value"));
                        row.add(auth.getAttributeValue("name"));
                        body.add(row);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("colName", JSON.toJSONString(colName));
        result.put("tableBody", JSON.toJSONString(body));
        return result;
    }
}