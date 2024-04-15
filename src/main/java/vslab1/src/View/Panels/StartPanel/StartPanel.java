/**
 * @author Aaron Moser
 */
package vslab1.src.View.Panels.StartPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import vslab1.src.View.Settings;
import vslab1.src.View.Panels.Panel;

public class StartPanel extends JPanel implements Panel {

    private Settings settings = null;

    private JLabel ipLabel = null;
    private JLabel portLabel = null;

    private JTextField ipTextField = null;
    private JTextField portTextField = null;

    private JButton submitButton = null;

    public StartPanel(Settings settings) {
        this.settings = settings;
        build();
    }

    @Override
    public void updateLanguage() {
        if (ipLabel != null) {
            ipLabel.setText(StartPanelLanguageConstants.ipLabelStrings[settings.getActiveLanguageOrdinal()]);
        }
        if (portLabel != null) {
            portLabel.setText(StartPanelLanguageConstants.portLabelStrings[settings.getActiveLanguageOrdinal()]);
        }
    }

    @Override
    public void updateColorTheme() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changeColorTheme'");
    }

    private void build() {
        this.setLayout(new GridBagLayout());
        addIPLabel(0,0);
        addIPTextField(1,0);
        addPortLabel(2,0);
        addPortTextField(3,0);
        addSubmitButton(4, 0);
    }

    private void addIPLabel(int row, int column) {
        ipLabel = new JLabel(StartPanelLanguageConstants.ipLabelStrings[settings.getActiveLanguageOrdinal()]);

        GridBagConstraints ipLabelConstraints = new GridBagConstraints();
        ipLabelConstraints.gridx = column;
        ipLabelConstraints.gridy = row;
        ipLabelConstraints.anchor = GridBagConstraints.NORTHWEST;
        this.add(ipLabel, ipLabelConstraints);
    }

    private void addIPTextField(int row, int column) {
        ipTextField = new JTextField(20);

        GridBagConstraints ipTextFieldConstraints = new GridBagConstraints();
        ipTextFieldConstraints.gridx = column;
        ipTextFieldConstraints.gridy = row;
        ipTextFieldConstraints.anchor = GridBagConstraints.NORTHWEST;
        this.add(ipTextField, ipTextFieldConstraints);
    }

    private void addPortLabel(int row, int column) {
        portLabel = new JLabel(StartPanelLanguageConstants.portLabelStrings[settings.getActiveLanguageOrdinal()]);

        GridBagConstraints portLabelConstraints = new GridBagConstraints();
        portLabelConstraints.gridx = column;
        portLabelConstraints.gridy = row;
        portLabelConstraints.anchor = GridBagConstraints.NORTHWEST;
        this.add(portLabel, portLabelConstraints);
    }

    private void addPortTextField(int row, int column) {
        portTextField = new JTextField(20);

        GridBagConstraints portTextFieldConstraints = new GridBagConstraints();
        portTextFieldConstraints.gridx = column;
        portTextFieldConstraints.gridy = row;
        portTextFieldConstraints.anchor = GridBagConstraints.NORTHWEST;
        this.add(portTextField, portTextFieldConstraints);
    }
    
    private void addSubmitButton(int row, int column) {
        submitButton = new JButton(StartPanelLanguageConstants.submitButtonStrings[settings.getActiveLanguageOrdinal()]);

        submitButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
            
        });

        GridBagConstraints submitButtonConstraints = new GridBagConstraints();
        submitButtonConstraints.gridx = column;
        submitButtonConstraints.gridy = row;
        submitButtonConstraints.anchor = GridBagConstraints.NORTHWEST;
        this.add(submitButton, submitButtonConstraints);
    }
}
