package at.htl_leonding.musicnotesync.bluetooth.deprecated.communication;

import java.nio.ByteBuffer;

/**
 * Created by michael on 05.09.16.
 */
public enum Flag {
    CONNECT, FILE, META, ACK, NAK, DATA, EOT;

    public static byte[] toByteArray(Flag command){
        return ByteBuffer.allocate(4).putInt(command.ordinal()).array();
    }

    public static Flag fromByteArray(byte[] buffer){
        int result = ByteBuffer.wrap(buffer).getInt();

        for(Flag f : Flag.values()){
            if(f.ordinal() == result){
                return f;
            }
        }

        return null;
    }
}
