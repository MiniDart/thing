package local.iotserver.thing.devices;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import local.iotserver.thing.network.ClientManager;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.util.Fields;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sergey on 19.11.2016.
 */
public class Device implements Runnable {
    private static HashMap<Integer,Device> devices=new HashMap<Integer, Device>();
    private static ArrayList<Thread> deviceThreads=new ArrayList<Thread>();
    private String param;
    private ArrayList<DeviceAction> deviceActions=new ArrayList<DeviceAction>();
    private int id;
    private String name;
    private  String thingGroup;
    static ClientManager clientManager=ClientManager.getInstance();
    public Device(String param) {
        this.param=param;
        Gson gson=new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String > read = gson.fromJson(param, type);
        this.id=Integer.parseInt(read.get("id"));
        this.name=read.get("name");
        this.thingGroup=read.get("thingGroup");
        for (Map.Entry<String,String> entry:read.entrySet()){
            if (entry.getKey().indexOf("action_")==0){
                deviceActions.add(new DeviceAction(entry.getValue()));
            }
        }
        devices.put(this.id,this);
        sayHi();
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("Device id="+this.id+" is sending data...");
            String res = generateJsonString();
            HttpClient client = clientManager.getClient("default");
            Fields.Field upgrade_thing = new Fields.Field("thing_param", res);
            Fields fields = new Fields();
            fields.put(upgrade_thing);
            ContentResponse response = null;
            try {
                response = client.FORM("http://iotmanager.local/upgradeactionsdata", fields);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Sending data status for device id="+this.id+" - "+response.getContentAsString());
            try {
                Thread.sleep(10000);
            }
            catch (InterruptedException e){
                return;
            }

        }

    }
    private void sayHi(){
        HttpClient client = clientManager.getClient("default");
        Fields.Field upgrade_thing = new Fields.Field("new_thing", param);
        Fields fields = new Fields();
        fields.put(upgrade_thing);
        ContentResponse response = null;
        try {
            response = client.FORM("http://iotmanager.local/newthing", fields);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Creation status for device id="+this.id+" - "+response.getContentAsString());
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
    private String generateJsonString(){
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
}
