package local.iotserver.thing.network;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.util.Fields;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Sergey on 19.11.2016.
 */
public class ClientManager {
    private static final ClientManager clientManager=new ClientManager();
    public static ClientManager getInstance(){
        return clientManager;
    }
    private final HashMap<String,HttpClient> clientMap=new HashMap<String, HttpClient>();

    private ClientManager() {
        HttpClient client=new HttpClient();
        client.setFollowRedirects(false);
        try {
            client.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        clientMap.put("default",client);
    }
    public HttpClient getClient(String name){
        return clientMap.get(name);
    }
    public void putClient(String name, HttpClient client){
        clientMap.put(name,client);
    }
    public void stopClient(String name){
        try{
            clientMap.get(name).stop();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        clientMap.remove(name);
    }
    public void stopAllClients(){
        Collection<HttpClient> clients=clientMap.values();
        for (HttpClient c:clients){
            try {
                c.stop();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        clientMap.clear();
    }
    public String sendPost(String url,Fields fields){
        HttpClient client = getInstance().getClient("default");
        ContentResponse response = null;
        try {
            response = client.FORM(url, fields);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.getContentAsString();
    }
}
