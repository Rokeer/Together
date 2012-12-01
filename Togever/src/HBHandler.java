import java.io.BufferedReader;
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

public class HBHandler implements Runnable {
	private Socket socket;
	private String mName;
	private BufferedReader mBufferedReader;
	private PrintWriter mPrintWriter;
	private Hashtable<String, ThreadServer> mClientList;
	private DateFormat fmt = new SimpleDateFormat("hh-mm");
	private Date date = new Date();
	private DataAccess mData = new DataAccess();
	private ExecutorService exec;

	public HBHandler(Socket socket, String mName, BufferedReader br,
			PrintWriter pw, Hashtable<String, ThreadServer> mClientList,
			ExecutorService exec) {
		this.socket = socket;
		this.mName = mName;
		this.mBufferedReader = br;
		this.mPrintWriter = pw;
		this.mClientList = mClientList;
		this.exec = exec;
	}

	public void run() {
		try {
			while (true) {
				date = new Date();
				String[] lastdate = new String[5];
				String[] nowdate = new String[5];
				nowdate = fmt.format(date).split("-");
				lastdate = mData.getHB(mName).split("-");
				int tmpdate = Integer.parseInt(nowdate[1])
						- Integer.parseInt(lastdate[1]);
				if (lastdate[0].equals(nowdate[0])) {
					if (tmpdate > 10) {
						mClientList.remove(mName);
						mBufferedReader.close();
						mPrintWriter.close();
						socket.close();
						exec.shutdownNow();
						break;
					}
				} else {
					if (tmpdate + 60 > 10) {
						mClientList.remove(mName);
						mBufferedReader.close();
						mPrintWriter.close();
						socket.close();
						exec.shutdownNow();
						break;
					}
				}
				Thread.sleep(5400000);
			}
			System.out.print("No Heartbeat");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}