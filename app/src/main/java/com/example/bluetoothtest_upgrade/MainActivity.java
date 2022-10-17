package com.example.bluetoothtest_upgrade;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;

    Button btnBluetoothOn , btnBluetoothOff;
    TextView tvBluetoothStatus;
    Button btnConnect;

    Button btnDiscover;
    public ArrayList<BluetoothDevice> mBTdevices;
    public DeviceListAdapter mDeviceListAdapter;
    ListView newDevicesList;
    Button btnFindDiscover;

    //Mode
    final static int BT_REQUEST_ENABLE = 1;
    final static int BT_MESSAGE_READ = 2;
    final static int BT_CONNECTING_STATUS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize
        btnBluetoothOn = findViewById(R.id.btnBluetoothOn);
        btnBluetoothOff = findViewById(R.id.btnBluetoothOff);
        tvBluetoothStatus = findViewById(R.id.tvBluetoothStatus);
        btnConnect = findViewById(R.id.btnConnect);
        btnDiscover = findViewById(R.id.btnDiscover);
        btnFindDiscover = findViewById(R.id.btnFindDiscover);
        newDevicesList = (ListView) findViewById(R.id.newDevicesList);
        mBTdevices = new ArrayList<>();

        //get Defualt of blooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        btnBluetoothOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blueToothOn();
            }
        });

        btnBluetoothOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blueToothOff();
            }
        });
        //10.14 새벽 1:53분 코드 완료 이 이후 작성 시작 해야함.

        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothDiscovery();
            }
        });

        btnFindDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothDiscover();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBTPermissions(){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Mainfest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Mainfest.permission.ACCESS_FINE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            }
            else {
                Log.d("CheckPermission", "No Need to check permissions. SDK version <LOLLIPOP>");
            }
        }
    }

    private final BroadcastReceiver mBroadCastReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Toast.makeText(getApplicationContext(), "Discoverability enabled", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Toast.makeText(getApplicationContext(), "Discoverability Disabled. Able to receive connections", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Toast.makeText(getApplicationContext(), "Discoverability Disabled. Not able to receive connections", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Toast.makeText(getApplicationContext(), "Connecting...", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            else if (action.equals(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED)){
                final int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,BluetoothAdapter.ERROR);

                switch (mode) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Toast.makeText(getApplicationContext(), "Discoverability enabled", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Toast.makeText(getApplicationContext(),"Discoverability Disabled. Able to receive connections", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Toast.makeText(getApplicationContext(),"Discoverability Disabled. Not able to receive connections",Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Toast.makeText(getApplicationContext(),"Connecting...", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Toast.makeText(getApplicationContext(), "Connected.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            else if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                //get devices
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTdevices.add(device);
                Toast.makeText(getApplicationContext(), device.getName() + " : " + device.getAddress(), Toast.LENGTH_SHORT).show();
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTdevices);
                newDevicesList.setAdapter(mDeviceListAdapter);
            }
        }
    };

    //10.15.토요일 오전 12:26분 작성 시작.
    // blueToothON 메소드
    @SuppressLint("MissingPermission")
    public void blueToothOn() {
        if (mBluetoothAdapter == null){
            Toast.makeText(getApplicationContext(),"This device dosen`t support bluetooth service",Toast.LENGTH_SHORT).show();
            tvBluetoothStatus.setText("NonActive");
        }
        else if (mBluetoothAdapter.isEnabled()){
            Toast.makeText(getApplicationContext(), "Already On", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intentBluetoothEnable = new Intent(mBluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intentBluetoothEnable, BT_REQUEST_ENABLE);

            IntentFilter BTIntent = new IntentFilter(mBluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadCastReceiver, BTIntent);
        }
    }
    // blueToothOff 메소드
    @SuppressLint("MissingPermission")
    public void blueToothOff() {
        if (!mBluetoothAdapter.isEnabled()){
            Toast.makeText(getApplicationContext(), "Already OFF",Toast.LENGTH_SHORT).show();
        }
        else if (mBluetoothAdapter.isEnabled());{
            mBluetoothAdapter.disable();
        }
    }
        //bluetoothDiscovery 메소드
    @SuppressLint("MissingPermission")
    public void bluetoothDiscovery() {
        Toast.makeText(getApplicationContext(), "Making device discoverable for 300 seconds.", Toast.LENGTH_SHORT).show();

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        //get scanmode change
        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadCastReceiver, intentFilter);
    }

    // bluetoothDiscover 메소드
    @SuppressLint("MissingPermission")
    public void bluetoothDiscover() {
        if (mBluetoothAdapter.isDiscovering()) { //already discovering > cancel
            mBluetoothAdapter.cancelDiscovery();
            Toast.makeText(getApplicationContext(), "Canceling discovery", Toast.LENGTH_SHORT).show();

            checkBTPermissions(); //check for permissions 빌드에 전혀 문제 없는 오류임

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadCastReceiver, discoverDevicesIntent);
        } else if (!mBluetoothAdapter.isDiscovering()) {
            checkBTPermissions();
            Toast.makeText(getApplicationContext(), "Starting discovery", Toast.LENGTH_SHORT).show();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadCastReceiver, discoverDevicesIntent);
        }
    }


    // onDestroy 맨 마지막에 들어 가야함.
    @Override
    protected void onDestroy() {
        Toast.makeText(getApplicationContext(),"onDestroy called", Toast.LENGTH_SHORT).show();
        Log.d("onDestroy", "onDestroy called");
        super.onDestroy();
        unregisterReceiver(mBroadCastReceiver);
    }
}