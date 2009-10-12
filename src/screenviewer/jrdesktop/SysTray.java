package jrdesktop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import jrdesktop.server.ActiveConnectionsGUI;
import jrdesktop.server.ConfigGUI;
import jrdesktop.server.rmi.Server;
import jrdesktop.viewer.ConnectionDialog;

/**
 * SysTray.java
 * @author benbac
 */
public class SysTray {

    final static public int SERVER_STARTED = 1;
    final static public int SERVER_STOPPED = 2;
    final static public int CONNECTION_FAILED = 3;
    final static public int SERVER_RUNNING = 4;
    final static public int SERVER_NOT_RUNNING = 5;    
    private static MenuItem serverItem;
    private static TrayIcon trayIcon;
    private static boolean enabled = false;
    
    public static void updateServerStatus(int msgType) {
        if (!SystemTray.isSupported() || enabled == false) return;
        
        switch (msgType) {
            case SERVER_RUNNING:
                serverItem.setLabel("Stop Server");
                if (Server.isRunning()) {
                    if (Server.getViewersCount() != 0)
                        trayIcon.setImage(new ImageIcon(main.ALIVE_ICON).getImage());
                    else
                        trayIcon.setImage(new ImageIcon(main.WAIT_ICON).getImage());
                }
                trayIcon.setToolTip("jrdesktop [Server running]\n" + 
                        jrdesktop.server.Config.server_address);              
                break;
            case SERVER_NOT_RUNNING:
                serverItem.setLabel("Start");
                trayIcon.setImage(new ImageIcon(main.IDLE_ICON).getImage());
                trayIcon.setToolTip("jrdesktop [Server stopped]\n" + 
                        jrdesktop.server.Config.server_address);
                break;                
            case SERVER_STARTED:
                serverItem.setLabel("Stop");
                trayIcon.displayMessage("Connection status", "Server Started !!",
                        TrayIcon.MessageType.INFO);
                trayIcon.setImage(new ImageIcon(main.WAIT_ICON).getImage());
                trayIcon.setToolTip("jrdesktop [Server running]\n" + 
                        jrdesktop.server.Config.server_address);
                break;
            case CONNECTION_FAILED:
                trayIcon.displayMessage("Connection status", "Connection Failed !!",
                        TrayIcon.MessageType.ERROR);
                break;
            case SERVER_STOPPED:
                serverItem.setLabel("Start");
                trayIcon.displayMessage("Connection status", "Server Stopped !!",
                        TrayIcon.MessageType.INFO);
                trayIcon.setImage(new ImageIcon(main.IDLE_ICON).getImage());
                trayIcon.setToolTip("jrdesktop [Server stopped]\n" + 
                        jrdesktop.server.Config.server_address);
                break;
        }
        serverItem.setEnabled(true);
    }

    public static void displayViewer(String viewer, int size, boolean connected) {
        if (!SystemTray.isSupported() || enabled == false) return;

        if (connected) {
            trayIcon.displayMessage("Viewer details", viewer + " connected !!",
                    TrayIcon.MessageType.INFO);
            if (size == 0) {
                trayIcon.setImage(new ImageIcon(main.ALIVE_ICON).getImage());
            }
        } else {
            trayIcon.displayMessage("Viewer details", viewer + " disconnected !!",
                    TrayIcon.MessageType.INFO);
            if (size == 0) {
                trayIcon.setImage(new ImageIcon(main.WAIT_ICON).getImage());
            }
        }
    }

    public static boolean isSupported() {
        return SystemTray.isSupported();
    }

    public static boolean isEnabled() {
        return enabled;
    }
    
    public static boolean isServerRunning() {
        boolean bool = Server.isRunning();
        if (!bool) {
            JOptionPane.showMessageDialog(null,
                    "Server is not running !!",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
        }
        return bool;
    }

    public static void Hide() {
        enabled = false;
        if (!SystemTray.isSupported()) return;
        final SystemTray tray = SystemTray.getSystemTray();
        tray.remove(trayIcon);
    }
    
    public static void Show() {
        if (!SystemTray.isSupported()) return;
        enabled = true;
        Runnable runner = new Runnable() {
            public void run() {
                final SystemTray tray = SystemTray.getSystemTray();
                PopupMenu popup = new PopupMenu();
                trayIcon = new TrayIcon(new ImageIcon(main.IDLE_ICON).getImage(),
                        "jrdesktop", popup);
                trayIcon.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                       // mainFrame.main(null);
                    }
                });

                /*
                MenuItem item = new MenuItem("Open jrdesktop");
                item.setFont(new Font(null, Font.BOLD, 12));
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        mainFrame.main(null);
                    }
                });
                
                popup.add(item);  
                
                item = new MenuItem("-");
                popup.add(item);

                Menu menu = new Menu("Server");
                
                serverItem = new MenuItem("Start");

                serverItem.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        serverItem.setEnabled(false);
                        if (Server.isRunning()) {
                            Server.Stop();
                        } else {
                            Server.Start();
                        }
                    }
                });
                menu.add(serverItem);

                item = new MenuItem("Configuration ...");
                item.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        ConfigGUI.main(null);
                    }
                });
                menu.add(item);
                
                item = new MenuItem("Active Connections");
                item.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        if (isServerRunning()) {
                            ActiveConnectionsGUI.main(null);
                        }
                    }
                });
                menu.add(item);
                popup.add(menu);

                item = new MenuItem("-");
                popup.add(item);

                item = new MenuItem("Connect to Server ...");
                item.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        ConnectionDialog.main(null);
                    }
                });
                popup.add(item);

                item = new MenuItem("-");
                popup.add(item);

                item = new MenuItem("About");
                item.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        AboutGUI.main(null);
                    }
                });
                popup.add(item);
		*/
                MenuItem item = new MenuItem("Exit");
                item.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        if (JOptionPane.showConfirmDialog(null, 
                                "Exit application ?", "Confirm Dialog", 
                                JOptionPane.OK_CANCEL_OPTION) ==
                                JOptionPane.OK_OPTION) {
                            tray.remove(SysTray.trayIcon);
                            main.exit();
                        }
                    }
                });
                popup.add(item);

                try {
                    tray.add(trayIcon);
                } catch (AWTException e) {
                    System.err.println("Can't add to tray");
                }
                
            if (Server.isRunning())
                SysTray.updateServerStatus(SERVER_RUNNING);
            else {
                if (!Server.isIdle())
                    SysTray.updateServerStatus(SERVER_NOT_RUNNING);
            }                
            }
        };
        EventQueue.invokeLater(runner);
    }
}
