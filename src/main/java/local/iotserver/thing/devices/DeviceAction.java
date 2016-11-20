package local.iotserver.thing.devices;

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

    public DeviceAction(String param) {
        String[] arrParam=param.split(", ");
        this.name=arrParam[0];
        this.format=arrParam[1];
        this.isChangeable =arrParam[2].equals("1")?true:false;
        this.importance=Integer.parseInt(arrParam[3]);
        if (arrParam.length>4){
            this.modes=arrParam[4].split(":");
        }
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
    }
    public void generateValue(){
        if (format.equals("int")){
            value=""+(int)Math.random()*1000;
        }
        else if (format.equals("list")){
            value=modes[(int)Math.random()*modes.length];
        }
    }
}
