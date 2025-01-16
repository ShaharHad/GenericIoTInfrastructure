package il.co.ilrd.GenericIoTInfrastructure.commands;


import il.co.ilrd.GenericIoTInfrastructure.connection_service.RespondableChannel;

@FunctionalInterface
public interface Command{
    void execute(RespondableChannel respondableChannel);
}
