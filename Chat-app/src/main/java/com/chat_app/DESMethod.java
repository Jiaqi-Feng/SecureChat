package com.chat_app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class DESMethod {
	private static SecretKey secretKey;

	
	//Generate a DES key
	public static void generateKey(){
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("DES");
			secretKey = keyGen.generateKey();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Save key to file
	public static boolean saveKeyToFile(File file){
		try {
			if(file != null) {
				try(ObjectOutputStream keyFile = new ObjectOutputStream(new FileOutputStream(file))){
					keyFile.writeObject(secretKey.getEncoded());
					System.out.println("Key save to "+ file.getAbsolutePath());
				}
				return true;
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//Load key from a file
	public static boolean loadKeyFromFile(File file){
		try {
			if(file != null) {
				try(ObjectInputStream keyFile = new ObjectInputStream(new FileInputStream(file))){
					byte[] keyBytes = (byte[]) keyFile.readObject();
					secretKey = new SecretKeySpec(keyBytes, "DES");
					System.out.println("Key load from "+ file.getAbsolutePath());
				}
				return true;
			}
			
		}catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//Encrypt message
	public static String encrypt(String msg){
		try {
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] encryptedBytes = cipher.doFinal(msg.getBytes());
			String encryptedMsg = Base64.getEncoder().encodeToString(encryptedBytes);
			return encryptedMsg;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	//Decrypt message
	public static String decrypt (String msg){
		try {
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(msg));
			return new String(decryptedBytes);
		} catch (Exception e) {
			e.printStackTrace();
			return "Error decrypting message.";
		}
		
	}
	
	public static void setSecretKey(SecretKey key) {
		secretKey = key;
	}
	
	public static SecretKey getSecretKey() {
		return secretKey;
	}
	
	public static SecretKey decodeKay(byte[] encodedKey) {
		return new SecretKeySpec(encodedKey, "DES");

	}
	


}
