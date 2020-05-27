package faraday.src.main.java.org.zaproxy.zap.extension.faraday;

import org.apache.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.core.scanner.Alert;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.extension.alert.AlertNode;
import org.zaproxy.zap.extension.alert.PopupMenuItemAlert;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class PopupMenuItemSendAlert extends PopupMenuItemAlert {
    private static final Logger logger = Logger.getLogger(PopupMenuItemSendAlert.class);
    private FaradayClient faradayClient;
    private int selectionCount = 0;
    private int totalSelectionCount = 0;
    private boolean treeAlertParentSelected = false;

    public PopupMenuItemSendAlert(String label) {
        super(label, true);
        Configuration configuration = Configuration.getSingleton();
        boolean ignoreSSLErrors = true;
        faradayClient = new FaradayClient(configuration.getServer(), configuration.isIgnoreSslErrors());
    }

    @Override
    protected void performAction(Alert alert) {
        Configuration configuration = Configuration.getSingleton();
        String workspace = configuration.getWorkspace();
        String session = configuration.getSession();
        if (!workspace.equals("") && !session.equals("")) {
            int responseCode = faradayClient.AddVulnerability(alert, configuration.getWorkspace(), session);
            String message;
            int iconMessage = 1;
            switch (responseCode) {
                case 200:
                case 201:
                case 409:
                    message = Constant.messages.getString("faraday.send.alert.success");
                    break;
                case 403:
                    message = Constant.messages.getString("faraday.send.alert.permissions.error");
                    iconMessage = JOptionPane.WARNING_MESSAGE;
                    break;
//                case 409:
//                    message = Constant.messages.getString("faraday.send.alert.conflict");
//                    iconMessage = JOptionPane.WARNING_MESSAGE;
//                    break;
                case 400:
                case 500:
                    message = "Unable to send " + alert.getName() + " to Faraday";
                    iconMessage = JOptionPane.ERROR_MESSAGE;
                    break;

                default:
                    message = "Unable to send " + alert.getName() + " to Faraday";
                    iconMessage = JOptionPane.ERROR_MESSAGE;
                    break;
            }

            if (canShowMessageDialog()/*this.selectionCount == 1 && !treeAlertParentSelected*/) {
                JOptionPane.showMessageDialog(
                        this,
                        message,
                        Constant.messages.getString("faraday.button.send.alert"),
                        iconMessage);
            }


            logger.error(message);
            if (View.isInitialised()) {
                // Report info to the Output tab
                View.getSingleton().getOutputPanel().append(message + "\n");
            }


        } else {
            if (canShowMessageDialog()) {
                JOptionPane.showMessageDialog(
                        this,
                        Constant.messages.getString("faraday.send.alert.permissions.error"),
                        Constant.messages.getString("faraday.button.send.alert"),
                        JOptionPane.ERROR_MESSAGE);
                logger.error(Constant.messages.getString("faraday.send.alert.permissions.error"));
            }


            if (View.isInitialised()) {
                // Report info to the Output tab
                View.getSingleton().getOutputPanel().append(Constant.messages.getString("faraday.send.alert.permissions.error") + "\n");
            }
        }

    }

    @Override
    public boolean isEnableForComponent(Component invoker) {
        logger.info(invoker.getName());
        this.totalSelectionCount = 0;
        try {
            if (Configuration.getSingleton().getSession() == null || Configuration.getSingleton().getSession().equals("")) {
                return false;
            }
            treeAlertParentSelected = ((JTree) invoker).isRowSelected(0);
            if (super.isEnableForComponent(invoker) || treeAlertParentSelected) {
                this.selectionCount = ((JTree) invoker).getSelectionCount();
                for (int i = 0; i < ((JTree) invoker).getSelectionPaths().length; i++) {
                    AlertNode nodeTemp = (AlertNode) ((JTree) invoker).getSelectionPaths()[i].getLastPathComponent();
                    this.totalSelectionCount += getTotalAlertsToProcess(nodeTemp);
                }

                setEnabled(true);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }


    private int getTotalAlertsToProcess(AlertNode node) {
        if (node.getChildCount() > 0) {
            int total = 0;
            for (int i = 0; i < node.getChildCount(); i++) {
                total += getTotalAlertsToProcess(node.getChildAt(i));
            }
            return total;
        } else {
            return 1;
        }

    }

    private boolean canShowMessageDialog() {
        this.totalSelectionCount--;
        if (this.treeAlertParentSelected) {
            this.totalSelectionCount = 1;
            this.treeAlertParentSelected = false;
        }

        return this.totalSelectionCount == 0;
    }
}
