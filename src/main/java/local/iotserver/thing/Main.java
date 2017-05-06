package local.iotserver.thing;


import local.iotserver.thing.network.ClientManager;
import local.iotserver.thing.network.ServerManager;

import java.util.ArrayList;


/**
 *
 *
 */
public class Main
{
    public static void main( String[] args )
    {
        ServerManager.getInstance();
        ClientManager.getInstance();
    }
}
