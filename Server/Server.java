import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Server{

	private String CONNECTION_LOST = "connect-lost";

	private ServerSocket serverSocket;

	private int maxClients = 100;
	private Socket clients[] = new Socket[maxClients];
	private int numClients = 0;

	private BufferedReader in[] = new BufferedReader[maxClients];
	private PrintWriter out[] = new PrintWriter[maxClients];

	public static void main(String[] args){
		Server server = new Server();
		server.start(12345);
	}

	public void start(int port){
		Runtime.getRuntime().addShutdownHook(new Thread(new shutdownThread()));
		while(true){
			try{
				serverSocket = new ServerSocket(port);
				break;
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		new Thread(new acceptClientsThread()).start();
	}

	public class acceptClientsThread implements Runnable{

		@Override
		public void run(){
			while(true){
				try{
					System.out.println("Listening for new clients...");
					clients[numClients] = serverSocket.accept();
					System.out.println("Client at" + clients[numClients].getRemoteSocketAddress() + "connected.");
					numClients++;
					new Thread(new clientThread(numClients - 1)).start();
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	public class clientThread implements Runnable{

		private int me;
		
		public clientThread(int num){
			this.me = num;
		}
		

		@Override
		public void run(){
			try{
				in[me] = new BufferedReader(new InputStreamReader(clients[me].getInputStream()));
				out[me] = new PrintWriter(clients[me].getOutputStream(), true);
				
				String input;
				System.out.println("Client " + me + " entering while");
				while(true)
				{
					try{
						input = in[me].readLine();
						if (input == null || input.length() == 0){
							System.out.println("Client " + me + "closed");
							clients[me].close();
							return;
						}
						System.out.println("Client " + me + " says: " + input);
					} catch(Exception e){
						clients[me].close();
						return;
					}
					for (int client = 0; client < numClients; client++){
						if (client == me){
							continue;
						} else{
							try{
								out[client].println(me + ": " + input);
							} catch(Exception e){
								System.out.println("Client " + client + " no longer connected.");
							}
						}
					}
				}				
			} catch(Exception e){
				e.printStackTrace();
			}

		}
	}

	public class shutdownThread implements Runnable{

		@Override
		public void run(){
			for (int client = 0; client < numClients; client++){
				try{
					out[client].println(CONNECTION_LOST);
					clients[client].close();
					System.out.println("Client " + client + " closed.");
				} catch(Exception e){
					System.out.println("Client " + client + " no longer connectioned.");
				}
			}
			while(true){
				try{
					serverSocket.close();
					System.out.println("Server closed.");
					break;
				} catch(Exception e){
					System.out.println("Server socket close error.");
				}
			}
		}
	};
}