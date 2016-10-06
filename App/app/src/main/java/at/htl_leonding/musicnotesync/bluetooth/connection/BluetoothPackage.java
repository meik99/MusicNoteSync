package at.htl_leonding.musicnotesync.bluetooth.connection;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import at.htl_leonding.musicnotesync.bluetooth.BluetoothConstants;

/**
 * Created by michael on 19.09.16.
 */

public class BluetoothPackage {
    private Flag flag;
    private byte[] content;

    public BluetoothPackage(){
        content = new byte[BluetoothConstants.BUFFER_CONTENT_SIZE];
    }

    public Flag getFlag() {
        return flag;
    }

    public void setFlag(Flag flag) {
        this.flag = flag;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        if(content.length > BluetoothConstants.BUFFER_CONTENT_SIZE){
            throw new IllegalArgumentException();
        }
        this.content = content;
    }

    public static BluetoothPackage fromByteArray(byte[] buffer){
        int flagNumber = ByteBuffer.wrap(buffer, 0, BluetoothConstants.BUFFER_FLAG_SIZE).getInt();
        Flag resultFlag = Flag.fromInt(flagNumber);
        BluetoothPackage result = new BluetoothPackage();
        byte[] resultBuffer = new byte[buffer.length - BluetoothConstants.BUFFER_FLAG_SIZE];

        for(int i = 0; i < resultBuffer.length; i++){
            resultBuffer[i] = buffer[i + BluetoothConstants.BUFFER_FLAG_SIZE];
        }

        if(resultFlag == null){
            throw new IllegalArgumentException("Buffer has illegal format");
        }

        result.setFlag(resultFlag);
        result.setContent(resultBuffer);

        return result;
    }

    public static BluetoothPackage fromByteArray(Byte[] buffer) {
        byte[] tmp = new byte[buffer.length];
        for(int i = 0; i < buffer.length; i++){
            tmp[i] = buffer[i];
        }
        return fromByteArray(tmp);
    }


    public byte[] toByteArray(){
        byte[] buffer = new byte[BluetoothConstants.BUFFER_FLAG_SIZE + content.length];
        byte[] flagBuffer =
                ByteBuffer.allocate(BluetoothConstants.BUFFER_FLAG_SIZE)
                        .putInt(flag.getValue())
                        .array();
        for(int i = 0; i < flagBuffer.length; i++){
            buffer[i] = flagBuffer[i];
        }

        for(int i = 0; i < content.length; i++){
            buffer[BluetoothConstants.BUFFER_FLAG_SIZE + i] = content[i];
        }

        return buffer;
    }
}
