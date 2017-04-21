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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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

    Button mEnableBtn, mConnectBtn, procurarBlt, printButton;

    String eNsu;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

    EditText placa, rua, valor;
    float valortotal;
    TextView cod, codAlert;
    String uniqueValue;

    CheckBox bonus_item1, bonus_item2, bonus_item3, bonus_item4;

    public float bonus_value = 0, bonus_espelho = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        placa = (EditText) findViewById(R.id.editTextSign);
        rua = (EditText) findViewById(R.id.editTextAddress);
        valor = (EditText) findViewById(R.id.editTextValor);
        cod = (TextView) findViewById(R.id.txtViewCodTicket);

        bonus_item1 = (CheckBox) findViewById(R.id.bonus1);
        bonus_item2 = (CheckBox) findViewById(R.id.bonus2);
        bonus_item3 = (CheckBox) findViewById(R.id.bonus3);
        bonus_item4 = (CheckBox) findViewById(R.id.bonus4);

        valor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String userInput = valor.getText().toString();

                    if (TextUtils.isEmpty(userInput)) {
                        userInput = "0.00";
                    } else {
                        float floatValue = Float.parseFloat(userInput);
                        userInput = String.format("%.2f",floatValue);
                    }
                    valor.setText(userInput);
                }
            }
        });

        generateCupom();


    }

    public void clearTicket(View v){
        placa.setText("");
        rua.setText("");
        valor.setText("");
        generateCupom();
    }

    public void OnClickBonus1(View v){
        if(bonus_item1.isChecked()){
            bonus_item2.setChecked(false);
            bonus_item3.setChecked(false);
            bonus_item4.setChecked(false);
            bonus_value = 0.50f;
        }else{
            bonus_value = 0;
        }
    }
    public void OnClickBonus2(View v){
        if(bonus_item2.isChecked()){
            bonus_item1.setChecked(false);
            bonus_item3.setChecked(false);
            bonus_item4.setChecked(false);
            bonus_value = 0.50f;
        }else{
            bonus_value = 0;
        }
    }
    public void OnClickBonus3(View v){
        if(bonus_item3.isChecked()){
            bonus_item1.setChecked(false);
            bonus_item2.setChecked(false);
            bonus_item4.setChecked(false);
            bonus_value = 1.50f;
        }else{
            bonus_value = 0;
        }
    }
    public void OnClickBonus4(View v){
        if(bonus_item4.isChecked()){
            bonus_item1.setChecked(false);
            bonus_item2.setChecked(false);
            bonus_item3.setChecked(false);
            bonus_value = 2.00f;
        }else{
            bonus_value = 0;
        }
    }

    public void endTicket(View v){
        
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.print_layout, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

        mDeviceSp = (Spinner) dialogView.findViewById(R.id.spDispositivos);
        mEnableBtn = (Button) dialogView.findViewById(R.id.ligar_blt);
        mConnectBtn = (Button) dialogView.findViewById(R.id.connect_blt);
        procurarBlt = (Button) dialogView.findViewById(R.id.procurar_blt);
        printButton = (Button) dialogView.findViewById(R.id.print);
        codAlert = (TextView) dialogView.findViewById(R.id.textViewAlertTicket);

        codAlert.setText(uniqueValue);
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

    public void generateCupom(){
        Random rand = new Random();
        int randomNum = rand.nextInt((1000 - 3) + 1) + 3;
        Calendar c = Calendar.getInstance();
        int seconds = c.get(Calendar.SECOND);
        int date = c.get(Calendar.YEAR);
        uniqueValue = date+""+randomNum+""+seconds;
        cod.setText(uniqueValue);
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
        procurarBlt.setVisibility(View.GONE);
        printButton.setVisibility(View.GONE);


    }

    private void showEnabled() {
        showToast("Bluetooth habilitado");

        mEnableBtn.setVisibility(View.GONE);
        mConnectBtn.setVisibility(View.VISIBLE);
        mDeviceSp.setVisibility(View.VISIBLE);
        procurarBlt.setVisibility(View.VISIBLE);
        printButton.setVisibility(View.VISIBLE);

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
            generateCupom();
        } catch (P25ConnectionException e) {
            e.printStackTrace();
        }
    }


    private void printContent() {

        valortotal = Float.parseFloat(String.valueOf(valor.getText()));
        if(bonus_item1.isChecked()){
            valortotal += bonus_value;
        }
        if(bonus_item2.isChecked()){
            valortotal += bonus_value;
        }
        if(bonus_item3.isChecked()){
            valortotal += bonus_value;
        }
        if(bonus_item4.isChecked()){
            valortotal += bonus_value;
        }

        /*********** print head*******/
        String titleStr = "\n\n" +
                "\n==========================================" +
                "\nTROIA PARK"+
                "\n\nTICKET DE ENTRADA ANTECIPADO\n" +
                "==========================================\n\n";

        StringBuilder bodyStr = new StringBuilder();

        /******print body********/
        long milis = System.currentTimeMillis();
        String date = DateUtil.timeMilisToString(milis, "dd-MM-yy / HH:mm");

        bodyStr.append("\nCUPOM     : "+ uniqueValue + "\n");
        bodyStr.append("DATA-HORA : " + date + "\n");
        bodyStr.append("PLACA     : " + placa.getText().toString() + "\n");
        bodyStr.append("RUA       : "+ rua.getText().toString() + "\n");
        bodyStr.append("Valor     : "+ valor.getText().toString() + "\n\n");
        if(bonus_value != 0) {
        bodyStr.append("Credito   : "+ bonus_value + "\n");
        }
        bodyStr.append("Total     : "+ valortotal + "\n\n");
        bodyStr.append("========================================="+"\n\n");
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
