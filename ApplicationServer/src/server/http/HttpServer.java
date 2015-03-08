package server.http;

import server.http.servlet.UploadFile;
import server.config.Config;
import javax.servlet.MultipartConfigElement;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import server.Main;
import server.http.servlet.SignIn;
import server.http.servlet.SignUp;
import server.http.servlet.allpay.TradeOrder;
import server.http.servlet.allpay.TradeResult;

public class HttpServer implements Runnable {

    public static Server SERVER = new Server(Config.HTTP_SERVER_PORT);

    public static void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SERVER.start();
                    Config.SERVER_IS_RUN = true;
                    SERVER.join();
                } catch (Exception ex) {
                    System.out.println("伺服器狀態：啟動時發生錯誤  " + ex);
                }
            }
        }).start();
    }

    public static void stop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SERVER.stop();
                    Config.SERVER_IS_RUN = false;
                } catch (Exception ex) {
                    System.out.println("伺服器狀態：停止時發生錯誤  " + ex);
                }
            }
        }).start();
    }

    @Override
    public void run() {

        SERVER = new Server();
        ServerConnector c = new ServerConnector(SERVER);
        c.setIdleTimeout(1000 * 60 * 60);
        c.setPort(8080);
        SERVER.addConnector(c);

        WebAppContext wac = new WebAppContext();
        wac.setResourceBase(".");

        ServletContextHandler context = wac;

        ServletHolder sh = new ServletHolder(new UploadFile());
        sh.getRegistration().setMultipartConfig(new MultipartConfigElement(Main.PATH));

        context.addServlet(sh, "/UploadFile");
        context.addServlet(new ServletHolder(new TradeResult()), "/TradeResult");
        context.addServlet(new ServletHolder(new TradeOrder()), "/TradeOrder");
        context.addServlet(new ServletHolder(new SignUp()), "/SignUp");
        context.addServlet(new ServletHolder(new SignIn()), "/SignIn");

        HandlerList hl = new HandlerList();
        hl.addHandler(context);

        SERVER.setHandler(hl);

    }

}
