package com.quardroiders;

import java.io.BufferedReader; 
import java.io.IOException; 
import java.io.InputStreamReader; 
import java.net.MalformedURLException; 
import java.net.URL; 
import java.net.URLConnection; 

public class Madd { 
	Madd(){
	
}

public String geocodeAddr(String latitude, String longitude) { 
	String addr = ""; 

	String url = String.format( 
	"http://maps.google.com/maps/geo?output=csv&key=abcdef&q=%s,%s", 
	latitude, longitude); 
	URL myURL = null; 
	URLConnection httpsConn = null; 
	try { 
		myURL = new URL(url); 
	} catch (MalformedURLException e) { 
		e.printStackTrace(); 
		return null; 
	} 
	try { 
		httpsConn = (URLConnection) myURL.openConnection(); 
		if (httpsConn != null) { 
			InputStreamReader insr = new InputStreamReader(httpsConn 
					.getInputStream(), "UTF-8"); 
			BufferedReader br = new BufferedReader(insr); 
			String data = null; 
			if ((data = br.readLine()) != null) { 
				//System.out.println(data); 
				String[] retList = data.split(","); 
				if (retList.length > 2 && ("200".equals(retList[0]))) { 
					for (int i = 2; i < retList.length; i++)
						addr = addr + retList[i];
						addr = addr.replace("\"", ""); 
				} else { 
					addr = ""; 
				} 
			} 
			insr.close(); 
		} 
	} catch (IOException e) { 
		e.printStackTrace(); 
		return null; 
	} 
	return addr; 
} 
	}