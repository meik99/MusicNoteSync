package at.htl_leonding.musicnotesync.bluetooth.communication;

import java.nio.ByteBuffer;

/**
 * Created by michael on 05.09.16.
 */
public enum Command {
    FILE, RETR, ABRT;

    public static byte[] toByteArray(Command command){
        return ByteBuffer.allocate(4).putInt(command.ordinal()).array();
    }
}
