import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadServer implements Runnable {
	private Socket mSocket;
	private BufferedReader mBufferedReader;
	private PrintWriter mPrintWriter;
	private String mStrMSG;
	private Hashtable<String, ThreadServer> mClientList;
	private Enumeration<ThreadServer> e;
	private DataAccess dataaccess;
	private String ulat = "0";
	private String ulng = "0";
	private String mName = "0";
	private String mPwd = "0";
	private String mToName = "0";
	private ResultSet resultSet = null;
	private ExecutorService exec = Executors.newCachedThreadPool();

	public ThreadServer(Socket socket,
			Hashtable<String, ThreadServer> mClientList) throws IOException {
		this.mSocket = socket;
		this.mClientList = mClientList;
		mBufferedReader = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		/*
		 * mStrMSG = "user:" + this.mSocket.getInetAddress() + " come total:" +
		 * mClientList.size(); sendMessage();
		 */
		dataaccess = new DataAccess();
	}

	public void run() {
		try {
			while (((mStrMSG = mBufferedReader.readLine()) != null)) {
				if (mStrMSG.trim().equals("/exit")) {
					// when a client exit
					mClientList.remove(mName);
					mBufferedReader.close();
					mPrintWriter.close();
					mStrMSG = "user:" + mName + " exit total:"
							+ mClientList.size();
					mSocket.close();
					System.out.println(mStrMSG);
					break;
				} else if (mStrMSG.trim().equals("/heartbeat")) {
					// heart beat package
					saveHB(mName);

				} else if (mStrMSG.trim().startsWith("/signup ")) {
					// when get a user name
					mStrMSG = mStrMSG.trim().substring(8);
					if (dataaccess.signup(mStrMSG)) {
						mPrintWriter = new PrintWriter(
								mSocket.getOutputStream(), true);
						mPrintWriter.println("/success Sign Up Successful");
					} else {
						mPrintWriter = new PrintWriter(
								mSocket.getOutputStream(), true);
						mPrintWriter
								.println("/error Username already exists.");
					}

				} else if (mStrMSG.trim().startsWith("/username ")) {
					// when get a user name
					mName = mStrMSG.trim().substring(10);

				} else if (mStrMSG.trim().startsWith("/pwd ")) {
					// when get a password
					mPwd = mStrMSG.trim().substring(5);
					if (dataaccess.checkIdentity(mName, mPwd)) {
						mPrintWriter = new PrintWriter(
								mSocket.getOutputStream(), true);
						mPrintWriter.println("/success Login Successful");
						addName(mName);
						saveHB(mName);
						exec.execute(new HBHandler(mSocket, mName,
								mBufferedReader, mPrintWriter, mClientList,
								exec));
					} else {
						mPrintWriter = new PrintWriter(
								mSocket.getOutputStream(), true);
						mPrintWriter
								.println("/error The user does not exist or password is incorrect.");
					}

				} else if (mStrMSG.trim().equals("/getfriend")) {
					// when user want to get their friend list
					resultSet = dataaccess.getList("friendlist", mName);

					while (resultSet.next()) {
						// Friend is online
						if (mClientList.containsKey(resultSet
								.getString("tname"))) {
							mPrintWriter = new PrintWriter(
									mSocket.getOutputStream(), true);
							mPrintWriter.println("/friend ,"
									+ resultSet.getString("tname")
									+ ",0,"
									+ dataaccess.getLocation(resultSet
											.getString("tname")));
						} else {
							// Friend is offline
							mPrintWriter = new PrintWriter(
									mSocket.getOutputStream(), true);
							mPrintWriter.println("/friend ,"
									+ resultSet.getString("tname") + ",1");
						}

					}
					dataaccess.close();
					mPrintWriter = new PrintWriter(mSocket.getOutputStream(),
							true);
					mPrintWriter.println("/finish");

				} else if (mStrMSG.trim().startsWith("/addfriend ")) {
					// when user want to add a friend
					if (dataaccess.addList("friendlist", mName, mStrMSG.trim()
							.substring(11))) {
						mPrintWriter = new PrintWriter(
								mSocket.getOutputStream(), true);
						mPrintWriter.println("/error "
								+ mStrMSG.trim().substring(11)
								+ " has been added to your friends list");
					} else {
						mPrintWriter = new PrintWriter(
								mSocket.getOutputStream(), true);
						mPrintWriter.println("/error "
								+ mStrMSG.trim().substring(11)
								+ " had already in your friends list");
					}

				} else if (mStrMSG.trim().startsWith("/delfriend ")) {
					// when user want to delete a friend
					dataaccess.delList("friendlist", mName, mStrMSG.trim()
							.substring(11));
					mPrintWriter = new PrintWriter(mSocket.getOutputStream(),
							true);
					mPrintWriter.println("/error "
							+ mStrMSG.trim().substring(11)
							+ " has been deleted from your friends list");

				} else if (mStrMSG.trim().equals("/getblock")) {
					// when user want to get their block list
					resultSet = dataaccess.getList("blocklist", mName);

					while (resultSet.next()) {
						mPrintWriter = new PrintWriter(
								mSocket.getOutputStream(), true);
						mPrintWriter.println("/block "
								+ resultSet.getString("tname"));
					}
					dataaccess.close();

				} else if (mStrMSG.trim().startsWith("/addblock ")) {
					// when user want to add a block
					dataaccess.addList("blocklist", mName, mStrMSG.trim()
							.substring(10));
					mPrintWriter = new PrintWriter(mSocket.getOutputStream(),
							true);
					mPrintWriter.println("/error "
							+ mStrMSG.trim().substring(10)
							+ " has been added to your block list");

				} else if (mStrMSG.trim().startsWith("/delblock ")) {
					// when user want to delete a block
					dataaccess.delList("blocklist", mName, mStrMSG.trim()
							.substring(10));
					mPrintWriter = new PrintWriter(mSocket.getOutputStream(),
							true);
					mPrintWriter.println("/error "
							+ mStrMSG.trim().substring(10)
							+ " has been deleted from your block list");

				} else if (mStrMSG.trim().startsWith("/chatwith ")) {
					// when user want to chat with a people
					mToName = mStrMSG.trim().substring(10);

				} else if (mStrMSG.trim().startsWith("/tosomeone ")) {
					// when user want to chat with a people

					mStrMSG = "/p " + mName + ":"
							+ mStrMSG.trim().substring(11);
					sendToOne(mToName);

				} else if (mStrMSG.trim().startsWith("/all ")) {
					// when user want to chat in area
					mStrMSG = mStrMSG.trim().substring(5);
					mStrMSG = "/all " + mName + ":" + mStrMSG;
					chatInArea();

				} else if (mStrMSG.trim().startsWith("/getlocation ")) {
					// when user want to know someone's location
					String someonesloc = "0";
					String[] loc = new String[5];
					someonesloc = mStrMSG.trim().substring(13);
					if (mClientList.containsKey(someonesloc)) {
						someonesloc = dataaccess.getLocation(someonesloc);
						loc = someonesloc.split(",");
						// Send latitude info
						mPrintWriter = new PrintWriter(
								mSocket.getOutputStream(), true);
						mPrintWriter.println("/latitude " + loc[0]);
						// Send longitude info
						mPrintWriter = new PrintWriter(
								mSocket.getOutputStream(), true);
						mPrintWriter.println("/longitude " + loc[1]);
						// set flag to true
						mPrintWriter = new PrintWriter(
								mSocket.getOutputStream(), true);
						mPrintWriter.println("/finish");
					} else {
						mPrintWriter = new PrintWriter(
								mSocket.getOutputStream(), true);
						mPrintWriter.println("/error User cannot be founded.");
					}

				} else if (mStrMSG.trim().startsWith("/latitude ")) {
					// when get a latitude information
					ulat = mStrMSG.trim().substring(10);
					dataaccess.saveLocation(mName, ulat, ulng);
				} else if (mStrMSG.trim().startsWith("/longitude ")) {
					// when get a longitude information
					ulng = mStrMSG.trim().substring(11);
					dataaccess.saveLocation(mName, ulat, ulng);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// return Socket
	public Socket getSocket() {
		return mSocket;
	}

	public String getName() {
		return mName;
	}

	public void saveHB(String uname) throws Exception {
		String lastmsg;
		DateFormat fmt = new SimpleDateFormat("hh-mm");
		Date date = new Date();
		lastmsg = fmt.format(date);
		dataaccess.saveHB(uname, lastmsg);
	}

	public void addName(String uname) {
		if (mClientList.containsKey(uname)) {
			try {
				e=mClientList.elements();
				while (e.hasMoreElements()) {
					ThreadServer th = (ThreadServer) e.nextElement();
					if (th.getName().equals(mName)){
						th.mPrintWriter = new PrintWriter(th.getSocket().getOutputStream(),
								true);
						th.mPrintWriter.println("/quit Same account login in other place.");
					}
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mClientList.remove(uname);
		}
		mClientList.put(uname, this);
	}

	// send message to all clients
	@SuppressWarnings("unused")
	private void sendMessage() throws IOException {
		System.out.println(mStrMSG);

		e = mClientList.elements();
		while (e.hasMoreElements()) {
			ThreadServer th = (ThreadServer) e.nextElement();
			th.mPrintWriter = new PrintWriter(th.getSocket().getOutputStream(),
					true);
			th.mPrintWriter.println(mStrMSG);
		}
	}

	private void sendToOne(String toname) {
		try {
			if (mClientList.containsKey(toname)
					&& !dataaccess.wasBlocked(mName, toname)) {

				/*
				 * mPrintWriter = new PrintWriter(mSocket.getOutputStream(),
				 * true); String tmp = mName + " To "+mToName+": "+mStrMSG;
				 * mPrintWriter.println(tmp);
				 */
				// mStrMSG = mName + "To You: " + mStrMSG;
				ThreadServer th = mClientList.get(toname);
				th.mPrintWriter = new PrintWriter(th.getSocket()
						.getOutputStream(), true);
				th.mPrintWriter.println(mStrMSG);
				mPrintWriter = new PrintWriter(mSocket.getOutputStream(), true);
				mPrintWriter.println(mStrMSG);
			} else {
				mPrintWriter = new PrintWriter(mSocket.getOutputStream(), true);
				mPrintWriter
						.println("/error User is offline or cannot be found.");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void chatInArea() {
		System.out.println(mStrMSG);

		e = mClientList.elements();
		while (e.hasMoreElements()) {
			ThreadServer th = (ThreadServer) e.nextElement();

			try {
				if (dataaccess.isInArea(mName, th.getName())
						&& !dataaccess.wasBlocked(mName, th.getName())) {
					th.mPrintWriter = new PrintWriter(th.getSocket()
							.getOutputStream(), true);
					th.mPrintWriter.println(mStrMSG);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}