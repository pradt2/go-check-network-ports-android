package com.pr.checknetworkportsandroid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Arrays;

public class PortCheckHandler {

	private static final byte[] PING = "ping".getBytes(Charset.forName("ASCII"));
	private static final byte[] PONG = "pong".getBytes(Charset.forName("ASCII"));

	private String host;
	private int timeoutMs;

	public PortCheckHandler(String host, int timeoutMs) {
		this.host = host;
		this.timeoutMs = timeoutMs;
	}

	public void checkTcp(int port) throws IOException {
		Socket socket = new Socket();
		socket.connect(new InetSocketAddress(InetAddress.getByName(this.host), port), this.timeoutMs);
		socket.setSoTimeout(this.timeoutMs);
		OutputStream os = socket.getOutputStream();
		os.write(PING);
		os.flush();
		InputStream is = socket.getInputStream();
		byte[] buf = new byte[PONG.length];
		is.read(buf);
		if (!(new String(buf).equalsIgnoreCase(new String(PONG)))) {
			throw new IOException("Unexpected TCP response");
		}
		is.close();
		os.close();
		socket.close();
	}

	public boolean safeCheckTcp(int port) {
		try {
			this.checkTcp(port);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void checkUdp(int port) throws IOException {
		InetAddress address = InetAddress.getByName(this.host);
		DatagramSocket socket = new DatagramSocket();
		socket.setSoTimeout(this.timeoutMs);
		DatagramPacket packet = new DatagramPacket(Arrays.copyOf(PING, PING.length), PING.length, address, port);
		socket.send(packet);
		socket.receive(packet);
		byte[] pong = packet.getData();
		if (!(new String(pong).equalsIgnoreCase(new String(PONG)))) {
			throw new IOException("Unexpected UDP response");
		}
		socket.close();
	}

	public boolean safeCheckUdp(int port) {
		try {
			this.checkUdp(port);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
