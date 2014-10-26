package core.util;

/**
 * Encapsulation of a host and port.
 */
public class HostAndPort {

    private final String host;

    private final int port;

    public HostAndPort(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

}
