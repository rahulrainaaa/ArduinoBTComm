package com.example.osxuser.arduinobtcomm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class CommandActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editText = null;
    private BluetoothAdapter adapter = null;
    private BluetoothSocket socket = null;
    private BluetoothDevice device = null;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);
        editText = (EditText) findViewById(R.id.edit_text);
        findViewById(R.id.button).setOnClickListener(this);
        new MyConnection().execute("");

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (socket != null) //If the btSocket is busy
        {
            try {
                socket.close(); //close connection
            } catch (IOException e) {
                Log.d("Error", "" + e.getMessage());
            }
        }
        finish();
    }

    @Override
    public void onClick(View v) {

        String commandStr = editText.getText().toString();
        try {
            byte[] bytes = new byte[4096];
            socket.getOutputStream().write(commandStr.trim().getBytes());
           // socket.getInputStream().read(bytes);
           // String responseData = new String(bytes, "US-ASCII");
           // Toast.makeText(this, "Arduino: " + responseData, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class MyConnection extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            adapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
            Iterator<BluetoothDevice> iterator = pairedDevices.iterator();
            while (iterator.hasNext()) {
                device = iterator.next();
                break;
            }

            String ack = "Found Devices Paired.";
            if (device != null) {
                try {
                    socket = device.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    ack = "Connected to BT: " + device.getName();
                } catch (IOException e) {
                    e.printStackTrace();
                    ack = "EXCEPTION: " + e.getMessage();
                }
            }
            return ack;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(CommandActivity.this, "" + s.trim(), Toast.LENGTH_SHORT).show();

        }
    }

}
