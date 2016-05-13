package registry;

import com.google.gson.Gson;
import org.junit.Test;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;

/**
 * Created on 4/30/16.
 */
public class RegistryJsonTest {
    private final Gson gson = new Gson();
    @Test
    public void whereis() throws Exception {
        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(ZMQ.REP);
        String registry_endpoint = "ipc://registryTest";
        socket.bind(registry_endpoint);
        System.out.println("Socket bound to " + registry_endpoint);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        String service_name = "Youpi_service";
        Callable<Service> client_request = () -> {
            RegistryJson registry = new RegistryJson(registry_endpoint);
            return registry.whereis(service_name);
        };
        Future<Service> future_service = executorService.submit(client_request);

        System.out.println("Waiting for response");
        String request = socket.recvStr(0);
        System.out.println(request);

        RegistryJson.FindRequest findRequest = gson.fromJson(request, RegistryJson.FindRequest.class);
        assertEquals(service_name, findRequest.getName());

        String endpoint = "ipc://endpoint";
        Service youpi = new Service("endpoint", "ipc", -1, service_name);
        RegistryJson.FindResponse response = new RegistryJson.FindResponse(youpi, findRequest.getId());
        socket.send(gson.toJson(response));

        Service service = future_service.get();
        assertEquals(endpoint, service.getEndpoint());
        socket.close();
    }
}
