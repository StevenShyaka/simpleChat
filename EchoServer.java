// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import ocsf.server.*;
import java.util.Scanner;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  ServerConsole _serverConsole;
  
  Scanner _fromConsole;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
    _fromConsole = new Scanner(System.in);
    _serverConsole = new ServerConsole(this);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	String message = "Message received: "+msg+" from "+client.getInfo("#login");
	_serverConsole.display(message);
	if(String.valueOf(msg).startsWith("#login")) {
		if(client.getInfo("#login") != null) {
			String errorMessage = "There can only be 1 associated loginID per account.";
			try {
				client.sendToClient(errorMessage);
			} catch(IOException e) {}
		} else {
			String[] clientInfo = String.valueOf(msg).split(" ");
			client.setInfo(clientInfo[0], clientInfo[1]);
			_serverConsole.display(client.getInfo("#login")+" has logged on.");
		}
	} else if (String.valueOf(msg).startsWith("#disconnecting")){
		_serverConsole.display(client.getInfo("#login")+" has disconnected.");
		this.sendToAllClients(client.getInfo("#login")+" has disconnected.");
	} else {
		message = String.valueOf(client.getInfo("#login"))+" > " + msg;
	    this.sendToAllClients(message);
	}
  }
  
  public void accept() {
	  try {
		  String message;
		  while(true) {
			  message = _fromConsole.nextLine();
			  _serverConsole.handleMessageFromServerUI(message);
		  }
	  } catch (Exception ex) {
		  System.out.println
		  ("Unexpected error while reading from console!");
	  }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    _serverConsole.display
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
	this.sendToAllClients("WARNING - The server has stopped listening for connections");
    _serverConsole.display
      ("Server has stopped listening for connections.");
  }
  
  @Override
  protected void clientConnected(ConnectionToClient client){
	  _serverConsole.display("A new client is attempting to connect to the server.");
  }
  
  @Override
  synchronized protected void clientDisconnected(
		    ConnectionToClient client) {
	  _serverConsole.display(client.getInfo("#login")+" has disconnected.");
	  this.sendToAllClients(client.getInfo("#login")+" has disconnected.");
	  
  }
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
    sv.accept();
  }
}
//End of EchoServer class
