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
    private final static HashMap<String,Device> devices=new HashMap<String, Device>();
    private final static ArrayList<Thread> deviceThreads=new ArrayList<Thread>();
    private String param;
    private HashMap<String,DeviceAction> deviceActionHashMap=new HashMap<String, DeviceAction>();
    private String uri;
    private int updateTime;
    private int id;
    public boolean isHaveClient;
    static ClientManager clientManager=ClientManager.getInstance();

    public Device(String param) {
        this.param=param;
        JsonObject mainFeatures = new JsonParser().parse(param).getAsJsonObject();
        this.uri=mainFeatures.get("uri").getAsString();
        this.updateTime=mainFeatures.get("updateTime").getAsInt();
        this.isHaveClient=mainFeatures.has("isHaveClient")&&mainFeatures.get("isHaveClient").getAsBoolean();
        JsonArray actionGroups=mainFeatures.get("actionGroups").getAsJsonArray();
        findActions(actionGroups);
        devices.put(this.uri,this);
        sayHi();
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            sendDataFromActions();
            try {
                Thread.sleep(this.updateTime*1000);
            }
            catch (InterruptedException e){
                return;
            }

        }

    }
    private void sayHi(){
        Fields.Field upgrade_thing = new Fields.Field("new_thing", param);
        Fields fields = new Fields();
        fields.put(upgrade_thing);
        String answer=clientManager.sendPost("http://iotmanager.local/",fields);
        System.out.println("Creation status for device uri="+this.uri+" - "+answer);
        if (this.isHaveClient){
            this.id=Integer.parseInt(answer.split(" ")[1].substring(3));
        }

    }
    private void findActions(JsonArray actionGroupsJson){
        for (JsonElement actionGroupJson:actionGroupsJson){
            if (actionGroupJson.getAsJsonObject().has("actions")){
                JsonArray actionsJson=actionGroupJson.getAsJsonObject().get("actions").getAsJsonArray();
                for (JsonElement actionJson:actionsJson){
                    DeviceAction deviceAction=new DeviceAction(actionJson.getAsJsonObject(),this);
                    this.deviceActionHashMap.put(deviceAction.getUri(),deviceAction);
                }
            }
            if (actionGroupJson.getAsJsonObject().has("actionGroups")){
                findActions(actionGroupJson.getAsJsonObject().get("actionGroups").getAsJsonArray());
            }
        }
    }
    public HashMap<String, DeviceAction> getDeviceActionHashMap() {
        return deviceActionHashMap;
    }
    public static HashMap<String, Device> getDevices() {
        return devices;
    }

    public static ArrayList<Thread> getDeviceThreads() {
        return deviceThreads;
    }
    public synchronized String generateJsonFromActions(ArrayList<DeviceAction> actions){
        StringBuilder res=new StringBuilder();
        res.append("[");
        for (DeviceAction deviceAction:actions){
            deviceAction.generateValue();
            res.append("{\"uri\":\""+deviceAction.getUri()+"\",\"value\":\""+deviceAction.getValue()+"\"},");
        }
        String result=res.toString();
        result=result.substring(0,result.length()-1);
        result+="]";
        return result;
    }
    public void sendDataFromActions(){
        System.out.println("Device uri="+this.uri+" is sending data...");
        String res = generateJsonFromActions(new ArrayList<DeviceAction>(deviceActionHashMap.values()));
        String answer=clientManager.sendPut("http://iotmanager.local/"+this.id,res);
        System.out.println("Sending data value for device uri=" + this.uri + " - " + answer);
    }
}
