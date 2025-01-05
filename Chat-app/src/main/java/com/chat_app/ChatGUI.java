package com.chat_app;
	
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;
import org.json.*;
import javax.crypto.SecretKey;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class ChatGUI extends Application {
	
	private TextArea chatBoard;
	private TextField messageBox;
	private Button sendBtn;
	private ComboBox<String> encryptOptions;
//	private Button caesarBtn;
//	private Button desBtn;
	private Button loginBtn;
	private ListView<String> userList;
	private TextField usernameField;
	private Stage primaryStage;
	private PrintWriter out;
	private BufferedReader in;
	private Socket socket;
	private Button saveKeyBtn;
	private Button loadKeyBtn;
	private SecretKey desKey;
	private String username;

	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		showLoginScreen();

	}
	//Login in screen
	private void showLoginScreen() {
		Label usernameLabel = new Label("Username: ");
		usernameField = new TextField();
		usernameField.setPrefWidth(10);
		loginBtn = new Button("Login");
		
		
		loginBtn.setOnAction(e -> {
			connectToServer();
		});
		
		VBox loginLayout = new VBox(30,usernameLabel,usernameField,loginBtn);
		loginLayout.setSpacing(10);
		Scene loginScene = new Scene(loginLayout,600,400);
		primaryStage.setTitle("Chat Room Login");
		primaryStage.setScene(loginScene);
		primaryStage.show();
		
		
		
		
	}
	
	//Chat screen
	private void showChatRoom() {
		chatBoard = new TextArea();
		chatBoard.setEditable(false);
		chatBoard.setWrapText(true);
		chatBoard.setPrefHeight(300);
		ScrollPane chatScrollPane = new ScrollPane(chatBoard);
		chatScrollPane.setFitToWidth(true);
		chatScrollPane.setFitToHeight(true);
		
		messageBox = new TextField();
		messageBox.setPromptText("Please type your message here...");	
		sendBtn = new Button("Send");
//		caesarBtn = new Button("Caesar Cipher");
//		desBtn = new Button("DES");
		encryptOptions = new ComboBox<>();
		encryptOptions.getItems().addAll("Unencrypted","Caesar","DES");
		encryptOptions.setValue("Unencrypted");
		
		Label userListLabel = new Label("Online User");
		userList = new ListView<>();
		userList.setPrefWidth(150);
		userList.setPlaceholder(new Label("No users online"));
		
		saveKeyBtn = new Button("Save DES key");
		loadKeyBtn = new Button ("Load DES key");
		
		messageBox.setOnAction(e -> sendMsg());
		sendBtn.setOnAction(e -> sendMsg());
		
		saveKeyBtn.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Save DES Key");
			File file = fileChooser.showSaveDialog(primaryStage);
			
			if(DESMethod.saveKeyToFile(file)){
				appendMsg("System: DES key saved.");
			}else {
				appendMsg("System: Failed to save DES key.");
			}
			
		});
		loadKeyBtn.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Load DES Key");
			File file = fileChooser.showOpenDialog(primaryStage);
			if(DESMethod.loadKeyFromFile(file)) {
				appendMsg("System: DES Key loaded from file.");
			}else {
				appendMsg("System: Failed to load DES key.");
			}
		});
		
		HBox root = new HBox(10);
		root.setPadding(new Insets(10));
		HBox inputBox = new HBox(10, messageBox,encryptOptions, sendBtn);
		inputBox.setPadding(new Insets(10));
//		HBox methodBox = new HBox(10,caesarBtn, desBtn );
//		methodBox.setPadding(new Insets(10));
		VBox chatBox = new VBox(10, chatScrollPane, inputBox);
		chatBox.setSpacing(5);
		VBox sideBox = new VBox(10, userListLabel,userList, saveKeyBtn, loadKeyBtn);
		root.getChildren().addAll(chatBox, sideBox);
		

		Scene scene = new Scene(root,600,400);
