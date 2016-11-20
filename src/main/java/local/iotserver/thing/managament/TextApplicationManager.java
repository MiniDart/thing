package local.iotserver.thing.managament;

import local.iotserver.thing.network.ClientManager;
import local.iotserver.thing.network.ServerManager;
import org.eclipse.jetty.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Sergey on 19.11.2016.
 */
public class TextApplicationManager implements Runnable{
    public void run() {
        BufferedReader reader=null;
        try {
            reader=new BufferedReader(new InputStreamReader(System.in));
            String command="";
            while (!command.equals("exit")){
                command=reader.readLine();
                System.out.println(command);
                commandHandler(command);
            }
            System.out.println("exit from textApplication");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    private void commandHandler(String command){
        if (command.equals("exit")){
            System.out.println("Start of stops");
            ServerManager serverManager=ServerManager.getInstance();
            serverManager.stopAllServers();
            ClientManager clientManager=ClientManager.getInstance();
            clientManager.stopAllClients();
            System.out.println("End of stops");
        }
    }
}
