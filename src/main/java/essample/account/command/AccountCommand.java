package essample.account.command;

public class AccountCommand {

    private CommandType commandType;

    private long payload;

    public AccountCommand(CommandType commandType, long payload) {
        this.commandType = commandType;
        this.payload = payload;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public long getPayload() {
        return payload;
    }

    public boolean isValidCommand() {
        return payload > 0;
    }
}
