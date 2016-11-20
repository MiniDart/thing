package local.iotserver.thing.handler;

import local.iotserver.thing.devices.Device;
import local.iotserver.thing.network.ClientManager;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;
import org.eclipse.jetty.util.Fields;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Sergey on 18.11.2016.
 */
public class ThingCreationHandler extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        String answer=req.getParameter("new_thing");
        System.out.println(answer);
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().println(answer);
        response.setStatus(HttpServletResponse.SC_OK);
        Thread t=new Thread(new Device(answer));
        Device.getDeviceThreads().add(t);
        t.start();
       /* ClientManager clientManager=ClientManager.getInstance();
        HttpClient client=clientManager.getClient("default");
        Fields.Field new_thing = new Fields.Field("new_thing", answer);
        Fields fields = new Fields();
        fields.put(new_thing);
        ContentResponse constructorResponse=null;
        try {
            constructorResponse = client.FORM("http://iotmanager.local/newthing", fields);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        System.out.println(constructorResponse.getContentAsString());*/

    }
}
