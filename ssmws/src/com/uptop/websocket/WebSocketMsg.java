package com.uptop.websocket;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import mapper.RelationshipMapper;
import net.sf.json.JSONObject;

import org.springframework.web.context.ContextLoader;

import po.GameState;
import po.Message;
import po.Relationship;
import po.User;
import service.MessageService;
import service.RelationshipService;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 * @author uptop
 */
@ServerEndpoint("/websocketMsg")
public class WebSocketMsg {
	MessageService messageService=(MessageService) ContextLoader.getCurrentWebApplicationContext().getBean("messageService"); 
	RelationshipService relationshipService=(RelationshipService) ContextLoader.getCurrentWebApplicationContext().getBean("relationshipService");
	String username;
	//静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    public static HashMap<String,WebSocketMsg> socketMap=new HashMap<String, WebSocketMsg>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 连接建立成功调用的方法
     *
     * @param session 可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        Map<String,List<String>> map=session.getRequestParameterMap();
        List<String> list=map.get("username");
        this.username=list.get(0);
        socketMap.put(this.username, this);
       
       
        
           
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
    	
      
        socketMap.remove(username);
        subOnlineCount();           //在线数减1
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     * @throws IOException 
     */
    @OnMessage
    public void onMessage(String message, Session session)  {
    	System.out.print(message);
    	JSONObject jsonObject=new JSONObject().fromObject(message);
    	JSONObject msg=new JSONObject();
    	String tt=null;
    	int type=jsonObject.getInt("type");
    	if(type==0){
    		int msgId=jsonObject.getInt("msgId");
    		messageService.isRead(msgId);
    		return;
    	}
    	String to=jsonObject.getString("to");
    	Message mm= messageService.insertMessage(username, to, "", type, 0,new Timestamp(new Date().getTime()));
    	WebSocketMsg socketMsg=socketMap.get(to);
		msg.element("to", to);
		msg.element("msgId", mm.getMsgId());
		msg.element("type", type);
		msg.element("from", username);
		msg.element("content","" );
		msg.element("createDate", mm.getCreateDate());
		
		if(type==2){
			relationshipService.insertRelationship(username, to);
		}
		try{
			socketMsg.sendMessage(msg.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
    	
    }

    /**
     * 发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

    /**
     * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);

    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketMsg.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketMsg.onlineCount--;
    }


    public void sendMsg(String msg) {
       
    }


}
