package local.iotserver.thing.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import local.iotserver.thing.devices.Device;
import local.iotserver.thing.devices.DeviceAction;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Sergey on 29.03.2017.
 */
public class GeneralHandler extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        String answer=req.getParameter("new_thing");
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().println(answer);
        response.setStatus(HttpServletResponse.SC_OK);
        Device d=new Device(answer);
        if (d.isHaveClient) {
            Thread t = new Thread(d);
            Device.getDeviceThreads().add(t);
            t.start();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri_json=req.getParameter("actions");
        if (uri_json==null) return;
        String path=req.getPathInfo();
        String uri="localhost:3000"+path.substring(0,path.length()-1);
        if (!Device.getDevices().containsKey(uri)){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Device d=Device.getDevices().get(uri);
        JsonArray actions_json = new JsonParser().parse(uri_json).getAsJsonArray();
        ArrayList<DeviceAction> devices=new ArrayList<DeviceAction>();
        for (int i=0;i<actions_json.size();i++){
            devices.add(d.getDeviceActionHashMap().get(actions_json.get(i).getAsString()));
        }
        String data=d.generateJsonFromActions(devices);
        resp.setContentType("text/html;charset=utf-8");
        resp.getWriter().println(data);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
        String data = br.readLine();
        if (data==null) return;
        String uri_json=URLDecoder.decode(data,"UTF-8").substring(8);
        String path=req.getPathInfo();
        String uri="localhost:3000"+path;
        Device d=Device.getDevices().get(uri);
        JsonArray actions_json = new JsonParser().parse(uri_json).getAsJsonArray();
        for (int i=0;i<actions_json.size();i++){
            d.getDeviceActionHashMap().get(actions_json.get(i).getAsJsonObject().get("uri").getAsString())
                    .setValue(actions_json.get(i).getAsJsonObject().get("value").getAsString());
        }
        resp.setContentType("text/html;charset=utf-8");
        resp.getWriter().println("Success");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
