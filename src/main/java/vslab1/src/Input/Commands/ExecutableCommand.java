/**
 * @author Aaron Moser
 */
package vslab1.src.Input.Commands;

public interface ExecutableCommand {
    public String toString();
    public ECommandType getType();
    public void execute();
}
