package com.barinek.gnip;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class WebApplication extends HttpServlet {
    static final List<String> knownCities = Lists.newArrayList(
            "arlington",
            "atlanta",
            "austin",
            "boston",
            "boulder",
            "charlotte",
            "chicago",
            "cleveland",
            "dallas",
            "denver",
            "fortworth",
            "houston",
            "indianapolis",
            "jacksonville",
            "miami",
            "minneapolis",
            "neworleans",
            "newyork",
            "nyc",
            "philadelphia",
            "phoenix",
            "portland",
            "sanantonio",
            "sandiego",
            "sanfrancisco",
            "sf",
            "seattle",
            "washington");

    private final KeywordDAO keywordDAO;
    private final Configuration configuration;
    private final Template template;

    public WebApplication() throws IOException {
        keywordDAO = new KeywordDAO();
        configuration = new Configuration();
        template = new Template("template", new InputStreamReader(getClass().getResourceAsStream("/index.ftl")), configuration);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HashMap<String, Object> content = Maps.newHashMap();
        StringWriter writer = new StringWriter();
        try {
            content.put("keywords", keywordDAO.getAll());
            content.put("keywordsTop10", keywordDAO.getTop10());
            template.process(content, writer);
        } catch (TemplateException ignore) {
            System.out.println(ignore.getMessage());
        } catch (SQLException ignore) {
            System.out.println(ignore.getMessage());
        }
        resp.getWriter().print(writer.toString());
    }

    public static void main(String[] args) throws Exception {
        String port = System.getenv("PORT");
        if (port == null) {
            port = "8888";
        }
        Server server = new Server(Integer.valueOf(port));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new WebApplication()), "/*");
        server.start();
        server.join();
    }
}
