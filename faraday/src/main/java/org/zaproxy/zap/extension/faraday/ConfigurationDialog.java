package faraday.src.main.java.org.zaproxy.zap.extension.faraday;


import org.apache.log4j.Logger;
import org.parosproxy.paros.Constant;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;


@SuppressWarnings("serial")
public class ConfigurationDialog extends JFrame {
    private static final Logger logger = Logger.getLogger(ConfigurationDialog.class);
    private FaradayClient faradayClient;

    private static String LOGIN_BUTTON = "Login";
    private static String LOGOUT_BUTTON = "Logout";
    private static String WORKSPACES_FIELD = "Select faraday workspace";
    private static String IMPORT_NEW_VULNS_FIELD = "Import new vulnerabilities";
    private static String IMPORT_BUTTON = "Import vulnerabilities";
    private static String REFRESH_BUTTON = "Refresh";
    private static String RESTORE_BUTTON = "Restore to defaults";
    private static String SAVE_BUTTON = "Save";

    private JTabbedPane tabbedPane;
    private JPanel authPanel;
    private JPanel configPanel;

    private JTextField fldUser;
    private JTextField fldPass;
    private JTextField fldServer;
    private JCheckBox cboxIgnoreSslErrors;

    private JComboBox<String> cmbWorkspaces;
    private JCheckBox cboxSetConfigDefault;


    private JButton loginButton;
    private JButton logoutButton;
    private JButton refreshButton;
    private JButton restoreButton;
    private JButton importButton;
    private JButton saveButton;
    private JButton closeButton;


    public ConfigurationDialog(String s) throws HeadlessException {
        super(s);
    }


    public void init() {
        logger.info("Init Faraday configuration dialog");
        // Setup the content-pane of JFrame in BorderLayout
        Container cp = this.getContentPane();
        cp.setLayout(new BorderLayout(5, 5));
        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);


        String USERNAME_FIELD = Constant.messages.getString("faraday.config.dialog.auth.user");
        String PASS_FIELD = Constant.messages.getString("faraday.config.dialog.auth.pass");
        String SERVER_FIELD = Constant.messages.getString("faraday.config.dialog.server");
        String IGNORE_SSL_ERRORS_CHECKBOX = Constant.messages.getString("faraday.config.dialog.ignoreSslErrors");
        LOGIN_BUTTON = Constant.messages.getString("faraday.config.dialog.auth.login");
        LOGOUT_BUTTON = Constant.messages.getString("faraday.config.dialog.auth.logout");
        WORKSPACES_FIELD = Constant.messages.getString("faraday.config.dialog.workspace");
        IMPORT_NEW_VULNS_FIELD = Constant.messages.getString("faraday.config.dialog.import.new");
        IMPORT_BUTTON = Constant.messages.getString("faraday.config.dialog.import.new");
        REFRESH_BUTTON = Constant.messages.getString("faraday.config.dialog.refresh");
        RESTORE_BUTTON = Constant.messages.getString("faraday.config.dialog.restore");
        SAVE_BUTTON = Constant.messages.getString("faraday.config.dialog.save");
        tabbedPane = new JTabbedPane();

        JPanel buttonLoginPanel = new JPanel();
        buttonLoginPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JPanel buttonConfigPanel = new JPanel();
        buttonConfigPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        authPanel = new JPanel(new GridLayout(4, 2, 10, 2));
        authPanel.setBorder(padding);
        configPanel = new JPanel(new GridLayout(3, 2, 10, 2));
        configPanel.setBorder(padding);
        Configuration configuration = Configuration.getSingleton();
        authPanel.add(new JLabel(USERNAME_FIELD));
        fldUser = new JTextField(10);
        fldUser.setText(configuration.getUser());
        authPanel.add(fldUser);

        authPanel.add(new JLabel(PASS_FIELD));
        fldPass = new JPasswordField(10);
        fldPass.setText(configuration.getPassword());
        authPanel.add(fldPass);

        authPanel.add(new JLabel(SERVER_FIELD));
        fldServer = new JTextField(10);
        fldServer.setText(configuration.getServer());
        authPanel.add(fldServer);

        cboxIgnoreSslErrors = new JCheckBox(IGNORE_SSL_ERRORS_CHECKBOX, configuration.isIgnoreSslErrors());
        authPanel.add(cboxIgnoreSslErrors);

