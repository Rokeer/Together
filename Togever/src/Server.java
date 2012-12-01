import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	// server port
	private int SERVERPORT = 54321;
	// clients connection
	private Hashtable<String, ThreadServer> mClientList = new Hashtable<String, ThreadServer>();
	// thread pool
	private ExecutorService mExecutorService;
	// ServerSocket object
	private ServerSocket mServerSocket;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Server();
	}

	public Server() {
		try {
			// set server port
			mServerSocket = new ServerSocket(SERVERPORT);
			// create a thread pool
			mExecutorService = Executors.newCachedThreadPool();
			System.out.println("start...");
			// save socket object from client connections
			Socket client = null;
			while (true) {
				// get clients' connections and add them to a list
				System.out.println("start accept");
				client = mServerSocket.accept();
				System.out.println("new people");
				// open a client thread
				mExecutorService.execute(new ThreadServer(client, mClientList));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}