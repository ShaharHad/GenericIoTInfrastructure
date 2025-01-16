package commands;


import il.co.ilrd.GenericIoTInfrastructure.connection_service.RespondableChannel;

@FunctionalInterface
public interface Command{
    void execute(RespondableChannel respondableChannel);
}
