package com.chat_app;

public class CaesarCipherMethod {
	private static final int shiftKey = 17;
	//CaesarCipher Encrypt
	public static String encrypt(String plaintext) {
		StringBuilder encryptedMsg = new StringBuilder();
		
		for(char c: plaintext.toCharArray()) {
			if(Character.isLetter(c)) {
				char base = Character.isUpperCase(c)? 'A':'a';
				encryptedMsg.append((char)((c - base + shiftKey)%26 + base));
			}else {
				encryptedMsg.append(c);
			}
			
			
		}
		return encryptedMsg.toString();
	}
	
	//CaesarCipher Decrypt
	public static String decrypt(String ciphertext) {
		StringBuilder decryptedMsg = new StringBuilder();
		
		for(char c: ciphertext.toCharArray()) {
			if(Character.isLetter(c)) {
				char base = Character.isUpperCase(c)? 'A':'a';
				decryptedMsg.append(((char)((c - base - shiftKey+26)%26 + base)));
			}else {
				decryptedMsg.append(c);
			}
		}
		
		return decryptedMsg.toString();
	}

}
