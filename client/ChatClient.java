// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  String _loginID = "";

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
	if(message.length() != 0) {
		try
	    {
			if(message.charAt(0) == '#') {
				if(message.equals("#quit")) {
					quit();
				} else if (message.equals("#logoff")) {
					logOff();
				} else if(message.equals("#login")) {
					if(isConnected()) {
						clientUI.display("You cannot login while still being connected to a server.");
					} else {
						openConnection();
					}
				} else if(message.startsWith("#sethost")){
					if(isConnected()) {
						clientUI.display("You cannot change host while still being connected to a server.");
					} else {
						clientUI.display("Host set to: "+message.substring(9).trim());
						setHost(message.substring(9).trim());
					}
				} else if(message.startsWith("#setport")) {
					if(isConnected()) {
						clientUI.display("You cannot change port while still being connected to a server.");
					} else {
						try {
							clientUI.display("Port set to: "+message.substring(9).trim());
							setPort(Integer.parseInt(message.substring(9).trim()));
						} catch (Exception e) {
							clientUI.display("Please enter a valid port number.");
						}
					}
				} else if(message.equals("#gethost")) {
					clientUI.display(getHost());
				} else if(message.equals("#getport")) {
					clientUI.display(String.valueOf(getPort()));
				} else {
					clientUI.display("Please enter a valid command.\n"
							+ "#quit #logoff #login #gethost #getport \n"
							+ "#sethost <host> #setport <port>");
				}
			} else {
				sendToServer(message);
			}
	    }
	    catch(IOException e)
	    {
	      clientUI.display
	        ("Could not send message to server.  Terminating client.");
	      quit();
	    }
	}
	
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      sendToServer("#disconnecting");
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  @Override
  protected void connectionClosed() {
	  clientUI.display("Connection closed.");
  }
  
  @Override
  protected void connectionException(Exception exception) {
	  clientUI.display("SERVER SHUTTING DOWN! DISCONNECTING!");
	  clientUI.display("Abnormal termination of connection.");
	  quit();
  }
  
  @Override
  protected void connectionEstablished() {
	  try {
		  sendToServer("#login "+_loginID);
	  } catch (IOException e) {}
  }
  
  private void logOff() {
	  try
	    {
		  sendToServer("#disconnecting");
	      closeConnection();
	    }
	    catch(IOException e) {}
  }
  
  public String getLoginID() {
	  return _loginID;
  }
  
  public void setLoginID(String loginID) {
	  _loginID = loginID;
  }

  
}
//End of ChatClient class
