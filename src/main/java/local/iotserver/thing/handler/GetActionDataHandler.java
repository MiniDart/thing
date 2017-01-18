package local.iotserver.thing.handler;

import local.iotserver.thing.devices.Device;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Sergey on 18.01.2017.
 */
public class GetActionDataHandler extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int deviceId=Integer.parseInt(req.getParameter("device_id"));
        String data=Device.getDevices().get(deviceId).generateJsonFromActions(false);
        resp.setContentType("text/html;charset=utf-8");
        resp.getWriter().println(data);
        resp.setStatus(HttpServletResponse.SC_OK);

    }
}