//		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Chatting Room");
		primaryStage.show();
	}
	
	

	
	private void connectToServer() {
		username = usernameField.getText();
		if(username == null || username.isBlank()) {
			showError("Username cannot be empty");
			return;
		}
		try {
//			socket = new Socket("3.95.161.71", 9095);
			socket = new Socket("localhost", 9095);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(),true);
			
			out.println(username);
			showChatRoom();
			
			//Start a thread to handle message from server
			new Thread(() -> {
			    try {
			        String msgFromServer;
			        while ((msgFromServer = in.readLine()) != null) {
			            System.out.println("Received: " + msgFromServer);//For testing

			            // Check if the message is a JSON object
			            if (msgFromServer.trim().startsWith("{") && msgFromServer.trim().endsWith("}")) {
			                try {
			                    // Parse the message as JSON
			                    JSONObject jsonMsg = new JSONObject(msgFromServer);
			                    String type = (String)jsonMsg.get("type");
			                    System.out.println(type);
			                    String content = (String)jsonMsg.get("content");
			                    System.out.println(content);

			                    if (type.equals("DESKEY:")) {
			                        // Decode the key and set it
			                    	byte[]decodedKey = Base64.getDecoder().decode(content);
			                        desKey = DESMethod.decodeKay(decodedKey);
			                        DESMethod.setSecretKey(desKey);
			                        appendMsg("System: DES key received from server.");
			                    } else {
			                        // Handle other types of JSON messages
			                        appendMsg("System: Received unknown JSON message type.");
			                    }
			                } catch (Exception e) {
			                    System.err.println("Error parsing JSON message: " + e.getMessage());
			                    appendMsg("System: Error parsing JSON message.");
			                }
			            } else if (msgFromServer.startsWith("USERLIST:")) {
			                // Handle non-JSON USERLIST messages
			                updateUserList(msgFromServer);
			            } else {
			                // Handle other messages
			                appendMsg(msgFromServer);
			            }
			        }
			    } catch (IOException e) {
			        appendMsg("System: Disconnected from server.");
			    }
			}).start();
			
		}catch (IOException e) {
			showError("Unable to connect to server. Please try again.");
		}
	}
	
	//Send encrypted message to server according to user's choice
	private void sendMsg() {
		String msg = messageBox.getText();		
		if(msg == null || msg.isEmpty()) {
			return;
		}
		String encryptionMethod = encryptOptions.getValue();
		String encryptMsg = msg;
		
		switch (encryptionMethod) {
		case "Caesar":
			encryptMsg = CaesarCipherMethod.encrypt(msg);
//			out.println("ENCRYPTED:Caesar|");
//			out.println(caesarEncryptedMsg);
//			jsonMsg.put("content", caesarEncryptMsg);
			appendMsg(username +"(You)(Caesar): " + msg);
			break;
			
		case "DES":
			if(desKey == null) {
				System.err.println("DES key is null!");
				return;
			}
			System.out.println(desKey);
			System.out.println(DESMethod.getSecretKey());
			System.out.println(msg);
			encryptMsg = DESMethod.encrypt(msg);
//			out.println("ENCRYPTED:DES|");
//			out.println(desEncryptedMsg);
//			DESMethod.setSecretKey(desKey);
			System.out.println(encryptMsg);
			appendMsg(username +"(You)(DES) :" + msg);
			break;
		case "Unencrypted":
		default:
			encryptMsg = msg;
			appendMsg(username +"(You): " + msg);
			
		}
		
		//Construct JSON object and send to server
//		Map<String, String> messageMap = new HashMap<>();
//		messageMap.put("method", encryptionMethod);
//		messageMap.put("content", encryptMsg);
		JSONObject jsonMsg = new JSONObject();
		jsonMsg.put("method", encryptionMethod);
		jsonMsg.put("content", encryptMsg);
		out.println(jsonMsg.toString());
		System.out.println(jsonMsg.toString());
		messageBox.clear();
	}
	
	private void appendMsg(String msg) {
//		Platform.runLater(()-> chatBoard.appendText(msg + "\n"));
		if(!msg.startsWith("DESKEY:")) {
			chatBoard.appendText(msg + "\n");
		}
		
	}
	
	private void updateUserList(String userListMsg) {
		Platform.runLater(()-> {
			userList.getItems().clear();
			String[] onlineUsers = userListMsg.substring("USERLIST:".length()).split(",");
			userList.getItems().setAll(onlineUsers);
		});
	}
	
	//Display error
	private void showError(String errorMsg) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(errorMsg);
		alert.showAndWait();
	}
	

	@Override
	public void stop() throws Exception {
		if(out != null) {
			out.println("/exit");
			out.close();
		}
		
		if(in != null) {
			in.close();
		}
		
		if(socket != null && !socket.isClosed()) {
			socket.close();
		}
		super.stop();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
