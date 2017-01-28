package local.iotserver.thing.devices;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Sergey on 09.12.2016.
 */
public class SupportDeviceAction extends DeviceAction {
    private DeviceAction actionOwner;
    private boolean isIndividual;

    public SupportDeviceAction(JsonObject param, Device owner, DeviceAction actionOwner) {
        super(param, owner);
        this.actionOwner=actionOwner;
        this.isIndividual=param.get("isIndividual").getAsBoolean();

    }
    public boolean isIndividual() {
        return isIndividual;
    }

    public DeviceAction getActionOwner() {

        return actionOwner;
    }
}
