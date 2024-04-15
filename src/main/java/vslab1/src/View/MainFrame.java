/**
 * @author Aaron Moser
 */
package vslab1.src.View;

import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.JFrame;

import vslab1.src.View.Language.ELanguage;
import vslab1.src.View.Panels.EPanelType;
import vslab1.src.View.Panels.PanelOrganizer;
import vslab1.src.View.Panels.StartPanel.StartPanel;

public class MainFrame extends JFrame {

    private Settings settings = null;

    private PanelOrganizer panelOrganizer = null;

    public MainFrame() {
        this.settings = new Settings(ELanguage.English);

        this.panelOrganizer = new PanelOrganizer(new StartPanel(settings));

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Peer to Peer Filesharing");
        //this.setLocation(null);

        this.setMainPanelTo(EPanelType.StartPanel);
    }

    public void setMainPanelTo(EPanelType panelType) {
        switch (panelType) {
            case StartPanel: {
                this.getContentPane().removeAll();
                this.getContentPane().add(panelOrganizer.startPanel());
            }break;
            default: {
                System.err.println("setMainPanelTo was called with unknown panel type.");
                return;
            }
        }
        this.setSize(new Dimension(1400, 700));
        this.update(getGraphics());
        this.setVisible(true);
    }
}
