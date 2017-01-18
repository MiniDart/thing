package local.iotserver.thing.network;

import local.iotserver.thing.handler.GetActionDataHandler;
import local.iotserver.thing.handler.ThingCreationHandler;
import local.iotserver.thing.handler.UpgradeActionHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Sergey on 19.11.2016.
 */
public class ServerManager {
    private static final ServerManager serverManager=new ServerManager();
    public static ServerManager getInstance(){
        return serverManager;
    }
    private final HashMap<String,Server> serverMap=new HashMap<String, Server>();

    private ServerManager() {
        ThingCreationHandler thingCreationHandler=new ThingCreationHandler();
        UpgradeActionHandler upgradeActionHandler=new UpgradeActionHandler();
        GetActionDataHandler getActionDataHandler=new GetActionDataHandler();

        Server server=new Server(3000);
        ServletContextHandler context=new ServletContextHandler(ServletContextHandler.SESSIONS);
        ResourceHandler resourceHandler = new ResourceHandler();

        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{ "thing.html" });
        resourceHandler.setResourceBase("pages/");
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, context });
        server.setHandler(handlers);
        context.addServlet(new ServletHolder(thingCreationHandler),"/newdevice");
        context.addServlet(new ServletHolder(upgradeActionHandler),"/upgradeaction");
        context.addServlet(new ServletHolder(getActionDataHandler),"/getactiondata");
        try {
            server.start();
            server.join();
        }
        catch (InterruptedException e){
            System.out.println("Server was interrupted");
        }
        catch (Exception e){
            System.out.println("Server failed to start");
        }
        serverMap.put("default",server);
    }
    public Server getServer(String name){
        return serverMap.get(name);
    }
    public void putServer(String name,  Server server){
        serverMap.put(name,server);
    }
    public void stopServer(String name){
        try{
            serverMap.get(name).stop();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        serverMap.remove(name);
    }
    public void stopAllServers(){
        Collection<Server> clients=serverMap.values();
        for (Server c:clients){
            try {
                c.stop();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        serverMap.clear();
    }
}
