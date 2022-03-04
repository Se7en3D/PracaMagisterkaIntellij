package BluetoothPackage;

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
