package local.iotserver.thing.handler;

import local.iotserver.thing.devices.Device;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Created by Sergey on 18.11.2016.
 */
public class ThingCreationHandler extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        String answer=req.getParameter("new_thing");
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().println(answer);
        response.setStatus(HttpServletResponse.SC_OK);
        Thread t=new Thread(new Device(answer));
        Device.getDeviceThreads().add(t);
        t.start();
    }
}
