package com.pr.checknetworkportsandroid;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

public class PortRangeCheckAsyncTask extends AsyncTask<Void, PortRangeCheckAsyncTask.PortCheck, Void> {

	private PortCheckHandler client;
	private int startPort;
	private int endPort;

	private List<Integer> failedTcpPorts = new ArrayList<>();
	private List<Integer> failedUdpPorts = new ArrayList<>();

	private OnUpdateListener onUpdateListener;
	private OnFinishedListener onFinishedListener;

	public PortRangeCheckAsyncTask(PortCheckHandler client, int startPort, int endPort, OnUpdateListener onUpdateListener, OnFinishedListener onFinishedListener) {
		this.client = client;
		this.startPort = startPort;
		this.endPort = endPort;
		this.onUpdateListener = onUpdateListener;
		this.onFinishedListener = onFinishedListener;
	}

	@Override
	protected Void doInBackground(Void... voids) {
		for (int port = this.startPort; port < this.endPort + 1; port++) {
			boolean isOk = this.client.safeCheckTcp(port);
			if (!isOk) {
				this.failedTcpPorts.add(port);
			}
			this.publishProgress(new PortCheck(port, "TCP", isOk));
			isOk = this.client.safeCheckUdp(port);
			if (!isOk) {
				this.failedUdpPorts.add(port);
			}
			this.publishProgress(new PortCheck(port, "UDP", isOk));
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(PortCheck... values) {
		if (this.onUpdateListener == null) return;
		PortCheck val = values[0];
		this.onUpdateListener.onCheckUpdate(val.getPort(), val.getProtocol(), val.isOk());
	}

	@Override
	protected void onPostExecute(Void aVoid) {
		if (this.onFinishedListener == null) return;
		this.onFinishedListener.onCheckFinished(this.failedTcpPorts, this.failedUdpPorts);
	}

	public interface OnUpdateListener {
		void onCheckUpdate(int port, String protocol, boolean isOk);
	}

	public interface OnFinishedListener {
		void onCheckFinished(List<Integer> failedTcpPorts, List<Integer> failedUdpPorts);
	}

	protected static class PortCheck {
		private int port;
		private String protocol;
		private boolean isOk;

		public PortCheck(int port, String protocol, boolean isOk) {
			this.port = port;
			this.protocol = protocol;
			this.isOk = isOk;
		}

		public int getPort() {
			return port;
		}

		public String getProtocol() {
			return protocol;
		}

		public boolean isOk() {
			return isOk;
		}
	}

}
