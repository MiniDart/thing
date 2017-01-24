package local.iotserver.thing.handler;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import local.iotserver.thing.devices.ActionGroup;
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
        System.out.println(param);
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().println(upgradeActions(param));
        response.setStatus(HttpServletResponse.SC_OK);
    }
    private String upgradeActions(String param){
        JsonObject deviceJson = new JsonParser().parse(param).getAsJsonObject();
        Device device=Device.getDevices().get(deviceJson.get("id").getAsInt());
        JsonArray actionGroupsJson=deviceJson.get("actionGroups").getAsJsonArray();
        for (JsonElement actionGroupJson:actionGroupsJson){
            ActionGroup actionGroup=device.getActionGroupsMap().get(actionGroupJson.getAsJsonObject().get("name").getAsString().toLowerCase());
            JsonArray actionsJson=actionGroupJson.getAsJsonObject().get("actions").getAsJsonArray();
            for(JsonElement actionJson:actionsJson){
                DeviceAction deviceAction=actionGroup.getDeviceActionsMap().get(actionJson.getAsJsonObject().get("name").getAsString().toLowerCase());
                if (actionJson.getAsJsonObject().has("value")){
                    deviceAction.setValue(actionJson.getAsJsonObject().get("value").getAsString());
                }
                if (actionJson.getAsJsonObject().has("supportActions")){
                    JsonArray supportActionsJson=actionJson.getAsJsonObject().get("supportActions").getAsJsonArray();
                    for (JsonElement supportActionJson:supportActionsJson){
                        SupportDeviceAction supportDeviceAction=deviceAction.getSupportDeviceActionsMap().get(supportActionJson.getAsJsonObject().get("name").getAsString().toLowerCase());
                        supportDeviceAction.setValue(supportActionJson.getAsJsonObject().get("value").getAsString());
                    }
                }
            }

        }

        return device.generateJsonFromActions(false);
    }
}
