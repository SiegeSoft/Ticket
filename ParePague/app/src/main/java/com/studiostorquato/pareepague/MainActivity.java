package com.studiostorquato.pareepague;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Set;


import com.studiostorquato.pareepague.pockdata.PocketPos;
import com.studiostorquato.pareepague.util.DateUtil;
import com.studiostorquato.pareepague.util.FontDefine;
import com.studiostorquato.pareepague.util.Printer;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static OutputStream btoutputstream;

    private ProgressDialog mProgressDlg;
    private ProgressDialog mConnectingDlg;

    private BluetoothAdapter mBluetoothAdapter;

    private P25Connector mConnector;

    private Spinner mDeviceSp;

    Button mEnableBtn, mConnectBtn, procurarBlt;

    String eNsu;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

    TextView placa, rua, telefone;
    String uniqueValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDeviceSp = (Spinner) findViewById(R.id.spDispositivos);
        mEnableBtn = (Button) findViewById(R.id.ligar_blt);
        mConnectBtn = (Button) findViewById(R.id.connect_blt);
        procurarBlt = (Button) findViewById(R.id.procurar_blt);

        placa = (TextView) findViewById(R.id.edit_placa);
        rua = (TextView) findViewById(R.id.edit_rua);
        telefone = (TextView) findViewById(R.id.edit_fone);

        Random rand = new Random();
        int randomNum = rand.nextInt((8 - 3) + 1) + 3;
        Calendar c = Calendar.getInstance();
        int seconds = c.get(Calendar.SECOND);
        int date = c.get(Calendar.DAY_OF_YEAR);
        uniqueValue = date+""+randomNum+""+seconds;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            showUnsupported();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                showDisabled();
            } else {
                showEnabled();

                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                if (pairedDevices != null) {
                    mDeviceList.addAll(pairedDevices);

                    updateDeviceList();
                }


                //
                mProgressDlg = new ProgressDialog(this);
                mProgressDlg.setMessage("Procurando...");
                mProgressDlg.setCancelable(false);
                mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        mBluetoothAdapter.cancelDiscovery();
                    }
                });


                //
                mConnectingDlg = new ProgressDialog(this);
                mConnectingDlg.setMessage("Conectando...");
                mConnectingDlg.setCancelable(false);

                mConnector = new P25Connector(new P25Connector.P25ConnectionListener() {

                    @Override
                    public void onStartConnecting() {
                        mConnectingDlg.show();
                    }

                    @Override
                    public void onConnectionSuccess() {
                        mConnectingDlg.dismiss();
                        showConnected();
                    }

                    @Override
                    public void onConnectionFailed(String error) {
                        mConnectingDlg.dismiss();
                    }

                    @Override
                    public void onConnectionCancelled() {
                        mConnectingDlg.dismiss();
                    }

                    @Override
                    public void onDisconnected() {
                        showDisonnected();
                    }
                });

            }

            IntentFilter filter = new IntentFilter();

            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

            registerReceiver(mReceiver, filter);
        }


    }

    ////*******///
    @Override
    public void onPause() {
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
        }

        if (mConnector != null) {
            try {
                mConnector.disconnect();
            } catch (P25ConnectionException e) {
                e.printStackTrace();
            }
        }

        super.onPause();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);

        super.onDestroy();
    }


    /*************************************/
    public void mEnableBtn(View v) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, 1000);
    }

    public void mConnectBtn(View v) {
        connect();
    }

    public void mPrintTextBtn(View v) {
        printContent();

    }


    public void pesquisarDispositivos(View v) {
        mBluetoothAdapter.startDiscovery();
    }

    private String[] getArray(ArrayList<BluetoothDevice> data) {
        String[] list = new String[0];

        if (data == null) return list;

        int size = data.size();
        list = new String[size];

        for (int i = 0; i < size; i++) {
            list[i] = data.get(i).getName();
        }

        return list;
    }


    /*************************************/
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void updateDeviceList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, getArray(mDeviceList));

        adapter.setDropDownViewResource(R.layout.spinner_item);

        mDeviceSp.setAdapter(adapter);
        mDeviceSp.setSelection(0);
    }


    /********************/
    private void showDisabled() {
        showToast("Bluetooth desabilitado");

        mEnableBtn.setVisibility(View.VISIBLE);
        mConnectBtn.setVisibility(View.GONE);
        mDeviceSp.setVisibility(View.GONE);

    }

    private void showEnabled() {
        showToast("Bluetooth habilitado");

        mEnableBtn.setVisibility(View.GONE);
        mConnectBtn.setVisibility(View.VISIBLE);
        mDeviceSp.setVisibility(View.VISIBLE);
    }

    private void showUnsupported() {
        showToast("Bluetooth não é suportado nesse dispositivo");


        mDeviceSp.setEnabled(false);

    }

    private void showConnected() {
        showToast("Conectado");

        mConnectBtn.setText("Desconectado");


        mDeviceSp.setEnabled(true);
    }

    private void showDisonnected() {
        showToast("Desconectado");

        mConnectBtn.setText("Conectado");
        mDeviceSp.setEnabled(true);
    }

    private void connect() {
        if (mDeviceList == null || mDeviceList.size() == 0) {
            return;
        }

        BluetoothDevice device = mDeviceList.get(mDeviceSp.getSelectedItemPosition());

        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            try {
                createBond(device);
            } catch (Exception e) {
                showToast("Falha ao parear o dispositivo");

                return;
            }
        }

        try {
            if (!mConnector.isConnected()) {
                mConnector.connect(device);
            } else {
                mConnector.disconnect();

                showDisonnected();
            }
        } catch (P25ConnectionException e) {
            e.printStackTrace();
        }
    }

    private void createBond(BluetoothDevice device) throws Exception {

        try {
            Class<?> cl = Class.forName("android.bluetooth.BluetoothDevice");
            Class<?>[] par = {};

            Method method = cl.getMethod("createBond", par);

            method.invoke(device);

        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }
    }

    private void sendData(byte[] bytes) {
        try {
            mConnector.sendData(bytes);
        } catch (P25ConnectionException e) {
            e.printStackTrace();
        }
    }


    private void printContent() {

        /*********** print head*******/
        String titleStr = "\n\n\n\n+TRÓIA PARK"+"\n\nTICKET DE ENTRADA ANTECIPADO\n\n";

        StringBuilder bodyStr = new StringBuilder();

        /******print body********/
        long milis = System.currentTimeMillis();
        String date = DateUtil.timeMilisToString(milis, "dd-MM-yy / HH:mm");

        bodyStr.append("\nCUPOM   : "+ uniqueValue + "\n");
        bodyStr.append("DATA-HORA : " + date + "\n");
        bodyStr.append("PLACA     : " + placa.getText().toString() + "\n");
        bodyStr.append("RUA       : "+ rua.getText().toString() + "\n");
        bodyStr.append("FONE      : "+ telefone.getText().toString() + "\n\n\n\n");

        byte[] titleByte = Printer.printfont(titleStr, FontDefine.FONT_24PX, FontDefine.Align_CENTER,
                (byte) 0x1A, PocketPos.LANGUAGE_CHINESE);

        byte[] bodyByte = Printer.printfont(bodyStr.toString(), FontDefine.FONT_24PX, FontDefine.Align_LEFT,
              (byte) 0x1A, PocketPos.LANGUAGE_CHINESE);

        byte[] totalByte = new byte[titleByte.length + bodyByte.length];

        int offset = 0;

        System.arraycopy(titleByte, 0, totalByte, offset, titleByte.length);
        offset += titleByte.length;

        System.arraycopy(bodyByte, 0, totalByte, offset, bodyByte.length);
        offset += bodyByte.length;

        byte[] senddata1 = PocketPos.FramePack(PocketPos.FRAME_TOF_PRINT, totalByte, 0, totalByte.length);

        sendData(senddata1);

    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                    showEnabled();
                } else if (state == BluetoothAdapter.STATE_OFF) {
                    showDisabled();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mDeviceList = new ArrayList<BluetoothDevice>();

                mProgressDlg.show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mProgressDlg.dismiss();

                updateDeviceList();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                mDeviceList.add(device);

                showToast("Dispositivo encontrado: " + device.getName());
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED) {
                    showToast("Pareado");

                    connect();
                }
            }
        }
    };
}
