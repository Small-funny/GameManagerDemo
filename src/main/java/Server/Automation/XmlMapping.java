package Server.Automation;

import Server.DatabaseHelper.ManagerDatabaseHelper;
import Server.DatabaseHelper.VerifyDatabaseHelper;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jdom2.*;

import java.text.SimpleDateFormat;
import java.util.*;

import static Server.Automation.PageUtil.*;

/**
 * @author Wen
 */
@Slf4j
@SuppressWarnings("unchecked")
public class XmlMapping {

    /**
     * 根据xml生成页面的内容
     *
     * @param element 页面的元素
     * @param route   当前页面
     */

    public static String createElementString(Element element, String route) {
        String authorization = element.getAttributeValue("authorization");
        String pageType = element.getAttributeValue("type");
        StringBuilder stringBuilder = new StringBuilder();
        for (Element child : element.getChildren()) {
            stringBuilder.append(elementForm(child, route));
        }

        return stringBuilder.toString();
    }

    /**
     * 根据xml生成表格控件
     *
     * @param element 页面元素
     * @param route   当前页面服务器和路径
     */
    private static String elementForm(Element element, String route) {
        StringBuilder stringBuilder = new StringBuilder();
        String operation = element.getAttributeValue("operation");
        stringBuilder.append("<div class=\"card-header\"><strong>")
                .append(element.getAttributeValue("name"))
                .append("</strong></div>")
                .append("<div class=\"card-body card-block\">")
                .append("<form name=\"").append(element.getAttributeValue("name"))
                .append("\" operation=\"").append(element.getAttributeValue("operation")).append("\" class=\"form-horizontal\">");
        for (Element child : element.getChildren()) {
            switch (child.getName()) {
                case "input":
                    stringBuilder.append(elementInput(child, operation));
                    break;
                case "select":
                    stringBuilder.append(elementSelect(child, operation));
                    break;
                case "formcheck":
                    stringBuilder.append(elementFormCheck(child));
                    break;
                case "textarea":
                    stringBuilder.append(elementTextarea(child, operation));
                    break;
                case "file":
                    stringBuilder.append(elementFile(child));
                    break;
                case "time":
                    stringBuilder.append(elementTime(child));
                    break;
                default:
                    break;
            }
        }

        stringBuilder.append("<input type=\"hidden\" value=\"").append(element.getAttributeValue("operation")).append("\" name=\"operation\" id=\"operation\" from=\"").append(operation).append("\"/>")
                .append("<input type=\"hidden\" value=\"").append(route).append("\" name=\"route\" id=\"route\" from=\"").append(operation).append("\"/>")
                .append("</form>")
                .append("</div>");
        return stringBuilder.toString();

    }

