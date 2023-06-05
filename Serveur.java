//package projet30;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import java.net.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Serveur implements Runnable{
	private static DatagramSocket socket;
	private static String rec;
	private static byte[] buf=new byte[256];
	private static ArrayList Cust_list;
	private static Thread t1,t2,t3;

	public Serveur() throws SocketException {
		socket=new DatagramSocket(1026);
		rec="";
		Cust_list=new ArrayList<>();
		t1=new Thread(this);
		t2=new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stu
				try {
					Dhcp();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		t3=new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Serveur d=new Serveur();
		while(true) {
		t1.start();
		ServerSocket Serv=new ServerSocket(5000);
		Socket s=Serv.accept();
		t2.start();
		
		}
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		DatagramPacket packet=new DatagramPacket(buf,buf.length);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Reception du message de diffusion envoy� par des users
		
		InetAddress address=packet.getAddress();
		int port=packet.getPort();
		String received=new String(packet.getData(),0,packet.getLength());
		System.out.println("recu "+received);
		String ip="";
		try {
			ip = InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] ipaddr=ip.split("/");
		buf=ipaddr[1].getBytes();
		packet=new DatagramPacket(buf,buf.length,address,port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//La fonction qui va determiner les @ip � assigner
	public void Dhcp() throws UnknownHostException {
		String inet="";
		inet=InetAddress.getLocalHost().toString();
		String[] attr=inet.split("/");
		System.out.println(attr[1]);
		String[] p=attr[1].split("[.]",4);
		System.out.println("octet "+p[3]);
		rec=p[0]+"."+p[1]+"."+p[2];
		//System.out.println(rec);
		while(true) {
			int suite = new Random().nextInt();
			if(suite!=0) {
				String s="."+suite;
				rec+=s;
				System.out.println(rec);
			}
			
		String cmd=rec;
		int i=0;
				try {
			Process run=Runtime.getRuntime().exec("ping -n 1 "+cmd);
			BufferedReader reader = new BufferedReader(new InputStreamReader(run.getInputStream()));
			String[] result=new String[4];
			while((cmd=reader.readLine())!=null && i<4) {
				result[i]=cmd;
				i++;
			}
			if(result[2].contains("Impossible")) {
				System.out.println("attribuable");
			}
			else {
				System.out.println("nion");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				rec=p[0]+"."+p[1]+"."+p[2];
		}
		//p.command("bash","mkdir g");
				}

}

