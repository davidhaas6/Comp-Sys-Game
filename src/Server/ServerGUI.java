package Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
/*
 * Created by JFormDesigner on Thu Jun 25 11:15:18 EDT 2015
 */

/**
 * @author David Haas
 */
class ServerGUI extends JFrame {
    private HashMap<String, String> connectedUsers;
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Jimmy Smith
    private JTextPane textPane1;
    private JScrollPane scrollPane1;
    private JTextArea textArea1;
    private JButton button1;
    private JTextPane textPane2;

    public ServerGUI() {
        initComponents();
        setVisible(true);
        connectedUsers = new HashMap<>();
    }

    public void addUser(String IP, String userName) {
        connectedUsers.put(IP, userName);
        textArea1.setText(formattedUserInfo());
    }

    public void removeUser(String IP) {
        connectedUsers.remove(IP);
        textArea1.setText(formattedUserInfo());
    }

    public void setServerIP(String IP) {
        textPane1.setText("Server IP: " + IP);
    }

    private String formattedUserInfo() {
        String str = "";
        for (String ip : connectedUsers.keySet())
            str += ip + "   -   " + connectedUsers.get(ip) + "\n";
        return str;
    }

    private void buttonMouseClicked(MouseEvent e) {
        System.exit(1);
        Server.serverOpen = false;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - David Haas
        textPane1 = new JTextPane();
        scrollPane1 = new JScrollPane();
        textArea1 = new JTextArea();
        button1 = new JButton();
        textPane2 = new JTextPane();
        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setTitle("Server GUI");
        Container contentPane = getContentPane();
        //---- textPane1 ----
        textPane1.setText("Server IP:");
        textPane1.setEditable(false);
        textPane1.setFont(new Font("Verdana", Font.PLAIN, 12));

        //======== scrollPane1 ========
        {
            //---- textArea1 ----
            textArea1.setEditable(false);
            scrollPane1.setViewportView(textArea1);
        }
        //---- button1 ----
        button1.setText("Shutdown server");
        button1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                buttonMouseClicked(e);
            }
        });
        //---- textPane2 ----
        textPane2.setText("Connected users:");
        textPane2.setFont(new Font("Verdana", Font.PLAIN, 10));
        //TODO Figure out why server hangs on this step ***
        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(button1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                                        .addComponent(textPane1, GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addComponent(textPane2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 168, Short.MAX_VALUE))
                                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE))
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(textPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textPane2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 366, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
