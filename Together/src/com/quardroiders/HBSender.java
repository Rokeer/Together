package com.quardroiders;


import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;

public class HBSender implements Runnable {
	private Socket socket;
	private BufferedReader mBufferedReader;
	private PrintWriter mPrintWriter;

	public HBSender(Socket socket, BufferedReader br, PrintWriter pw) {
		this.socket = socket;
		this.mBufferedReader = br;
		this.mPrintWriter = pw;
	}

	public void run() {
		try {
			while (true) {
				Thread.sleep(2500000);
				mPrintWriter.print("/heartbeat\n");
				mPrintWriter.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}