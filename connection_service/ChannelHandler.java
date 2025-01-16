package connection_service;

import java.nio.channels.SelectableChannel;

public interface ChannelHandler {
    void handle(SelectableChannel channel);
}
