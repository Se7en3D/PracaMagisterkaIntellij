package BluetoothPackage;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

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
        for(RemoteDevice i:remoteDevice){

            if(i.getFriendlyName(false).equals(friendlyName)){
                this.streamConnection=(StreamConnection) Connector.open("btspp://"+i.getBluetoothAddress()+":1;authenticate=true;encrypt=true;master=false;");
                this.inputStream=streamConnection.openInputStream();
                this.outputStream=streamConnection.openOutputStream();
                this.NamePort=friendlyName;
                System.out.println("Połączony z bluetooth "+friendlyName);
            }
        }
    }
    public RemoteDevice[] getRemoteDevice() {
        return remoteDevice;
    }
    public ArrayList<String> getFrendlyName() throws IOException {
        ArrayList<String> frendlyName=new ArrayList<>();
        int sizeRemoteDevice=0;
        if(remoteDevice==null){
            searchDevices();
        }
        sizeRemoteDevice=this.remoteDevice.length;
        for(int i=0;i<sizeRemoteDevice;i++){
            String tempString=this.remoteDevice[i].getFriendlyName(false);
            if(tempString!=null) {
                frendlyName.add(tempString);
            }
        }

        return frendlyName;
    }


    public void closeConnection() throws Exception{
       if(inputStream!=null){
            inputStream.close();
            inputStream=null;
        }
        if(outputStream!=null){
            outputStream.close();
            outputStream=null;
        }
        if(streamConnection!=null){
            streamConnection.close();
            streamConnection=null;
        }
    }

    public void setRemoteDevice(RemoteDevice[] remoteDevice) {
        this.remoteDevice = remoteDevice;
    }

    public byte[] getInputData() throws IOException {
        if(inputStream!=null && inputStream.available()>0){
            byte[] bytes=new byte[inputStream.available()];
            inputStream.read(bytes);
            return bytes;
        }
        return null;
    }
}
