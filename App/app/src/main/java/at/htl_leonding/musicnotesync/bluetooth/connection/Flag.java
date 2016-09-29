package at.htl_leonding.musicnotesync.bluetooth.connection;

/**
 * Created by michael on 19.09.16.
 */

public enum Flag {
    HANDSHAKE(0), POSITIVE(1), NEGATIVE(2), FILE(3), FILEDATA(4);

    private int value;

    private Flag(int value){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }

    public static Flag fromInt(int flagNumber){
        for(Flag f : Flag.values()){
            if(f.getValue() == flagNumber){
                return f;
            }
        }

        return null;
    }
}
