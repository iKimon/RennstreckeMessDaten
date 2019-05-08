package com.example.team919.rennstreckemessdaten;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Calendar;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    EditText txt_name;
    Button btn_start;
    TextView txtACC;
    TextView txtGyro;
    Spinner spinnerSensorSpeed;

    DecimalFormat decimalFormat;

    SensorManager sensorManager;

    LinkedList<String> gyroList = new LinkedList<>();
    LinkedList<String> ACCList = new LinkedList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_start = findViewById(R.id.btn_start);
        txtACC = findViewById(R.id.txtACC);
        txtGyro = findViewById(R.id.txtGyro);
        spinnerSensorSpeed = findViewById(R.id.spinnerSensorSpeed);
        txt_name = findViewById(R.id.txt_name);

        decimalFormat = new DecimalFormat("00.00");


        //Inhalt der Spinner
        LinkedList<String> spinnerSensorSpeedList = new LinkedList<>();
        spinnerSensorSpeedList.add("FASTEST");
        spinnerSensorSpeedList.add("GAME");
        spinnerSensorSpeedList.add("UI");
        spinnerSensorSpeedList.add("NORMAL");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerSensorSpeedList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSensorSpeed.setAdapter(arrayAdapter);


        //############################FileWriter############################

        FileWriter fWriter;





        //############################SensorManager/Listener############################
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        final SensorEventListener SElistener= new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                switch(event.sensor.getType()){
                    case Sensor.TYPE_ACCELEROMETER:
                        txtACC.setText("ACC:\n" + "X: " + decimalFormat.format(event.values[0])  + "\nY: " + decimalFormat.format(event.values[1]) + "\nZ: " + decimalFormat.format(event.values[2]));

                        ACCList.add("\""+ Calendar.getInstance().getTime().getTime() + "\";\"" + event.values[0]  + "\";\"" +event.values[1]  + "\";\"" +event.values[2]  + "\";");
                    break;

                    case Sensor.TYPE_GYROSCOPE:
                        txtGyro.setText("GYRO:\n" + "X: " + decimalFormat.format(event.values[0])  + "\nY: " + decimalFormat.format(event.values[1]) + "\nZ: " + decimalFormat.format(event.values[2]));

                        gyroList.add("\"" + event.values[0]  + "\";\"" +event.values[1]  + "\";\"" +event.values[2]  + "\"\n");

                    break;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_start.getText() != "STOP") {
                    registerSensor(SElistener);
                    spinnerSensorSpeed.setEnabled(false);
                    btn_start.setText("STOP");

                }else{
                    sensorManager.unregisterListener(SElistener);
                    btn_start.setText("START");
                    spinnerSensorSpeed.setEnabled(true);

                    //############################FileWriter###########################
                    File dir = new File(Environment.getExternalStorageDirectory(),"Praktikum2");
                    if(!dir.exists()){
                        if(!dir.mkdirs()){
                            Log.e("Error_wasmanbessersuchenkann", "Can't create Directory");
                        }
                    }


                    int j = 0;
                    File file = new File(dir,txt_name.getText() + Integer.toString(j)+".csv");
                    while(file.exists()){
                        file = new File(dir,txt_name.getText() + Integer.toString(j)+".csv");
                        j++;
                    }

                    try{
                        FileOutputStream fos = new FileOutputStream(file,false);
                        Log.e("Error_wasmanbessersuchenkann", Integer.toString(ACCList.size()) +" --------" +Integer.toString(gyroList.size()));
                        int smaler;
                        if(ACCList.size()<gyroList.size()){
                            smaler =ACCList.size();
                        }else {
                            smaler = gyroList.size();
                        }
                        String bezeichnung = "\"Timestamp\";\"ACCX\";\"ACCY\";\"ACCZ\";\"GYROX\";\"GYROY\";\"GYROZ\";\n";
                        fos.write(bezeichnung.getBytes());
                        for(int i=0;i<smaler;i++){
                            fos.write(ACCList.get(i).getBytes());
                            fos.write(gyroList.get(i).getBytes());
                        }

                        fos.close();

                    } catch (FileNotFoundException e1){
                        e1.printStackTrace();
                        Log.e("Error", "FileNotFoundException)");
                    } catch (IOException e2){
                        e2.printStackTrace();
                        Log.e("Error", "IOException");
                    }


                }

            }
        });
    }


    private void registerSensor(SensorEventListener SE){
        sensorManager.unregisterListener(SE);
        sensorManager.registerListener(SE,sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),spinnerSensorSpeed.getSelectedItemPosition());
        sensorManager.registerListener(SE,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),spinnerSensorSpeed.getSelectedItemPosition());

    }

}
