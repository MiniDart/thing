package local.iotserver.thing.devices;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Sergey on 19.11.2016.
 */
public class DeviceAction {
    private String name;
    private String format;
    private boolean isChangeable;
    private String value;
    private String[] modes=null;
    private int delay=0;
    private Device owner;
    private String uri;
    private String from;
    private String to;


    public DeviceAction(JsonObject param, Device owner) {
        this.owner=owner;
        this.name=param.get("name").getAsString();
        this.uri=param.get("uri").getAsString();

        this.format=param.get("format").getAsString();
        this.isChangeable=param.get("isChangeable").getAsBoolean();
        if (param.has("range")) {
            if (this.format.equals("list")) {
                JsonArray jsonModes = param.getAsJsonArray("range");
                this.modes = new String[jsonModes.size()];
                for (int i = 0; i < modes.length; i++) {
                    modes[i] = jsonModes.get(i).getAsJsonObject().get("name").getAsString();
                }
            }
            else {
                JsonArray jsonRange=param.getAsJsonArray("range");
                this.from=jsonRange.get(0).getAsJsonObject().has("from")?jsonRange.get(0).getAsJsonObject().get("from").getAsString():null;
                this.to=jsonRange.get(0).getAsJsonObject().has("to")?jsonRange.get(0).getAsJsonObject().get("to").getAsString():null;
            }
        }
        if (param.has("support")){
        JsonArray jsonSupports=param.getAsJsonArray("support");
            for (int i = 0; i < jsonSupports.size(); i++) {
                DeviceAction d=new DeviceAction(jsonSupports.get(i).getAsJsonObject(),this.owner);
                this.owner.getDeviceActionHashMap().put(d.getUri(),d);
            }
        }



    }

    public String getUri() {
        return uri;
    }

    public Object getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
        this.delay=10;
    }
    public void generateValue(){
        if (delay>0){
            delay--;
            System.out.println("Stop generating value for "+this.name+"; count="+this.delay);
            return;
        }
        if (format.equals("number")){
            int from=this.from==null?(-10000):Integer.parseInt(this.from);
            int to=this.to==null?(10000):Integer.parseInt(this.to);
            value=""+(int)((Math.random()*(to-from))+from);
            System.out.println("Generated "+this.name+"="+this.value+"; format="+this.format);
        }
        else if (format.equals("list")){
            value=modes[(int)(Math.random()*modes.length)];
            System.out.println("Generated "+this.name+"="+this.value+"; format="+this.format);
        }
    }
}