    /**
     * 根据xml生成input控件
     *
     * @param element   页面元素
     * @param operation 当前表单的操作
     */
    private static String elementInput(Element element, String operation) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<div class=\"row form-group\">")
                .append("<div class=\"col col-md-3\">")
                .append("<label  class=\" form-control-label\">");
        stringBuilder.append(element.getAttributeValue("name") == null ? " " : element.getAttributeValue("name"));

        stringBuilder.append("</label>")
                .append("</div>")
                .append("<div class=\"col-12 col-md-9\">")
                .append("<input class=\"");
        if ("file".equals(element.getAttributeValue("type"))) {
            stringBuilder.append("form-control-file");
        } else {
            stringBuilder.append("form-control");
        }
        stringBuilder.append("\" type=\"")
                .append(element.getAttributeValue("type"))
                .append("\" name=\"")
                .append(element.getAttributeValue("name"))
                .append("\" id=\"")
                .append(element.getAttributeValue("id"))
                .append("\" from=\"").append(operation)
                .append("\"");
        if ("button".equals(element.getAttributeValue("type"))) {
            stringBuilder.append("value=\"")
                    .append(element.getAttributeValue("value"))
                    .append("\" onclick=\"changeReturn('/");
            if ("userManage".equals(element.getAttributeValue("id"))) {
                stringBuilder.append("manager");
            } else if ("configManage".equals(element.getAttributeValue("id"))) {
                stringBuilder.append("forward");
            }
            stringBuilder.append("','").append(operation).append("')\"");
        }


        stringBuilder.append("/>").append("</div>").append("</div>");

        return stringBuilder.toString();
    }

    private static String elementTextarea(Element element, String operation) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<div class=\"row form-group\">")
                .append("<div class=\"col col-md-3\">")
                .append("<label  class=\" form-control-label\">");
        stringBuilder.append(element.getAttributeValue("name") == null ? " " : element.getAttributeValue("name"));
        stringBuilder.append("</label>")
                .append("</div>")
                .append("<div class=\"col-12 col-md-9\">")
                .append("<textarea name=\"")
                .append(element.getAttributeValue("name"))
                .append("\" id=\"")
                .append(element.getAttributeValue("id"))
                .append("\" from=\"").append(operation)
                .append("\"").append("rows=\"19\"  class=\"form-control\" style=\"height:700px\"/>");
        stringBuilder.append("</div>").append("</div>");
        return stringBuilder.toString();
    }

    /**
     * 根据xml生成下拉框控件
     *
     * @param element   页面元素
     * @param operation 当前表单的操作
     */
    private static String elementSelect(Element element, String operation) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<div class=\"row form-group\">")
                .append("<div class=\"col col-md-3\">")
                .append("<label  class=\" form-control-label\">");
        stringBuilder.append(element.getAttributeValue("name") == null ? " " : element.getAttributeValue("name"));
        stringBuilder.append("</label>")
                .append("</div>")
                .append("<div class=\"col-12 col-md-9\">")
                .append("<select class=\"form-control\" name=\"")
                .append(element.getAttributeValue("name"))
                .append("\" id=\"")
                .append(element.getAttributeValue("id"))

                .append("\"").append("\" from=\"").append(operation)
                .append("\">");
        for (Element child : element.getChildren()) {
            stringBuilder.append("<option value=\"").append(child.getAttributeValue("value")).append("\">")
                    .append(child.getValue())
                    .append("</option>");
        }
        stringBuilder.append("</select>")
                .append("</div>")
                .append("</div>");

        return stringBuilder.toString();
    }

    /**
     * 根据xml生成单选框或复选框控件
     *
     * @param element 页面元素
     */
    private static String elementFormCheck(Element element) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<div class=\"row form-group\">")
                .append("<div class=\"col col-md-3\">")
                .append("<label  class=\" form-control-label\">");
        stringBuilder.append(element.getAttributeValue("name") == null ? " " : element.getAttributeValue("name"));
        stringBuilder.append("</label>")
                .append("</div>")
                .append("<div class=\"col-12 col-md-9\">")
                .append("<div class=\"form-check\">");
        for (Element child : element.getChildren()) {
            stringBuilder.append("<div class=\"").append(child.getName()).append("\">")
                    .append("<label class=\"form-check-label \">")
                    .append("<input type=\"").append(child.getName()).append("\" name=\"").append(element.getAttributeValue("name")).append("\" value=\"").append(child.getAttributeValue("value")).append("\" class=\"form-check-input\">")
                    .append(child.getValue())
                    .append("</label>")
                    .append("</div>");
        }
        stringBuilder.append("</div>")
                .append("</div>")
                .append("</div>");

        return stringBuilder.toString();
    }

    /**
     * 根据xml选择文件控件
     *
     * @param element 页面元素
     */
    private static String elementFile(Element element) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<div class=\"row form-group\">")
                .append("<div class=\"col col-md-3\">")
                .append("<label  class=\" form-control-label\">");
        stringBuilder.append(element.getAttributeValue("name") == null ? " " : element.getAttributeValue("name"));
        stringBuilder.append("</label>")
                .append("</div>")
                .append("<div class=\"col-12 col-md-9\">")
                .append("<input type=\"file\" name=\"").append(element.getAttributeValue("name")).append("\" id=\"11122333\" class=\"form-file\">")
                .append("</div>")
                .append("</div>");
        return stringBuilder.toString();
    }

    /**
     * 根据xml选择时间控件
     *
     * @param element 页面元素
     */
    private static String elementTime(Element element) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<div class=\"row form-group\">")
                .append("<div class=\"col col-md-3\">")
                .append("<label  class=\" form-control-label\">");
        stringBuilder.append(element.getAttributeValue("name") == null ? " " : element.getAttributeValue("name"));
        stringBuilder.append("</label>")
                .append("</div>")
                .append("<div class=\"col-12 col-md-9\">")
                .append("<input type=\"text\" name=\"")
                .append(element.getAttributeValue("name"))
                .append("\" id=\"timestamp\" class=\"form-control\" autocomplete=\"off\">")
                .append("<script>\n" +
                        "    lay('#version').html('-v' + laydate.v);\n" +
                        "    laydate.render({\n" +
                        "        elem: '#timestamp'\n" +
                        "        , type: 'datetime'\n" +
                        "\n" +
                        "    });\n" +
                        "</script>")
                .append("</div>")
                .append("</div>");
        return stringBuilder.toString();
    }

    /**
     * 根据页面名获取页面的元素
     *
     * @param str 页面名
     * @return 页面元素
     */
    public static Element getElement(String str) {
        return PAGE_ELEMENT.get(str);
    }

    /**
     * 根据xml生成侧边栏菜单
     *
     * @param token  用户的token
     * @param server 当前服务器
     */
    public static String createAsideString(String token, String server) {
        List<String> urlList = VerifyDatabaseHelper.selectAuthority(token, server);
        StringBuilder stringBuilder = new StringBuilder();
        //从外层div开始 div类是navigation-menu-body
        //这是最外层的ul
        stringBuilder.append("<ul>");
        //这个是最大类别 暂时没什么用
        stringBuilder.append("<li class=\"navigation-divider\">最大类别</li>");
        for (Map.Entry<String, Element> entry : TYPE_ELEMENT.entrySet()) {
            if (!urlList.contains(entry.getValue().getAttributeValue("authorization"))) {
                continue;
            }
            if ("playerManage".equals(entry.getKey()) && !"root".equals(VerifyDatabaseHelper.tokenToUsername(token))) {
                continue;
            }
            stringBuilder.append("<li> <a href=\"");
            if (entry.getValue().getAttribute("authorization") != null) {
                stringBuilder.append(entry.getValue().getAttributeValue("name")).append("\">");
            } else {
                stringBuilder.append("#\">");
            }
            stringBuilder.append("<i class=\"nav-link-icon\" data-feather=\"")
                    .append(entry.getValue().getAttributeValue("icon"))
                    .append("\"></i>")
                    .append(entry.getValue().getAttributeValue("name"))
                    .append("</a><ul>");
            for (Element element : entry.getValue().getChildren()) {
                stringBuilder.append("<li id =\" ")
                        .append(element.getAttributeValue("name"))
                        .append("\"><a href=\"#\" onclick=\"changeAside('")
                        .append(server)
                        .append("','")
                        .append(element.getAttributeValue("authorization"))
                        .append("','").append(element.getAttributeValue("list"))
                        .append("','").append(element.getAttributeValue("table"))
                        .append("')\" id=\"").append(element.getAttributeValue("authorization")).append("\">")
                        .append(element.getAttributeValue("name"))
                        .append("</a></li>");
            }
            stringBuilder.append("</ul>");
            stringBuilder.append("</li>");
        }
        if (VerifyDatabaseHelper.isSupLevel(VerifyDatabaseHelper.tokenToUsername(token))) {
            stringBuilder.append("<li><a href=\"playerManage\"><i class=\"nav-link-icon\" data-feather=\"anchor\"></i>").append(TYPE_ELEMENT.get("playerManage").getAttributeValue("name")).append("</a><ul>");
            for (Element element : TYPE_ELEMENT.get("playerManage").getChildren()) {
                stringBuilder.append("<li id =\" ")
                        .append(element.getAttributeValue("name"))
                        .append("\"><a href=\"#\" onclick=\"changeAside('")
                        .append(server)
                        .append("','")
                        .append(element.getAttributeValue("authorization"))
                        .append("','").append(element.getAttributeValue("list"))
                        .append("','").append(element.getAttributeValue("table"))
                        .append("')\" id=\"").append(element.getAttributeValue("authorization")).append("\">")
                        .append(element.getAttributeValue("name"))
                        .append("</a></li>");
            }
        }
        stringBuilder.append("</ul>");
        return stringBuilder.toString();
    }

    /**
     * 生成选择服务器下拉框
     *
     * @param selected 当前选择的服务器
     */
    public static String createServerString(String selected) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<option value=\"0\"> 选择服务器 </option>");
        for (String string : SERVER_ELEMENT.keySet()) {
            stringBuilder.append("<option value=\"")
                    .append(SERVER_ELEMENT.get(string).getAttribute("value").getValue())
                    .append("\"");
            if (selected.equals(SERVER_ELEMENT.get(string).getAttribute("value").getValue())) {
                stringBuilder.append(" selected");
            }
            stringBuilder.append(">")
                    .append(SERVER_ELEMENT.get(string).getAttributeValue("name"))
                    .append("</option>");
        }
        return stringBuilder.toString();
    }

    /**
     * 生成右侧列表
     *
     * @param data 列表数据
     */
    public static String createConfigsList(String data, String argName) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<div class=\"card\">").append("<div class=\"card-body card-block\" style=\"width: auto\">" +
                "<div class=\"row form-group\"><div class=\"col col-md-12\">");
        List<String> list = JSON.parseObject(data, List.class);
        stringBuilder.append("<select style=\"height:700px\" name=\"multiple-select\" id=\"multiple-select\" multiple=\"\" class=\"form-control\">");
        for (String s : list) {
            stringBuilder.append("<option ondblclick=\"document.getElementById('").append(argName).append("').value='").append(s).append("'\" value=\"").append(s).append("\">").append(s).append("</option>");
        }
        stringBuilder.append("</div></div></div></div>");

        return stringBuilder.toString();
    }

    /**
     * 生成返回的内容
     *
     * @param type     返回内容的类型
     * @param data     返回内容的数据
     * @param auth     在str类型下是否有修改的权限
     * @param argsName 构造返回内容所需要的数据
     */
    public static String createReturnString(String type, String data, boolean auth, HashMap<String, String> argsName) {
        StringBuilder stringBuilder = new StringBuilder();
        String page = "";
        if (argsName != null) {
            page = argsName.get("route").split("/")[2];
        }
        if ("list".equals(type)) {
            List<String> list = JSON.parseObject(data, List.class);
            for (String s : list) {
                stringBuilder.append("<div class=\"checkbox\">").append("<label class=\"form-check-label \">")
                        .append("<input type=\"checkbox\" name=\"").append("auth").append("\" value=\"").append(s).append("\" class=\"form-check-input\">")
                        .append(s)
                        .append("</label>")
                        .append("</div>");
            }
            return stringBuilder.toString();
        } else {

            if ("table".equals(type)) {

                HashMap<String, String> hashMap1 = JSON.parseObject(data, HashMap.class);
                List<String> colName = JSON.parseObject(hashMap1.get("colName"), List.class);
                List<List<String>> tableBody = JSON.parseObject(hashMap1.get("tableBody"), List.class);
                stringBuilder.append("<table class=\"table table-bordered\">").append("<thead><tr>");
                for (String s : colName) {
                    stringBuilder.append("<th scope=\"col\">").append(s).append("</th>");
                }
                stringBuilder.append("</tr></thead><tbody>");
                for (List<String> subTableBody : tableBody) {
                    stringBuilder.append("<tr>");
                    for (int i = 0; i < subTableBody.size(); i++) {
                        stringBuilder.append("<td>");
                        if (i == 0 && USER_AUTH_MANAGE_PAGES.contains(page)) {
                            if (TYPE_ELEMENT.containsKey(subTableBody.get(i))) {
                                stringBuilder.append(TYPE_ELEMENT.get(subTableBody.get(i)).getAttributeValue("name"));
                            } else {
                                stringBuilder.append(subTableBody.get(i));
                            }
                        } else {
                            stringBuilder.append(subTableBody.get(i));
                        }
                        stringBuilder.append("</td>");
                    }
                    if (USER_MANAGE_PAGES.contains(page)) {
                        stringBuilder.append("<td><input type=\"checkbox\" name\"deleteCheckbox from=\"deleteCheckbox\" value=\"")
                                .append(subTableBody.get(0))
                                .append("\" onclick=\"updateCheckbox($(this))\" class=\"form-check-input\"/></td>");

                    }
                    stringBuilder.append("</tr>");
                }
                stringBuilder.append("</tbody></table>");
                if (USER_MANAGE_PAGES.contains(page)) {
                    stringBuilder.append("<input type=\"button\" class=\"form-control\" value=\"删除\" onclick=\"updateAuth('/manager','deleteUsers')\"");
                }
                stringBuilder.append("</div></div></div></form>");

            } else if ("str".equals(type)) {
                String route = argsName.get("route");
                stringBuilder.append(" <div class=\"card\"><div class=\"card-body card-block\" style=\"width: auto\">")
                        .append("<form id=\"updateForm\" class=\"form-horizontal\" style=\"width: auto\">")
                        .append("<div class=\"row form-group\" style=\"width: auto\">")
                        .append("<div class=\"col-12 \">")
                        .append("<textarea id=\"text\" name=\"body\" rows=\"19\" placeholder=\"Cont.\" class=\"form-control\" style=\"height:700px\" from=\"return\">")
                        .append(data)
                        .append("</textarea></div></div>");
                if (auth && CONFIG_MANAGE_PAGES.contains(page)) {
                    stringBuilder.append("<div class=\"row form-group\">")
                            .append("<div class=\"col col-md-3\"><label class=\" form-control-label\">subPassword</label></div>")
                            .append("<div class=\"col-12 col-md-9\">")
                            .append("<input type=\"password\" name=\"subPassword\" id=\"subPassword\" class=\"form-control\" from=\"return\"/>")
                            .append("</div></div>");
                    stringBuilder
                            .append("<input type=\"button\" name=\"submit\" onclick=\"updateReturn('/forward')\" class=\"form-control\" value=\"修改\"></div></div>")
                            .append("<input type=\"hidden\" value=\"updateConfigBody\" name=\"operation\" from=\"return\">")
                            .append("<input type=\"hidden\" value=\"")
                            .append(route)
                            .append("\" name=\"route\" from=\"return\">");
                }

                stringBuilder.append("</form></div>");
            } else if ("return".equals(type)) {
                Calendar calendar = Calendar.getInstance(Locale.CHINA);
                Date date = calendar.getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String format = dateFormat.format(date);

                stringBuilder.append(" <div class=\"card\">")
                        .append("<div class=\"card-header\" id=\"nowTime\"><strong>" + "操作时间：")
                        .append(format)
                        .append("</strong></div>")
                        .append("<div class=\"card-body card-block\" style=\"width: auto\">")
                        .append("<div class=\"row form-group\" style=\"width: auto\">")
                        .append("<div class=\"col-12 \">");
                stringBuilder.append("<p id=\"text\" name=\"body\" class=\"form-control\"  from=\"return\">")
                        .append(data)
                        .append("</p></div></div>");
                stringBuilder.append("</div>");
            } else if ("checkbox".equals(type)) {

                //List<String> authList = ManagerDatabaseHelper.selectAuthList(argsName.get("username"), "list", argsName.get("route").split("/")[1]);
                List<String> authList = JSON.parseArray(data, String.class);
                List<String> allAuthList = ManagerDatabaseHelper.selectAuthList("root", argsName.get("authType"), argsName.get("route").split("/")[1]);
                stringBuilder.append(" <div class=\"card\"><div class=\"card-body card-block\" style=\"width: auto\">");
                stringBuilder.append("<div class=\"row form-group\">")
                        .append("<div class=\"col-12 \">")
                        .append("<div class=\"form-check\">");
                for (String s : allAuthList) {
                    stringBuilder.append("<div class=\"checkbox\">")
                            .append("<label class=\"form-check-label \">")
                            .append("<input type=\"checkbox\" name=\"auth\" value=\"")
                            .append(s)
                            .append("\"");
                    if (authList.contains(s)) {
                        stringBuilder.append("checked=\"checked\" ");
                    }
                    stringBuilder.append("from=\"updateAuth\" onclick=\"updateCheckbox($(this))\" class=\"form-check-input\">")
                            .append(enAuth2zh(s))
                            .append("</label>")
                            .append("</div>");
                }
                stringBuilder.append("<input type=\"button\" class=\"form-control\" value=\"修改\" onclick=\"updateAuth('/manager','updateAuth')\"/>");
                stringBuilder.append("</div>")
                        .append("</div>")
                        .append("</div></div></div>");
            }
        }
        return stringBuilder.toString();
    }

}