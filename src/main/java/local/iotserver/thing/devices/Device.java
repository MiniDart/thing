package local.iotserver.thing.devices;

import com.google.gson.*;
import local.iotserver.thing.network.ClientManager;
import org.eclipse.jetty.util.Fields;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sergey on 19.11.2016.
 */
public class Device implements Runnable {
    private final static HashMap<Integer,Device> devices=new HashMap<Integer, Device>();
    private final static ArrayList<Thread> deviceThreads=new ArrayList<Thread>();
    private String param;
    private HashMap<Integer,DeviceAction> deviceActionHashMap=new HashMap<Integer, DeviceAction>();
    private int id;
    private String name;
    private  String thingGroup;
    private boolean haveStatisticsElements;
    private int updateTime;
    static ClientManager clientManager=ClientManager.getInstance();

    public Device(String param) {
        this.param=param;
        JsonObject mainFeatures = new JsonParser().parse(param).getAsJsonObject();
        this.id=mainFeatures.get("id").getAsInt();
        this.name=mainFeatures.get("name").getAsString();
        this.thingGroup=mainFeatures.get("thingGroup").getAsString();
        this.updateTime=mainFeatures.get("updateTime").getAsInt();
        JsonArray actionGroups=mainFeatures.get("actionGroups").getAsJsonArray();
        findActions(actionGroups);

        devices.put(this.id,this);
        sayHi();
    }

    public void run() {
        System.out.println("in Device run method");
        if (!haveStatisticsElements) return;
        while (!Thread.currentThread().isInterrupted()) {
            sendDataFromActions(true);
            try {
                Thread.sleep(this.updateTime);
            }
            catch (InterruptedException e){
                return;
            }

        }

    }
    private void sayHi(){
        System.out.println(id+"-sayHi()");

        Fields.Field upgrade_thing = new Fields.Field("new_thing", param);
        Fields fields = new Fields();
        fields.put(upgrade_thing);
        String answer=clientManager.sendPost("http://iotmanager.local/newthing",fields);
        System.out.println("Creation status for device id="+this.id+" - "+answer);

    }
    private void findActions(JsonArray actionGroupsJson){
        for (JsonElement actionGroupJson:actionGroupsJson){
            if (actionGroupJson.getAsJsonObject().has("actions")){
                JsonArray actionsJson=actionGroupJson.getAsJsonObject().get("actions").getAsJsonArray();
                for (JsonElement actionJson:actionsJson){
                    DeviceAction deviceAction=new DeviceAction(actionJson.getAsJsonObject(),this);
                    this.deviceActionHashMap.put(deviceAction.getId(),deviceAction);
                }
            }
            if (actionGroupJson.getAsJsonObject().has("actionGroups")){
                findActions(actionGroupJson.getAsJsonObject().get("actionGroups").getAsJsonArray());
            }
        }
    }
    public HashMap<Integer, DeviceAction> getDeviceActionHashMap() {
        return deviceActionHashMap;
    }
    public static HashMap<Integer, Device> getDevices() {
        return devices;
    }

    public static ArrayList<Thread> getDeviceThreads() {
        return deviceThreads;
    }

    public String getParam() {
        return param;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getThingGroup() {
        return thingGroup;
    }
    public synchronized String generateJsonFromActions(boolean isForStatistics){
        /*
        Gson gson=new Gson();
        HashMap<String,String> hashMapJson=new HashMap<String, String>();
        hashMapJson.put("thing_id",String.valueOf(this.id));
        ArrayList<String> groupActionString=new ArrayList<String>();
            for (int i=0;i<this.actionGroups.size();i++){
                ActionGroup actionGroup;
                if (isForStatistics) {
                    if (this.actionGroups.get(i).isHaveStatisticsElements()) {
                        actionGroup=this.actionGroups.get(i);

                    }
                    else continue;
                }
                else actionGroup=this.actionGroups.get(i);
                HashMap<String,String> actionGroupHashMap=new HashMap<String, String>();
                actionGroupHashMap.put("name",actionGroup.getName());
                ArrayList<DeviceAction> deviceActions=actionGroup.getDeviceActions();
                ArrayList<String> deviceActionString=new ArrayList<String>();
                for (int l=0;l<deviceActions.size();l++){
                    DeviceAction deviceAction;
                    if (isForStatistics){
                        if (deviceActions.get(i).isNeedStatistics()){
                            deviceAction=deviceActions.get(i);
                        }
                        else continue;
                    }
                    else deviceAction=deviceActions.get(i);
                    HashMap<String,String> deviceActionHashMap=new HashMap<String, String>();
                    deviceAction.generateValue();
                    deviceActionHashMap.put("name",deviceAction.getName());
                    deviceActionHashMap.put("value",String.valueOf(deviceAction.getValue()));
                    if (deviceAction.getSupportActions()!=null){
                        ArrayList<String> supportActionsString=new ArrayList<String>();
                        for (int m=0;m<deviceAction.getSupportActions().length;m++){
                            HashMap<String,String> supportActionsHashMap=new HashMap<String, String>();
                            SupportDeviceAction supportDeviceAction=deviceAction.getSupportActions()[m];
                            supportDeviceAction.generateValue();
                            supportActionsHashMap.put("name",supportDeviceAction.getName());
                            supportActionsHashMap.put("value",String.valueOf(supportDeviceAction.getValue()));
                            supportActionsString.add(gson.toJson(supportActionsHashMap));
                        }
                        deviceActionHashMap.put("supportActions",gson.toJson(supportActionsString));
                    }
                    deviceActionString.add(gson.toJson(deviceActionHashMap));
                }
                actionGroupHashMap.put("actions",gson.toJson(deviceActionString));
                groupActionString.add(gson.toJson(actionGroupHashMap));
            }
        hashMapJson.put("actionGroups",gson.toJson(groupActionString));
        */
        StringBuilder res=new StringBuilder();
        res.append("{\"thing_id\":\""+this.id+"\",\"actions\":[");
        for (DeviceAction deviceAction:this.deviceActionHashMap.values()){
            if (isForStatistics&&!deviceAction.isNeedStatistics()) continue;
            deviceAction.generateValue();
            res.append("{\"id\":\""+deviceAction.getId()+"\",\"value\":\""+deviceAction.getValue()+"\"},");
        }
        String result=res.toString();
        result=result.substring(0,result.length()-1);
        result+="]}";
        return result;
    }
    public void sendDataFromActions(boolean isForStatistics){
        System.out.println("Device id="+this.id+" is sending data...");
        String res = generateJsonFromActions(isForStatistics);
        Fields.Field upgrade_thing = new Fields.Field("thing_param", res);
        Fields fields = new Fields();
        fields.put(upgrade_thing);
        String  answer;
        if (isForStatistics) answer=clientManager.sendPost("http://iotmanager.local/upgradeactionsdata",fields);
        else answer=clientManager.sendPost("http://iotmanager.local/upgradeactionsvalues",fields);
        System.out.println("Sending data status for device id="+this.id+" - "+answer);
    }
}
