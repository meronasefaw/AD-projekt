package com.myapp.bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class bluetoothConnection {
    private static final String TAG = "BluetoothConnection";

    private static String appName = "MYBLUETOOTH";

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8c93745c-e672-4c6f-9b1d-973e63a9eba8");

    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;

    private AcceptThread mInsecureAcceptThread;

    private ConnectThread mConnectThread;
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;
    ProgressDialog mProgressDialog;

    private ConnectedThread mConnectedThread;

    public bluetoothConnection(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        start();
    }

    public class AcceptThread extends Thread{
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(){
            BluetoothServerSocket tmp = null;

            try {
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);
                Log.d(TAG,"AcceptThread: setting up Server using: " + MY_UUID_INSECURE);

            }catch(IOException e){
                //Log.e(TAG, "AcceptThread: IOException" + e.getMessage());
            }
            mmServerSocket = tmp;
        }

        public void run(){
            Log.d(TAG, "run: AcceptThread Running.");

            BluetoothSocket socket = null;

            try {
                Log.d(TAG, "run: RFCOM server socket start.....");

                socket = mmServerSocket.accept();
                Log.d(TAG, "run: RFCOM server socket accepted connection");
            } catch (IOException e){
                Log.e(TAG, "AcceptThread: IOException" + e.getMessage());
            }

            if (socket != null){
                connected(socket,mmDevice);
            }
            Log.i(TAG, "END mAcceptThread");
        }
        public void cancel(){
            Log.d(TAG, "cancel: Canceling AcceptTHread.");
            try {
                mmServerSocket.close();
            } catch (IOException e){
                Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed." + e.getMessage());
            }
        }
    }

    private class ConnectThread extends Thread{
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid){
            Log.d(TAG, "ConnectThread started. ");
            mmDevice = device;
            deviceUUID = uuid;
        }

        public void run(){
            BluetoothSocket tmp = null;
            Log.i(TAG, "RUN mConnectThread");

            try {
                Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using:");
                tmp = mmDevice.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: could not create InsecureRfcommSocket using:");
            }
            mmSocket = tmp;
            mBluetoothAdapter.cancelDiscovery();

            try {
                mmSocket.connect();
                Log.d(TAG, "run: ConnectThread connected.");

            } catch (IOException e) {
                try {
                    mmSocket.close();
                } catch (IOException e1){
                    Log.e(TAG, "mConnectThread: run: unable to close connection " + e1.getMessage());
                }
                Log.d(TAG, "run. connectThread: could not connect to UUID: " + MY_UUID_INSECURE);
            }
            connected(mmSocket,mmDevice);
        }
        public  void cancel(){
            try {
                Log.d(TAG, "cancel; Closing Client Socket");
                mmSocket.close();
            } catch (IOException e){
                Log.e(TAG, "cancel: close() og mmSocket in ConnectThread failed. " + e.getMessage());
            }
        }
    }


    public synchronized void start(){
        Log.d(TAG, "start");

        if(mConnectThread!= null){
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if(mInsecureAcceptThread == null){
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }
    }

    public void startClient(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startClient: Start.");

        mProgressDialog = ProgressDialog.show(mContext, "Connecting Bluetooth"
        ,"please wait...", true);

        mConnectThread = new ConnectThread(device, uuid);
        mConnectThread.start();
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket){
            Log.d(TAG, "ConnectedThread: Starting.");

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            mProgressDialog.dismiss();

            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e){
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;

            while(true){
                try {
                    bytes = mmInStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                } catch (IOException e){
                    Log.e(TAG, "write: Error reading inputStream. " + e.getMessage());
                    break;
                }
            }
        }

        public void write(byte[] bytes){
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: writing to outStream: " + text);

            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "write: Error writing to outStream. " + e.getMessage());
            }
        }

        private void cancel(){
            try {
                mmSocket.close();
            } catch (IOException e){

            }
        }
    }

    private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
        Log.d(TAG, "connected: String.");

        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }

    public void write(byte[] out) {
        ConnectThread r;

        Log.d(TAG, "write: write Called.");
        mConnectedThread.write(out);
    }
}
