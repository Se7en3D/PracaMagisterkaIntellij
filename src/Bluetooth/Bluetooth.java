package Bluetooth;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.InputStream;
import java.io.OutputStream;

public class Bluetooth {
    private static RemoteDevice[] remoteDevice;
    private static StreamConnection streamConnection;
    private static InputStream inputStream;
    private static OutputStream outputStream;
    private static String NamePort="";
    public void searchDevices() throws BluetoothStateException {
        LocalDevice localDevice=LocalDevice.getLocalDevice();

        this.remoteDevice=localDevice.getDiscoveryAgent().retrieveDevices(DiscoveryAgent.PREKNOWN);
    }

    public void connectToDevice(String friendlyName) throws Exception{
        closeConnection();
        for(RemoteDevice i:remoteDevice){
            if(i.getFriendlyName(false).equals(friendlyName)){
                this.streamConnection=(StreamConnection) Connector.open("btspp://"+i.getBluetoothAddress()+":1;authenticate=true;encrypt=true;master=false;");
                this.inputStream=streamConnection.openInputStream();
                this.outputStream=streamConnection.openOutputStream();
                this.NamePort=friendlyName;
            }
        }
    }
    public RemoteDevice[] getRemoteDevice() {
        return remoteDevice;
    }
    synchronized public void sendData(byte[] bytes ) throws Exception {
        if (outputStream == null){
            return;
        }
        outputStream.write(0xFF);
        outputStream.write(bytes);
        outputStream.write(0xFE);
    }
    synchronized public void sendData(byte Byte ) throws Exception {
        if (outputStream == null){
            return;
        }
        outputStream.write(0xFF);
        outputStream.write(Byte);
        outputStream.write(0xFE);
        outputStream.write(0xFE);
    }
    synchronized public void sendCalibrationPWMData(byte function ,int period,int cycle) throws Exception {
        if (outputStream == null){
            return;
        }
        outputStream.write(0xFF);
        outputStream.write(function);
        //System.out.println("sendCalibrationPWMData() period="+((byte)period>>8));
        outputStream.write((byte) (period>>8));
        outputStream.write((byte) period);
        outputStream.write((byte)(cycle>>8));
        outputStream.write((byte)cycle);
        outputStream.write(0xFE);
        outputStream.write(0xFE);

    }
    public byte[] getData() throws Exception{
        if(inputStream!=null &&  inputStream.available()>0){
                byte[] bytes=new byte[inputStream.available()];
                inputStream.read(bytes);
                return bytes;
        }
        return null;

    }
    public void closeConnection() throws Exception{
        if(inputStream!=null){
            inputStream.close();
        }
        if(outputStream!=null){
            outputStream.close();
        }
        if(streamConnection!=null){
            streamConnection.close();
        }
    }
    public void setRemoteDevice(RemoteDevice[] remoteDevice) {
        this.remoteDevice = remoteDevice;
    }

}
