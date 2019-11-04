package net;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;

public class DNSResolver {
    /**
     * Well-known port for DNS servers
     */
    static final int DNS_PORT = 53;

    /**
     * Maximum size of a UDP message
     */
    static final int MAX_DNS_MSG_SIZE = 512;


    /**
     * Read a name, in the DNS Resource-record (RR) format supporting the DNS compression scheme.
     * (see RFC 1035 for details)
     *
     * @param name    The StringBuilder into which the name will be written (if not null)
     * @param origBuf The buffer from which the name should be read (it will be appended)
     * @param offset  The offset in the buffer at which the name starts.
     * @return the number of bytes read from the buffer.
     * @throws IOException (in case the name is badly formatted)
     */
    public int readName(StringBuilder name, byte[] origBuf, int offset) throws IOException {

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(origBuf, offset, origBuf.length - offset));

        int pos = 0;
        int labelLen = in.readUnsignedByte();
        while (labelLen > 0) {
            if ((labelLen & 0xc0) == 0xc0) {
                // This is a pointer rather than an actual string
                // We use readName recursively to get the actual suffix.
                int ptr = in.readUnsignedByte();
                ptr |= (labelLen << 8) & ~(0xc000);
                readName(name, origBuf, ptr);
                break;

            } else if (labelLen > 63) {
                throw new IOException("label in domain name must be under 64 chars");
            } else if (in.available() < labelLen) {
                throw new EOFException("label longer than buffer");
            }
            byte[] buf = new byte[labelLen];
            in.readFully(buf);
            if (name != null)
                name.append(new String(buf));
            labelLen = in.readUnsignedByte();
            if (labelLen > 0 && name != null)
                name.append(".");
        }
        return origBuf.length - offset - in.available();
    }


    /**
     * Query a DNS nameserver for a hostname and return the corresponding IP address.
     * <p>
     * (you can use {@link InetAddress#getByAddress} to generate an {@link InetAddress} object).
     * <p>
     * You may assume the hostname and nameserver actually exist (you don't have to handle I/O
     * errors).
     *
     * @param hostname
     * @param nameServer
     * @return
     * @throws IOException
     */
    InetAddress queryDNS(String hostname, InetAddress nameServer) throws IOException {
        // TODO: Write implementation
        return null;
    }
}
