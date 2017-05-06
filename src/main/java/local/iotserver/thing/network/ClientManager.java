package local.iotserver.thing.network;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.util.Fields;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

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
        if (name==null) return clientMap.get("default");
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
        HttpClient client = getInstance().getClient(null);
        ContentResponse response = null;
        try {
            response = client.FORM(url, fields);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.getContentAsString();
    }
    public String sendPut(String urlS,String data){
        try {
            URL url = new URL(urlS);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("PUT");
            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
            out.write(data);
            out.close();
            BufferedReader reader=new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
            return reader.readLine();
        }
        catch (MalformedURLException e){
            return "Error";
        }
        catch (IOException e){
            return "Error";
        }
    }
}
