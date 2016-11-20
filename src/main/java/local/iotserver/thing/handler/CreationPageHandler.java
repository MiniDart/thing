package local.iotserver.thing.handler;

import local.iotserver.thing.templater.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sergey on 18.11.2016.
 */
public class CreationPageHandler extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> pageVariables = new HashMap<String, Object>();
        req.setCharacterEncoding("utf-8");

        response.getWriter().println(PageGenerator.instance().getPage("thing.html", pageVariables));

        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
