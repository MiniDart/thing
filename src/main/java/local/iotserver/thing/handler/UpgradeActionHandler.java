package local.iotserver.thing.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import local.iotserver.thing.devices.Device;
import local.iotserver.thing.devices.DeviceAction;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
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
        upgradeAction(param);
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().println("Success!!! jsonString="+param);
        response.setStatus(HttpServletResponse.SC_OK);
    }
    private void upgradeAction(String param){
        Gson gson=new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String > read = gson.fromJson(param, type);
        System.out.println("device_id="+read.get("device_id"));
        ArrayList<DeviceAction> deviceActions=Device.getDevices().get(Integer.parseInt(read.get("device_id"))).getDeviceActions();
        for (int i=0;i<deviceActions.size();i++){
            if (deviceActions.get(i).getName().equals(read.get("name"))){
                deviceActions.get(i).setValue(read.get("value"));
            }
        }
    }
}
