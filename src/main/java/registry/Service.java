package registry;

/**
 * Created on 5/13/16.
 */
public class Service {
    private final String hostname;
    private final String protocol;
    private final int port;
    private final String name;

    public Service(String hostname, String protocol, int port, String name) {
        this.hostname = hostname;
        this.protocol = protocol;
        this.port = port;
        this.name = name;
    }

    public String getEndpoint() {
        if (port != -1) {
            if (protocol.equals("REP")) {
                return "tcp://" + hostname + ":" + port;
            } else {
                return "tcp://" + hostname + ":" + port;
            }
        } else {
            return protocol + "://" + hostname;
        }
    }
}
