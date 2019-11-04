package net;


import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;

import static org.junit.Assert.assertEquals;

public class DNSResolverTest {
    final static String TEST_HOST = "factcenter.org";
    final static byte[] NS_ADDR = {8,8,8,8}; // This is Google's caching DNS server; it will answer queries from anyone.

    DNSResolver asker;

    @Before
    public void setup() {
        asker = new DNSResolver();
    }

    @Test
    public void testQuery() throws IOException {
        InetAddress NAMESERVER = InetAddress.getByAddress(NS_ADDR);

        InetAddress expected = InetAddress.getByName(TEST_HOST);

        InetAddress actual = asker.queryDNS(TEST_HOST, NAMESERVER);

        assertEquals(expected, actual);
    }

}