        buttonConfigPanel.add(getCloseButton());
        buttonConfigPanel.add(getCloseButton());
        buttonConfigPanel.add(getRefreshButton());
        buttonConfigPanel.add(getRestoreButton());
        buttonConfigPanel.add(getSaveButton());
//        buttonConfigPanel.add(getImportButton());
        buttonConfigPanel.add(getLoginButton());
        buttonConfigPanel.add(getLogoutButton());


        authPanel.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {

            }

            @Override
            public void componentMoved(ComponentEvent componentEvent) {

            }

            @Override
            public void componentShown(ComponentEvent componentEvent) {

                refreshButton.setVisible(false);
//                importButton.setVisible(false);
                saveButton.setVisible(false);
            }

            @Override
            public void componentHidden(ComponentEvent componentEvent) {
                refreshButton.setVisible(true);
//                importButton.setVisible(true);
                saveButton.setVisible(true);
            }
        });

        configPanel.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {

            }

            @Override
            public void componentMoved(ComponentEvent componentEvent) {

            }

            @Override
            public void componentShown(ComponentEvent componentEvent) {
                loginButton.setVisible(false);
                logoutButton.setVisible(false);
                restoreButton.setVisible(false);
            }

            @Override
            public void componentHidden(ComponentEvent componentEvent) {
                restoreButton.setVisible(true);
                if (configuration.getSession().equals("")) {
                    loginButton.setVisible(true);
                } else {
                    logoutButton.setVisible(true);
                }
            }
        });

        tabbedPane.addTab(Constant.messages.getString("faraday.config.dialog.tab.auth"), null, authPanel, null);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);


        tabbedPane.addTab(Constant.messages.getString("faraday.config.dialog.tab.conf"), null, configPanel, null);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        tabbedPane.setEnabledAt(1, false);

        cp.add(tabbedPane, BorderLayout.NORTH);
        cp.add(buttonConfigPanel, BorderLayout.SOUTH);

        if (!configuration.getSession().equals("")) {
            logoutButton.setVisible(true);
            loginButton.setVisible(false);
            restoreButton.setVisible(true);
        } else {
            loginButton.setVisible(true);
            logoutButton.setVisible(false);
            restoreButton.setVisible(true);
        }

        fldUser.setText(configuration.getUser());
        fldPass.setText(configuration.getPassword());
        fldServer.setText(configuration.getServer());


        this.setSize(550, 300);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }


    private JButton getLoginButton() {
        if (this.loginButton == null) {
            this.loginButton = new JButton();
            this.loginButton.setText(LOGIN_BUTTON);
            this.loginButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (fldUser.getText().equals("") || fldPass.getText().equals("") || fldServer.getText().equals("")) {
                        showMessage(Constant.messages.getString("faraday.message.invalid.check.credentials"), Constant.messages.getString("faraday.dialog.login.title"), JOptionPane.ERROR_MESSAGE);
                    } else {
                        logger.info("Create Client: " + cboxIgnoreSslErrors.isSelected());
                        faradayClient = new FaradayClient(fldServer.getText(), cboxIgnoreSslErrors.isSelected());
                        if (faradayClient.Login(fldUser.getText(), fldPass.getText())) {
                            logoutButton.setVisible(true);
                            loginButton.setVisible(false);
                            restoreButton.setVisible(false);
                            if (!tabbedPane.isEnabledAt(1)) {
                                tabbedPane.setEnabledAt(1, true);
                            }
                            tabbedPane.setSelectedIndex(1);
                            if (cmbWorkspaces == null) {
                                configPanel.add(new JLabel(WORKSPACES_FIELD));
                                configPanel.add(getWSComboBox());
                            } else {
                                configPanel.remove(cmbWorkspaces);
                                configPanel.add(getWSComboBox());
                            }
                        } else {
                            showMessage(Constant.messages.getString("faraday.message.login_error"), Constant.messages.getString("faraday.dialog.login.title"), JOptionPane.ERROR_MESSAGE);
                        }
                    }


                }
            });


        }

        return this.loginButton;
    }


    private JButton getLogoutButton() {
        if (this.logoutButton == null) {
            this.logoutButton = new JButton();
            this.logoutButton.setText(LOGOUT_BUTTON);
            this.logoutButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Configuration configuration = Configuration.getSingleton();
                    String userTemp = configuration.getUser();
                    if (faradayClient.Logout()) {
                        fldUser.setText("");
                        fldPass.setText("");
                        logoutButton.setVisible(false);
                        loginButton.setVisible(true);
                        restoreButton.setVisible(true);

                        if (tabbedPane.isEnabledAt(1)) {
                            tabbedPane.setEnabledAt(1, false);
                        }
                        tabbedPane.setSelectedIndex(0);


                        showMessage(Constant.messages.getString("faraday.dialog.logout.success"), Constant.messages.getString("faraday.dialog.logout.title"), JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        showMessage(Constant.messages.getString("faraday.dialog.logout.error"), Constant.messages.getString("faraday.dialog.logout.title"), JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }

        return this.logoutButton;
    }


    private JButton getRefreshButton() {
        if (this.refreshButton == null) {
            this.refreshButton = new JButton();
            this.refreshButton.setText(REFRESH_BUTTON);
            this.refreshButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    refreshWorkspaces(true);
                }
            });
        }

        return this.refreshButton;
    }


    private JButton getCloseButton() {
        if (this.closeButton == null) {
            this.closeButton = new JButton();
            this.closeButton.setText(Constant.messages.getString("faraday.dialog.button.close"));
            this.closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });
        }
        return this.closeButton;
    }


    private JButton getRestoreButton() {
        if (this.restoreButton == null) {
            this.restoreButton = new JButton();
            this.restoreButton.setText(RESTORE_BUTTON);
            this.restoreButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    logger.info("Restore to defaults");
                    Configuration configuration = Configuration.getSingleton();
                    configuration.restoreDefaultConfiguration();
                    fldUser.setText(configuration.getUser());
                    fldPass.setText(configuration.getPassword());
                    fldServer.setText(configuration.getServer());

                }
            });
        }

        return this.restoreButton;
    }


    private JButton getImportButton() {
        if (this.importButton == null) {
            this.importButton = new JButton();
            this.importButton.setText(IMPORT_BUTTON);
            this.importButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                }
            });
        }

        return this.importButton;
    }


    private JButton getSaveButton() {
        if (this.saveButton == null) {
            this.saveButton = new JButton();
            this.saveButton.setText(SAVE_BUTTON);
            this.saveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    saveConfiguration();
                    setVisible(false);
                }
            });
        }
        return this.saveButton;
    }


    private JComboBox<String> getWSComboBox() {
        Configuration configuration = Configuration.getSingleton();

        ArrayList<String> wsList = faradayClient.GetWorkspaces();
        String[] workspaces = new String[wsList.size()];
        for (int i = 0; i < wsList.size(); i++) {
            workspaces[i] = wsList.get(i);
        }
        cmbWorkspaces = new JComboBox<String>(workspaces);
        if (workspaces.length > 0) {
            if (!configuration.getWorkspace().equals("")) {
                cmbWorkspaces.setSelectedItem(configuration.getWorkspace());
            } else {
                configuration.setWorkspace(workspaces[0]);
                logger.info("Set current workspace to: " + cmbWorkspaces.getSelectedItem().toString());

            }
        }
        cmbWorkspaces.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                logger.info("Set current workspace to: " + cmbWorkspaces.getSelectedItem().toString());
                configuration.setWorkspace(cmbWorkspaces.getSelectedItem().toString());
            }
        });


        return cmbWorkspaces;
    }



    private void showMessage(String message, String title, int icon) {
        JOptionPane.showMessageDialog(
                this,
                message,
                title,
                icon);
    }


    private void saveConfiguration() {
        if (Configuration.getSingleton().save()) {
            JOptionPane.showMessageDialog(
                    this,
                    Constant.messages.getString("faraday.save.config.success"),
                    Constant.messages.getString("faraday.config.dialog.title"),
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    Constant.messages.getString("faraday.save.config.error"),
                    Constant.messages.getString("faraday.config.dialog.title"),
                    JOptionPane.ERROR_MESSAGE);

        }
    }



    private void refreshWorkspaces(boolean canShowAlert) {
        if (cmbWorkspaces != null) {
            configPanel.remove(cmbWorkspaces);
            configPanel.add(getWSComboBox());
            if (canShowAlert) {
                JOptionPane.showMessageDialog(
                        this,
                        Constant.messages.getString("faraday.refresh.workspace.done"),
                        Constant.messages.getString("faraday.config.dialog.title"),
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

}
