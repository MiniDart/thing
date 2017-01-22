package local.iotserver.thing.devices;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Created by Sergey on 19.01.2017.
 */
public class ActionGroup {
    private Device owner;
    private String name;
    private boolean haveStatisticsElements;
    private ArrayList<DeviceAction> deviceActions=new ArrayList<DeviceAction>();

    public boolean isHaveStatisticsElements() {
        return haveStatisticsElements;
    }

    public String getName() {
        return name;
    }

    public ArrayList<DeviceAction> getDeviceActions() {
        return deviceActions;
    }


    public ActionGroup(JsonObject param, Device owner){
        this.owner=owner;
        if (param.has("name")){
            this.name=param.get("name").getAsString();
        }
        JsonArray actions=param.get("actions").getAsJsonArray();
        for (int l=0;l<actions.size();l++){
            JsonObject action=actions.get(l).getAsJsonObject();
            deviceActions.add(new DeviceAction(action,this));
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
