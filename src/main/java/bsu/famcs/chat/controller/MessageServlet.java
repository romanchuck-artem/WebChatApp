package bsu.famcs.chat.controller;

import bsu.famcs.chat.model.Message;
import bsu.famcs.chat.model.MessageStorage;
import bsu.famcs.chat.storage.xml.XMLHistoryUtil;
import bsu.famcs.chat.exception.MyException;
import bsu.famcs.chat.util.MessageUtil;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.PrintWriter;

import static bsu.famcs.chat.util.MessageUtil.*;
import static bsu.famcs.chat.util.ServletUtil.APPLICATION_JSON;
import static bsu.famcs.chat.util.ServletUtil.getMessageBody;

@WebServlet("/chat")
public class MessageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(MessageServlet.class.getName());

    @Override
    public void init() throws ServletException {
        try {
            loadHistory();
        } catch (TransformerException | ParserConfigurationException | IOException | SAXException e) {
            logger.error(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getParameter(TOKEN);
        logger.info("Get request");
        if (token != null && !"".equals(token)) {
            int index = getIndex(token);
            String messages = serverResponse(0);
            response.setContentType(APPLICATION_JSON);
            PrintWriter out = response.getWriter();
            out.print(messages);
            out.flush();
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "token parameter is absent");
            logger.error("Token parameter is absent");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Post request");
        String data = getMessageBody(request);
        logger.info("Request data : " + data);
        try {
            JSONObject json = stringToJson(data);
            Message message = jsonToMessage(json);
            logger.info(message.getUserMessage());
            XMLHistoryUtil.addMessage(message);
            MessageStorage.addMessagePost(message);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ParseException | ParserConfigurationException | SAXException | TransformerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error("Invalid message");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Put request");
        String data = getMessageBody(request);
        logger.info("Request data : " + data);
        Message message = null;
        try {
            JSONObject jsonObject = stringToJson(data);
            message = jsonToCurrentMessage(jsonObject);
            message.setChangeDate();
            Message updated = XMLHistoryUtil.updateMessage(message);
            MessageStorage.addMessagePut(updated);
        } catch (ParseException | ParserConfigurationException | SAXException | XPathExpressionException | TransformerException |
                NullPointerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error("Invalid message");
        } catch (MyException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error("Message with id : " + message.getId() + " doesn't exist or was deleted");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Delete request");
        String data = getMessageBody(request);
        logger.info("Request data : " + data);
        Message message = null;
        try {
            JSONObject json = stringToJson(data);
            message = jsonToCurrentMessage(json);
            message.isDelete();
            message.setChangeDate();
            Message updated = XMLHistoryUtil.updateMessage(message);
            MessageStorage.addMessageDelete(updated);
        } catch (ParseException | ParserConfigurationException | SAXException | XPathExpressionException | TransformerException |
                NullPointerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error("Invalid message");
        } catch (MyException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.error("Message with id : " + message.getId() + " doesn't exist or was deleted");
        }
    }

    @SuppressWarnings("unchecked")
    private String serverResponse(int index) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MESSAGES, MessageStorage.getSubHistory(index));
        jsonObject.put(TOKEN, getToken(MessageStorage.getSize()));
        return jsonObject.toJSONString();
    }


    private void loadHistory() throws TransformerException, ParserConfigurationException, IOException,
            SAXException {
        if (!XMLHistoryUtil.isStorageExist()) {
            XMLHistoryUtil.createStorage();
            logger.info(MessageStorage.getSubHistory(0));
        } else {
            MessageStorage.addAll(XMLHistoryUtil.getMessages());
            logger.info('\n' + MessageStorage.getStringView());
            logger.info(MessageStorage.getSubHistory(0));
        }
    }
}