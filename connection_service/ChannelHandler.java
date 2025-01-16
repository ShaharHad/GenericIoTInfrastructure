package il.co.ilrd.GenericIoTInfrastructure.connection_service;

import java.nio.channels.SelectableChannel;

public interface ChannelHandler {
    void handle(SelectableChannel channel);
}
