import common.*;
import java.io.*;


public class ServerConsole implements ChatIF {
		
	EchoServer _sv;
	
	public ServerConsole(EchoServer sv) {
		_sv = sv;
	}
	
	public void handleMessageFromServerUI(String message) {
		if(message.length() != 0) {
			try {
				if(message.charAt(0) == '#') {
					if(message.equals("#quit")) {
						_sv.stopListening();
						_sv.close();
						System.exit(0);
					} else if (message.equals("#stop")) {
						_sv.stopListening();
					} else if (message.equals("#close")) {
						_sv.stopListening();
						_sv.close();
					} else if (message.equals("#start")) {
						if(_sv.isListening()) {
							display("You cannot use"
									+ " #start command while the server is still accepting connections.");
						} else {
							_sv.listen();
						}
					} else if (message.equals("#getport")) {
						display(String.valueOf(_sv.getPort()));
					} else if (message.startsWith("#setport")) {
						if(!_sv.isListening() && (_sv.getNumberOfClients() == 0)) {
							try {
								display("Port set to: "+message.substring(9).trim());
								_sv.setPort(Integer.parseInt(message.substring(9).trim()));
							} catch (Exception e) {
								System.out.println("Please enter a valid port number.");
							}
						} else {
							display("The server is not closed");
						}
					} else {
						display("Please enter a valid command.\n"
								+ "#quit #stop #close #start #getport"
								+ " #setport <port>");
					}
				} else {
					message = "SERVER MSG> "+message;
					System.out.println(message);
					_sv.sendToAllClients(message);
				}
			} catch (IOException e) {
				System.out.println("Could not send message to clients.");
			}
		}
	}
	
	@Override
	public void display(String message) {
		System.out.println("> "+message);
	}
}
