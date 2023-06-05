//package tt.reseaux;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	private static DatagramSocket socket=null;
	private static String passerelle;
	private static String broad;
	static String received=null;
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		// TODO Auto-generated method stub
		System.out.println("-------Welcome to Awe IP Attributor!--------");
		System.out.println("Type the keyword dhcp to get a new IP address");
		Scanner sc = new Scanner(System.in);
		Process rune = Runtime.getRuntime().exec("ip route show");
		rune.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(rune.getInputStream()));
		String line = reader.readLine();
		String[] pass = line.split(" ");
		// trouver @ passerelle
		System.out.println(pass[2]);
		passerelle = pass[2];
		//trouver adresse diffuse
		String[] root = pass[2].split("\\.");
		String broad = root[0]+"."+root[1]+"."+root[2]+"."+"255";
		String request =null;
		System.out.println(broad);
		do {
			System.out.println("test");
			request  = sc.nextLine();
			System.out.println("hey");
			if(request.equals("dhcp")) {
				DatagramPacket dp = broadcast("DHCP",InetAddress.getByName(broad));
			System.out.println("Connecting to the server...");
			Socket sock = new Socket(dp.getAddress(), dp.getPort());
			System.out.println("Connected!");
			System.out.println("Requesting an IP address...");
			DataInputStream dis =  new DataInputStream(sock.getInputStream());
			try {
				received = dis.readUTF();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(received.trim());
			}
			else
			{
				System.out.println("Wrong keyword!! Type dhcp");
			}
		}while(!(request.equals("dhcp")));
		//String add = "192.168.74.246";
		
		// update ip from the server
		String[] cmd = {"pkexec nmcli con mod Infinix ipv4.address "+received+"/24","nmcli con mod Infinix ipv4.gateway "+passerelle,"nmcli con mod Infinix ipv4.dns 8.8.8.8","nmcli con mod Infinix ipv4.method manual","nmcli con up Infinix"};
		System.out.println("Updating the nic settings...");
		String[] msgs = {"setting up the ip address...","setting up the gateway...","setting up the ip dns","disabling the DHCP config...","Almost complete...","ok"};
		for(int i = 0;i<cmd.length;i++) {
		Process run = Runtime.getRuntime().exec(cmd[i]);
		//run.getInputStream().read()
		run.waitFor();
		if(run.exitValue()!= 0) {
			InputStream error  =  run.getErrorStream();
			int c = 0;
			while((c=error.read())!=-1) {
				System.out.print((char)c); //for us the dev
			}
		}
		else {
			int j = 0;
			System.out.println(msgs[j]);
			j++;

		}
		System.out.println("\n");
		}
		//System.out.println("adresse recue "+dis.readUTF()); pas utile
		socket.close();
		//envoyer en diffusion

	}
	
	public static DatagramPacket broadcast(String msg,InetAddress address)throws IOException {
		socket=new DatagramSocket();
		System.out.println("DHCP");
		 socket.setBroadcast(true);
		byte[] buffer=msg.getBytes();
		byte[] buffered = new byte[1024];
		System.out.println("Looking for the server's IP...");
		DatagramPacket packet=new DatagramPacket(buffer,buffer.length,address,5050);
		socket.send(packet);
		DatagramPacket pack =new DatagramPacket(buffered,buffered.length);
		socket.receive(pack);
		return pack;
	}
}