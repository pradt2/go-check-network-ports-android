package com.pr.checknetworkportsandroid;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String logString = "";

    private PortRangeCheckAsyncTask.OnUpdateListener onUpdateListener = new PortRangeCheckAsyncTask.OnUpdateListener() {
        @Override
        public void onCheckUpdate(int port, String protocol, boolean isOk) {
            String log = "(" + protocol + ") " + port + " " + (isOk ? "PASSED" : "FAILED");
            MainActivity self = MainActivity.this;
            self.log(log);
        }
    };

    private PortRangeCheckAsyncTask.OnFinishedListener onFinishedListener = new PortRangeCheckAsyncTask.OnFinishedListener() {
        @Override
        public void onCheckFinished(List<Integer> failedTcpPorts, List<Integer> failedUdpPorts) {
            MainActivity self = MainActivity.this;
            self.log("---");
            self.log("");
            self.log("");
            self.log("Blocked TCP ports:");
            self.log(Arrays.toString(failedTcpPorts.toArray()));
            self.log("");
            self.log("Blocked UDP ports");
            self.log(Arrays.toString(failedUdpPorts.toArray()));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void runOnClick(View v) {
        this.clearLog();
        String hostname = this.getHostname();
        int startPort = this.getStartPort();
        int endPort = this.getEndPort();
        int timeout = this.getTimeout();
        PortCheckHandler portCheckHandler = new PortCheckHandler(hostname, timeout);
        PortRangeCheckAsyncTask task = new PortRangeCheckAsyncTask(portCheckHandler, startPort, endPort, onUpdateListener, onFinishedListener);
        task.execute();
    }

    private String getHostname() {
        String hostname = this.getEditTextValue(R.id.hostnameEditText);
        return hostname;
    }

    private int getStartPort() {
        String portString = this.getEditTextValue(R.id.startPortExitText);
        int port = Integer.parseInt(portString);
        return port;
    }

    private int getEndPort() {
        String portString = this.getEditTextValue(R.id.endPortEditText);
        int port = Integer.parseInt(portString);
        return port;
    }

    private int getTimeout() {
        String timeoutString = this.getEditTextValue(R.id.timeoutEditText);
        int timeout = Integer.parseInt(timeoutString);
        return timeout;
    }

    private String getEditTextValue(int id) {
        EditText editText = this.findViewById(id);
        String value = editText.getText().toString();
        value = value.trim().toLowerCase();
        return value;
    }

    private void log(String line) {
        this.logString = line + "\n" + this.logString;
        this.refreshLog();
    }

    private void clearLog() {
        this.logString = "";
        this.refreshLog();
    }

    private void refreshLog() {
        TextView output = this.findViewById(R.id.outputTextView);
        output.setText(this.logString);
    }
}
