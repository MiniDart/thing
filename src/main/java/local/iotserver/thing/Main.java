package local.iotserver.thing;


import local.iotserver.thing.network.ClientManager;
import local.iotserver.thing.network.ServerManager;


/**
 *
 *
 */
public class Main
{
    public static void main( String[] args )
    {
        ServerManager serverManager=ServerManager.getInstance();
        ClientManager clientManager=ClientManager.getInstance();
    }
}
