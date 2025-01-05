package com.chat_app;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.json.*;

//Managing overall communication logic
public class Server {
	
	private static final int port = 9095;
	private static Map<String,PrintWriter> clients = Collections.synchronizedMap(new HashMap<>());
	private static SecretKey desKey;
	
    public static void main(String[] args){
        
        
        System.out.println("Server started on " + port +".");
        
        try(ServerSocket listener = new ServerSocket(port)) {
        	//Generate DES key or load key from file
        	if(!DESMethod.loadKeyFromFile(null)) {
        		DESMethod.generateKey();
        		desKey = DESMethod.getSecretKey();
        		System.out.println(DESMethod.getSecretKey().toString());
        		System.out.println("New DES key has been generated.");
        	}else {
        		desKey = DESMethod.getSecretKey();
        		System.out.println("DES key has been loaded from file.");
        	}
        	
//        	desKey = DESMethod.generateKey();
            
            while (true) { 
                	//Accept a new client
                    Socket socket = listener.accept();
                    new ServerThread(socket).start();         
            }
        } 
        catch (IOException e) {

            e.printStackTrace();
        }	
        
    }
    
    //To broadcast clients' message
    private static void broadcast(String message, String sender) {
    	synchronized(clients){
    		for(Map.Entry<String, PrintWriter> client : clients.entrySet()) {
    			if(!client.getKey().equals(sender) && !message.startsWith("DESKEY:")) {
    				client.getValue().println(message);
    			}
    			
    		}
    	}
    }
    
    //Dynamic change of users list
    private static void broadcastUserList() {
    	String userListMsg = "USERLIST:" + String.join(",", clients.keySet());
    	synchronized(clients) {
    		for(PrintWriter writer : clients.values()) {
    			writer.println(userListMsg);
    		}
    	}
    	
    }

    
    //Managing a single client
    private static class ServerThread extends Thread{

        private Socket socket;
        private String username;
        private PrintWriter out;

        public ServerThread (Socket socket){
            this.socket = socket;
        }
        
        
        @Override
        public void run(){

            try(BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {	
                    out = new PrintWriter(socket.getOutputStream(), true);
                    
                    username = in.readLine();
                    if(username == null || username.isBlank()) {
                    	return;
                    }
                    synchronized(clients) {
                    	clients.put(username, out);
                    }
                    
                    System.out.println(username +"("+socket.getLocalAddress()+") connected.");
                    broadcast(username + " has entered the chatting room.", username);
                    broadcastUserList();
                    
                    shareDESKeytoClient();
                    
                    //Handle incoming messages from this client
                    String msgFromClient;
                    while((msgFromClient = in.readLine()) != null) {
                    	//Handle client exit
                    	if(msgFromClient.equalsIgnoreCase("/exit")) {
                    		break;
                    	}
                    	
                    	try {
							JSONObject jsonMsg = new JSONObject(msgFromClient);
							System.out.println(msgFromClient);
							System.out.println(jsonMsg);
							String method = jsonMsg.getString("method");
							String content = jsonMsg.getString("content");
							
							switch(method) {
	                    	case "Caesar":
	                    		String CdecryptedMsg = CaesarCipherMethod.decrypt(content);
	                    		broadcast(username + ": "+ CdecryptedMsg, username);
	                    		break;
	                    	case "DES":
	                    		String DdecryptedMsg = DESMethod.decrypt(content);
	                    		broadcast(username + ": "+DdecryptedMsg, username);
	                    		break;
	                    	case "Unencrypted":
	                    	default:
	                    		broadcast(username + ": "+content, username);
	                    		
	                    	}
                    	}catch (Exception e) {
	                    		out.println("Invalid message format.");
	                    		e.printStackTrace();
	                    	}
                    	}
            } catch (IOException e) {

                    System.out.println("Error handling client: "+ username);
       

            } finally {
            	//Remove client from map when client left
            	if(username != null) {
            		synchronized(clients) {
            			clients.remove(username);
            		}
                	broadcast(username + " has left the chatting room.", username);
                	broadcastUserList();
                	System.out.println(username + " disconnected.");
            	}
            	//Close socket when client disconnected
            	try {
            		socket.close();
            	} catch (IOException e) {
            		e.printStackTrace();
            	}
            }
        }
        
        private void shareDESKeytoClient() {
        	try {
        		String encodedKey = Base64.getEncoder().encodeToString(desKey.getEncoded());
        		JSONObject jsonKey = new JSONObject();
        		jsonKey.put("type", "DESKEY:");
        		jsonKey.put("content", encodedKey);
        		out.println(jsonKey.toString());
//        		String encodeKey = Base64.getEncoder().encodeToString(DESMethod.getSecretKey().getEncoded());
//        		out.println("DESKEY:" + encodeKey);
        	}catch (Exception e) {
        		e.printStackTrace();
        	}
        }
        
        
    }
    
 
        
//        private String decryptMessage (String method, String msg) {
//        	try {
//        		switch (method) {
//        		case "Caesar":
//        			return CaesarCipherMethod.decrypt(msg);
//        		case "DES":
//        			return DESMethod.decrypt(msg);
//        		default:
//        			return msg;
//        		}
//        	}catch (Exception e) {
//        		e.printStackTrace();
//        		return "Error decrypting message.";
//        	}
//        }
}
