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
    private ArrayList<DeviceAction> deviceActions=new ArrayList<DeviceAction>();
    private int id;
    private String name;
    private  String thingGroup;
    private boolean haveStatisticsElements;
    static ClientManager clientManager=ClientManager.getInstance();
    public Device(String param) {
        this.param=param;
        JsonObject mainFeatures = new JsonParser().parse(param).getAsJsonObject();
        this.id=mainFeatures.get("id").getAsInt();
        this.name=mainFeatures.get("name").getAsString();
        this.thingGroup=mainFeatures.get("thingGroup").getAsString();
        JsonArray actionGroups=mainFeatures.get("actionGroups").getAsJsonArray();
        for (int i=0;i<actionGroups.size();i++){
            JsonArray actions=actionGroups.get(i).getAsJsonObject().get("actions").getAsJsonArray();
            for (int l=0;l<actions.size();l++){
                JsonObject action=actions.get(l).getAsJsonObject();
                deviceActions.add(new DeviceAction(action,this));
            }
        }
        for (int i=0;i<deviceActions.size();i++){
            if (deviceActions.get(i).isNeedStatistics()){
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
            sendDataFromActions();
            try {
                Thread.sleep(10000);
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

    public ArrayList<DeviceAction> getDeviceActions() {
        return deviceActions;
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
    private String generateJsonFromAllActions(){
        String res="{\"thing_id\":\""+this.id+"\",";
        for (int i=0;i<deviceActions.size();i++){
            DeviceAction d=deviceActions.get(i);
            d.generateValue();
            res+="\""+d.getName()+"\":\""+d.getValue()+"\"";
            if (i!=deviceActions.size()-1){
                res+=",";
            }
        }
        res+="}";
        return res;
    }
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
    public void sendDataFromActions(){
        System.out.println("Device id="+this.id+" is sending data...");
        String res = generateJsonFromStatisticsActions();
        System.out.println("jsonString="+res);
        /*
        Fields.Field upgrade_thing = new Fields.Field("thing_param", res);
        Fields fields = new Fields();
        fields.put(upgrade_thing);
        String  answer=clientManager.sendPost("http://iotmanager.local/upgradeactionsdata",fields);
        System.out.println("Sending data status for device id="+this.id+" - "+answer);
        */
    }
}
