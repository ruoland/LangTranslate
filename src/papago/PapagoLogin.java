package papago;

import swing.MainNew;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class PapagoLogin extends JFrame implements ActionListener {
    JButton loginButton;
    JTextField clientTextField, secretTextField;
    File papagoFile = new File("./papago");
    Properties properties = new Properties();

    public PapagoLogin() throws Exception {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());
        loginButton = new JButton("연결");
        clientTextField = new JTextField(30);
        secretTextField = new JTextField(30);
        loginButton.addActionListener(this);
        add(clientTextField);
        add(secretTextField);
        add(loginButton);
        setVisible(true);
        fileInit();
    }

    public void fileInit() throws Exception {

        papagoFile.createNewFile();
        properties.load(new FileReader(papagoFile));
        if (!properties.isEmpty()) {
            dispose();
            PapagoAPI.clientId = (String) properties.get("papagoID");
            PapagoAPI.clientSecret = (String) properties.get("papagoSecret");
            new MainNew();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == loginButton) {
                PapagoAPI.clientId = clientTextField.getText();
                PapagoAPI.clientSecret = secretTextField.getText();
                String test = PapagoAPI.main(this, "a");
                if (test != null) {
                    dispose();
                    properties.setProperty("papagoID", PapagoAPI.clientId);
                    properties.setProperty("papagoSecret", PapagoAPI.clientSecret);
                    properties.store(new FileWriter(papagoFile), "");
                    new MainNew();
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
