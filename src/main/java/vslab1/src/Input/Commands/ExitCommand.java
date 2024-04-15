/**
 * @author Aaron Moser
 */
package vslab1.src.Input.Commands;

import vslab1.src.Terminatable;

public record ExitCommand(Terminatable[] threadsToTerminate) implements ExecutableCommand {

    @Override
    public ECommandType getType() {
        return ECommandType.Exit;
    }

    @Override
    public void execute() {
        for (Terminatable thread : threadsToTerminate) {
            if (thread != null) {
                thread.terminate();
            }
        }
    }
    
}
