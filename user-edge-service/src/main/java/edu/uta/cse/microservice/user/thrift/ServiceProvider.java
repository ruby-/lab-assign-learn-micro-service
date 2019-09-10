package edu.uta.cse.microservice.user.thrift;

import edu.uta.cse.thrift.message.MSGService;
import edu.uta.cse.thrift.user.UserService;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFastFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Wrap up Thrift services (user service and message service as demo)
 */
@Component
public class ServiceProvider {

    @Value("${thrift.user.ip}")
    private String userServerIp;
    @Value("${thrift.user.port}")
    private int userServerPort;

    @Value("${thrift.message.ip}")
    private String messageServerIp;
    @Value("${thrift.message.port}")
    private int messageServerPort;

    /* Interfaces */
    public UserService.Client getUserService() {
        return getService(userServerIp, userServerPort, ServiceType.USER);
    }

    public MSGService.Client geMessageService() { return getService(messageServerIp, messageServerPort, ServiceType.MESSAGE); }

    /* Choose the service via type */
    private enum ServiceType {
        USER,
        MESSAGE,
    }

    public <T> T getService(String ip, int port, ServiceType type) {
        TSocket socket = new TSocket(ip, port, 3000);
        TTransport transport = new TFastFramedTransport(socket);
        try {
            transport.open();
        } catch (TTransportException e) {
            e.printStackTrace();
            return null;
        }
        TProtocol protocol = new TBinaryProtocol(transport);

        TServiceClient result = null;

        switch (type) {
            case USER:
                result = new UserService.Client(protocol);
                break;
            case MESSAGE:
                result = new MSGService.Client(protocol);
                break;
        }

        return (T) result;
    }
}
