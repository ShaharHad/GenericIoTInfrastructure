package connection_service;

import java.nio.ByteBuffer;

public interface RespondableChannel{
    void respond(ByteBuffer bytes);
}
