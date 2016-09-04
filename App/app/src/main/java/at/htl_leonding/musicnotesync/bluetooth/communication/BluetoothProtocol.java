package at.htl_leonding.musicnotesync.bluetooth.communication;

import java.nio.ByteBuffer;

/**
 * Created by michael on 30.08.16.
 * A mixture of rfcomm and propertiery commands
 */
public enum BluetoothProtocol {
    DTR, DSR, DCD, RTS, CTS, TXD, ENQ, ACK, NAK, SOH, EOT, OPF;

    public static byte[] toByteArray(BluetoothProtocol command){
        return ByteBuffer.allocate(4).putInt(command.ordinal()).array();
    }
}
