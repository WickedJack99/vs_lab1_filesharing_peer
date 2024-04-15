/**
 * @author Aaron Moser
 */
package vslab1.src.View.Panels;

import vslab1.src.View.ColorTheme.EColorTheme;
import vslab1.src.View.Panels.StartPanel.StartPanel;

public record PanelOrganizer(StartPanel startPanel) {
    public Panel getPanelInstance(EPanelType panelType) {
        switch (panelType) {
            case StartPanel: {
                return startPanel;
            }
            default: {
                return null;
            }
        }
    }

    public void updateLanguage() {
        startPanel.updateLanguage();
    }

    public void updateColorTheme() {
        startPanel.updateColorTheme();
    } 
}
