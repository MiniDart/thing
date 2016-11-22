package local.iotserver.thing.devices;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sergey on 19.11.2016.
 */
public class DeviceAction {
    private String name;
    private String format;
    private boolean isChangeable;
    private int importance;
    private String value;
    private String[] modes=null;
    private int delay=0;
    private Device owner;


    public DeviceAction(String param, Device owner) {
        this.owner=owner;

        String[] arrParam=param.split(", ");
        this.name=arrParam[0];
        this.format=arrParam[1];
        this.isChangeable =arrParam[2].equals("1")?true:false;
        this.importance=Integer.parseInt(arrParam[3]);
        if (arrParam.length>4){
            this.modes=arrParam[4].split(":");
        }
    }
    public Device getOwner() {
        return owner;
    }
    public String[] getModes() {
        return modes;
    }

    public String getName() {
        return name;
    }

    public String getFormat() {
        return format;
    }

    public boolean isChangeable() {
        return isChangeable;
    }

    public int getImportance() {
        return importance;
    }

    public Object getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public void setValue(String value) {
        this.value = value;
        this.delay=5;
        owner.sendDataFromActions();
    }
    public void generateValue(){
        if (delay>0){
            delay--;
            System.out.println("Stop generating value for "+this.name+"; count="+this.delay);
            return;
        }
        if (format.equals("int")){
            value=""+(int)(Math.random()*1000);
            System.out.println("Generated "+this.name+"="+this.value+"; format="+this.format);
        }
        else if (format.equals("list")){
            value=modes[(int)(Math.random()*modes.length)];
            System.out.println("Generated "+this.name+"="+this.value+"; format="+this.format);
        }
        else if (format.equals("date")){
            Date d=new Date();
            d.setHours((int)(Math.random()*24));
            d.setMinutes((int)(Math.random()*60));
            d.setSeconds((int)(Math.random()*60));
            SimpleDateFormat dateFormat=new SimpleDateFormat("hh:mm:ss");
            value=dateFormat.format(d);
            System.out.println("Generated "+this.name+"="+this.value+"; format="+this.format);
        }
    }
}
