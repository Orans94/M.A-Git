package magithub.servlets;

import com.google.gson.Gson;
import engine.chat.ChatManager;
import engine.chat.SingleChatEntry;
import magithub.constants.Constants;
import magithub.utils.ServletUtils;
import magithub.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ChatServlet extends HttpServlet
{
    private static class ChatAndVersion
    {
        final private List<SingleChatEntry> entries;
        final private int version;

        public ChatAndVersion(List<SingleChatEntry> entries, int version)
        {
            this.entries = entries;
            this.version = version;
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String requestType = request.getParameter("requestType");
        switch (requestType)
        {
            case "chatContent":
                chatContentProcessRequest(request, response);
                break;
            case "sendMessage":
                sendMessageProcessRequest(request, response);
        }
    }

    private void sendMessageProcessRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        ChatManager chatManager = ServletUtils.getChatManager(getServletContext());
        String username = SessionUtils.getUsername(request);
        if (username == null)
        {
            response.sendRedirect(request.getContextPath() + "/index.html");
        }

        String userChatString = request.getParameter("userstring");
        if (userChatString != null && !userChatString.isEmpty())
        {
            logServerMessage("Adding chat string from " + username + ": " + userChatString);
            synchronized (getServletContext())
            {
                chatManager.addChatString(userChatString, username);
            }
        }
    }

    private void logServerMessage(String message)
    {
        System.out.println(message);
    }

    private void chatContentProcessRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("application/json");
        ChatManager chatManager = ServletUtils.getChatManager(getServletContext());
        String username = SessionUtils.getUsername(request);
        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/index.html");
        }

        /*
        verify chat version given from the user is a valid number. if not it is considered an error and nothing is returned back
        Obviously the UI should be ready for such a case and handle it properly
         */
        int chatVersion = ServletUtils.getIntParameter(request, Constants.CHAT_VERSION_PARAMETER);
        if (chatVersion == Constants.INT_PARAMETER_ERROR) {
            return;
        }

        /*
        Synchronizing as minimum as I can to fetch only the relevant information from the chat manager and then only processing and sending this information onward
        Note that the synchronization here is on the ServletContext, and the one that also synchronized on it is the chat servlet when adding new chat lines.
         */
        int chatManagerVersion = 0;
        List<SingleChatEntry> chatEntries;
        synchronized (getServletContext()) {
            chatManagerVersion = chatManager.getVersion();
            chatEntries = chatManager.getChatEntries(chatVersion);
        }

        // log and create the response json string
        ChatAndVersion cav = new ChatAndVersion(chatEntries, chatManagerVersion);
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(cav);
        logServerMessage("Server Chat version: " + chatManagerVersion + ", User '" + username + "' Chat version: " + chatVersion);
        logServerMessage(jsonResponse);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }

    }
}
