package Server.Resources;

import Server.Automation.XmlMapping;
import Server.Verify.Cache;
import Server.Verify.JwtUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class MainResources extends AbstractVerticle {

    ThymeleafTemplateEngine thymeleafTemplateEngine;
    XmlMapping xmlMapping;
    static String serverString;
    static String asideString;

    public void registerResources(Router router, Vertx vertx) {
//        router.get("/main").handler(this::main);
        try {
            xmlMapping = new XmlMapping();
        } catch (Exception e) {
            e.printStackTrace();
        }

        thymeleafTemplateEngine = ThymeleafTemplateEngine.create(vertx);
        router.route("/main/home").handler(ctx -> {
            String token = JwtUtils.findToken(ctx);
            //asideString = xmlMapping.createAsideString(token,);
            //System.out.println("/////" + asideString);
            serverString = xmlMapping.createServerString("0");
            var obj = new JsonObject();
            obj.put("sidePanal", "");
            obj.put("content", "");
            obj.put("servers", serverString);
            thymeleafTemplateEngine.render(obj, "src/main/java/resources/templates/home.html", bufferAsyncResult -> {
                ctx.response().putHeader("content-type", "text/html").end(bufferAsyncResult.result());
            });
        });

        router.route("/main/:serverRouter/:pageRouter").handler(ctx -> {
            asideString = xmlMapping.createAsideString(JwtUtils.findToken(ctx), ctx.request().getParam("serverRouter"));
            String pageRouter = ctx.request().getParam("pageRouter");
            String serverRouter = ctx.request().getParam("serverRouter");
            String returnContent = ctx.get("returnContent");
            String selectName = ctx.get("args");
            String type = ctx.get("type");
            String data = ctx.get("data");
            serverString = xmlMapping.createServerString(serverRouter);
            //页面中显示的东西
            String contentString;
            if ("0".equals(pageRouter)) {
                contentString = "";
            } else {
                contentString = xmlMapping.createElementString(xmlMapping.getElement(ctx.request().getParam("pageRouter")));
            }
            var obj = new JsonObject();
            obj.put("sidePanal", asideString);
            obj.put("pagename", pageRouter);
            obj.put("servers", serverString);
            obj.put("servername", serverRouter);
            obj.put("route", "/" + serverRouter + "/" + pageRouter);
            if (type != null) {
                switch (type) {
                    case "table":
                        break;
                    case "list":
                        break;
                    case "str":
                        obj.put("returnContent", data);
                        break;
                    default:
                        break;

                }
            }
            // HashMap<String, Object> hashMap = new HashMap<>();
            // List<String> list = new ArrayList<>();
            // switch (type) {
            //     case "table" :
            //         hashMap = JSON.parseObject(resultData, HashMap.class);
            //         routingContext.put("colName",hashMap.get("colName")).put("tableBody", hashMap.get("tableBody"));
            //         break;
            //     case "list" :
            //         list = JSON.parseObject(resultData, ArrayList.class);
            //         routingContext.put("list", list);
            //         break;
            //     case "str" :
            //         routingContext.put("data", resultData);
            //         break;
            // }
            obj.put("selectName", selectName);
            obj.put("content", contentString);
            thymeleafTemplateEngine.render(obj, "src/main/java/resources/templates/home.html", bufferAsyncResult -> {
                ctx.response().putHeader("content-type", "text/html").end(bufferAsyncResult.result());
            });
        });

        router.route("/main/args").handler(ctx -> {
            String token = JwtUtils.findToken(ctx);
            String args = Cache.getArgs(token);
            ctx.response().end(args);
        });

    }
}
