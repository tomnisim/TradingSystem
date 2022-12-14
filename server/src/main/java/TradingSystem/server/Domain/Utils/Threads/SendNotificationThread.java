package TradingSystem.server.Domain.Utils.Threads;

import TradingSystem.server.Domain.Utils.HttpUtility;
import TradingSystem.server.Domain.Utils.Logger.SystemLogger;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static TradingSystem.server.Domain.Utils.HttpUtility.METHOD_GET;
import static TradingSystem.server.Domain.Utils.HttpUtility.METHOD_POST;
import static java.net.HttpURLConnection.HTTP_OK;


public class SendNotificationThread implements Runnable {

    private SimpMessagingTemplate smt;
    private String dest;
    private String message;

    public SendNotificationThread(SimpMessagingTemplate smt, String dest, String message) {
        this.smt = smt;
        this.dest = dest;
        this.message = message;
    }

    /**
     * Requirement 6
     * this thread task is to send a notification WS-based to the client.
     */
    @Override
    public void run() {
        SystemLogger.getInstance().add_log("System Send Notification To:" + dest);
        smt.convertAndSend(dest, message);
    }

}
