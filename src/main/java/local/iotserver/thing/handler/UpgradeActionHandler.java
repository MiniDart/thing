package local.iotserver.thing.handler;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import local.iotserver.thing.devices.Device;
import local.iotserver.thing.devices.DeviceAction;
import local.iotserver.thing.devices.SupportDeviceAction;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sergey on 22.11.2016.
 */
public class UpgradeActionHandler extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        String param=req.getParameter("upgradeDevice");
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().println(upgradeActions(param));
        response.setStatus(HttpServletResponse.SC_OK);
    }
    private String upgradeActions(String param){
        JsonObject deviceJson = new JsonParser().parse(param).getAsJsonObject();
        Device device=Device.getDevices().get(deviceJson.get("id").getAsInt());
        JsonArray actionsJson=deviceJson.get("actions").getAsJsonArray();
        for (JsonElement actionJson:actionsJson){
            DeviceAction deviceAction=device.getDeviceActionHashMap().get(actionJson.getAsJsonObject().get("id").getAsInt());
            deviceAction.setValue(actionJson.getAsJsonObject().get("value").getAsString());
        }
        return device.generateJsonFromActions(false);
    }
}
