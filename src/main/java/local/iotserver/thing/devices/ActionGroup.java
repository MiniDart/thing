package local.iotserver.thing.devices;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sergey on 19.01.2017.
 */
public class ActionGroup {
    private Device owner;
    private String name;
    private boolean haveStatisticsElements;
    private ArrayList<DeviceAction> deviceActions=new ArrayList<DeviceAction>();
    private HashMap<String,DeviceAction> deviceActionsMap=new HashMap<String, DeviceAction>();

    public boolean isHaveStatisticsElements() {
        return haveStatisticsElements;
    }

    public String getName() {
        return name;
    }

    public ArrayList<DeviceAction> getDeviceActions() {
        return deviceActions;
    }


    public HashMap<String, DeviceAction> getDeviceActionsMap() {
        return deviceActionsMap;
    }

    public ActionGroup(JsonObject param, Device owner){
        this.owner=owner;
        if (param.has("name")){
            this.name=param.get("name").getAsString();
        }
        JsonArray actions=param.get("actions").getAsJsonArray();
        for (int l=0;l<actions.size();l++){
            JsonObject action=actions.get(l).getAsJsonObject();
            DeviceAction deviceAction=new DeviceAction(action,this);
            deviceActions.add(deviceAction);
            deviceActionsMap.put(deviceAction.getName().toLowerCase(),deviceAction);

        }
        for (int i=0;i<deviceActions.size();i++){
            if (deviceActions.get(i).isNeedStatistics()){
                haveStatisticsElements=true;
                break;
            }
        }

    }

    public Device getOwner() {
        return owner;
    }

}
