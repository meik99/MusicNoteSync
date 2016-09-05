package at.htl_leonding.musicnotesync.bluetooth.communication;

import java.nio.ByteBuffer;

/**
 * Created by michael on 30.08.16.
 * A mixture of rfcomm and propertiery commands
 *
 * ENQ: Enqueue - Request a process to start (E.q.: To let the client know he will receive a file)
 * ACK: Acknowledge - The last operation succeeded, was accepted, or "yes" as answer
 * NAK: Negative Acknowledge - The last operation failed, was denied, or "no" as answer
 * RTS: Request To Send - Ask if endpoint is ready for transmission
 * CTS: Clear To Send - Endpoint confirms that it is ready for transmission
 *
 * DSR
 */
public enum Operation {
    ENQ, ACK, NAK, RTS, CTS, SOH, DTR, DSR, DCD, TXD, EOT, OPF, OTH;

    public static byte[] toByteArray(Operation operation){
        return ByteBuffer.allocate(4).putInt(operation.ordinal()).array();
    }
}
