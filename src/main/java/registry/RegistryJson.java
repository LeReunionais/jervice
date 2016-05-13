package registry;

import com.google.gson.Gson;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.UUID;

/**
 * Created on 4/30/16.
 */
public class RegistryJson implements Registry {
    private final String endpoint;
    private final Gson gson = new Gson();
    public RegistryJson(String registry_endpoint) {
        endpoint = registry_endpoint;
    }

    static class FindRequest {
        private final String jsonrpc = "2.0";
        private final String method = "find";
        private final String params;
        private final UUID id;

        FindRequest(String service_to_find) {
            this.params = service_to_find;
            this.id = UUID.randomUUID();
        }

        public String getName() {
            return this.params;
        }
        public UUID getId() {
            return id;
        }
    }

    static class FindResponse {
        private final String jsonrpc = "2.0";
        private final Service result;
        private final UUID id;

        public Service getResult() {
            return result;
        }

        FindResponse(Service service, UUID id) {
            this.result = service;
            this.id = id;
        }
    }

    @Override
    public Service whereis(String service_name) {
        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(ZMQ.REQ);
        socket.connect(endpoint);
        System.out.println("Connected to " + endpoint);

        String request_json = gson.toJson(new FindRequest(service_name));
        socket.send(request_json, 0);
        System.out.println("Sent request" + request_json);
        String replyStr = socket.recvStr(0);
        System.out.println("Received reply" + replyStr);
        socket.close();

        FindResponse response  = gson.fromJson(replyStr, FindResponse.class);

        return response.getResult();
    }
}
