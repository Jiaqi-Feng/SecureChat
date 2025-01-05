Overview:

This is a secure chat application developed as a university assignment. The application allows users to join a multiplayer chat room using a chosen username (no sign-up required). 
It provides options for plain text messaging as well as encrypted communication using Caesar cipher and DES.


Feature:

  •	User Authentication: No sign-up required; users can log in with a chosen username.
  
  •	Plain Text Messaging: Users can send and receive messages in plain text.
  
  •	Encryption Options:
  
    o	Caesar Cipher: Uses a predefined key for encryption and decryption.
    
    o	DES (Data Encryption Standard): Users can save and load DES keys for encryption and decryption.
    
  •	Chat Room: Real-time multiplayer communication.

  
  
Technology Stack:

  •	Front-End: JavaFX (Graphical User Interface).
  
  •	Back-End: Java (Business Logic).
  
  •	Build Tool: Maven.
  
  •	Server Hosting: Previously tested on AWS EC2 but now runs locally.
  
  •	Development Environment: Tested in Eclipse.

  
  
Setup Instructions:

1.	Prerequisites:
  • Install the latest version of Java Development Kit (JDK).
  •	Install Eclipse IDE.
  •	Ensure Maven is properly configured in your development environment.
3.	Clone the Repository:
  git clone <repository-URL>
4.	Open the Project:
  •	Open the project in Eclipse.
5.	Run the Application Locally:
  •	Launch the server component locally on your machine.
  •	Launch the client application from Eclipse.
  •	Enter a username and connect to the chat room.
6.	Running on AWS EC2 (Optional):
  •	The application was previously tested to run on AWS EC2.
  •	To run it on AWS EC2 again, you will need to update the code configuration for server deployment and networking.



Usage:

  •	Enter a username to join the chat room.
  
  •	Send plain text messages or select an encryption method (Caesar Cipher or DES) for secure communication.
  
  •	Save and load DES keys as needed for encrypted messaging.

  

Future Improvements:

  •	Add user authentication with a sign-up and password-based login system.
  
  •	Implement additional encryption algorithms.
  
  •	Enhance the user interface for better usability.
  
  •	Improve server deployment configuration for seamless cloud hosting.
  

  
License:

This project is a university assignment and is not intended for commercial use. Copyright © Jiaqi Feng. All rights reserved.
