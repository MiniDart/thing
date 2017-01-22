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
    private ArrayList<ActionGroup> actionGroups=new ArrayList<ActionGroup>();
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
        for (int i=0;i<actionGroups.size();i++){
            this.actionGroups.add(new ActionGroup(actionGroups.get(i).getAsJsonObject(),this));
        }
        for (int i=0;i<this.actionGroups.size();i++){
            if (this.actionGroups.get(i).isHaveStatisticsElements()){
                haveStatisticsElements=true;
                break;
            }
        }
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
        ArrayList<ActionGroup> actionGroups;
        if (isForStatistics){
            actionGroups=new ArrayList<ActionGroup>();
            for (int i=0;i<this.actionGroups.size();i++) {
                if (this.actionGroups.get(i).isHaveStatisticsElements()) actionGroups.add(this.actionGroups.get(i));
            }
        }
        else actionGroups=this.actionGroups;
        if (actionGroups.size()==0) return "Error, there is no data to do a values";
        StringBuilder res=new StringBuilder();
        res.append("{\"thing_id\":\""+this.id+"\",\"actionGroups\":[");
        for (int i=0;i<actionGroups.size();i++){
            ActionGroup actionGroup=actionGroups.get(i);
            res.append("{\"name\":\""+(actionGroup.getName()==null?"":actionGroup.getName())+"\",\"actions\":[");
            ArrayList<DeviceAction> deviceActions;
            if (isForStatistics){
                deviceActions=new ArrayList<DeviceAction>();
                for (int m=0;m<actionGroup.getDeviceActions().size();m++){
                    if(actionGroup.getDeviceActions().get(i).isNeedStatistics()) deviceActions.add(actionGroup.getDeviceActions().get(i));
                }
            }
            else deviceActions=actionGroup.getDeviceActions();
            for (int n=0;n<deviceActions.size();n++) {
                DeviceAction d = deviceActions.get(n);
                d.generateValue();
                res.append("{\"name\":\"" + d.getName() + "\",\"value\":\"" + d.getValue() + "\"");
                if (d.getSupportActions() != null) {
                    res.append(",\"supportActions\":[");
                    SupportDeviceAction[] supportDeviceActions;
                    if (isForStatistics) {
                        ArrayList<SupportDeviceAction> supportDeviceActionsArr = new ArrayList<SupportDeviceAction>();
                        for (int l = 0; l < d.getSupportActions().length; l++) {
                            if (d.getSupportActions()[l].isNeedStatistics())
                                supportDeviceActionsArr.add(d.getSupportActions()[l]);
                        }
                        supportDeviceActions = new SupportDeviceAction[supportDeviceActionsArr.size()];
                        supportDeviceActionsArr.toArray(supportDeviceActions);
                    } else supportDeviceActions = d.getSupportActions();
                    for (int l = 0; l < supportDeviceActions.length; l++) {
                        SupportDeviceAction sd = supportDeviceActions[l];
                        sd.generateValue();
                        res.append("{\"name\":\"" + sd.getName() + "\",\"value\":\"" + sd.getValue() + "\"}");
                        if (l != supportDeviceActions.length - 1) {
                            res.append(",");
                        }
                    }
                    res.append("]");
                }
                res.append("}");
                if (n != deviceActions.size() - 1) {
                    res.append(",");
                }
            }
            res.append("]}");
            if (i != actionGroups.size() - 1) {
                res.append(",");
            }
        }
        res.append("]}");
        return res.toString();
    }
    /*-----------generateJsonFromStatisticsActions-------------------
    private String generateJsonFromStatisticsActions(){
        String res="{\"thing_id\":\""+this.id+"\",";
        for (int i=0;i<deviceActions.size();i++){
            DeviceAction d=deviceActions.get(i);
            if (d.isNeedStatistics()) {
                d.generateValue();
                res += "\"" + d.getName() + "\":\"" + d.getValue() + "\",";
            }
        }
        res=res.substring(0,res.length()-1);
        res+="}";
        return res;
    }
    */
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
