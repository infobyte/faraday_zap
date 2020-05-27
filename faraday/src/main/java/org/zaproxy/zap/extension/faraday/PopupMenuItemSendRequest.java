package faraday.src.main.java.org.zaproxy.zap.extension.faraday;

import org.apache.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.core.scanner.Alert;
import org.parosproxy.paros.db.DatabaseException;
import org.parosproxy.paros.db.RecordAlert;
import org.parosproxy.paros.model.HistoryReference;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.extension.alert.PopupMenuAlert;
import org.zaproxy.zap.view.messagecontainer.http.HttpMessageContainer;
import org.zaproxy.zap.view.popup.PopupMenuItemHistoryReferenceContainer;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("serial")
public class PopupMenuItemSendRequest extends PopupMenuItemHistoryReferenceContainer {
    private FaradayClient faradayClient;
    private int selectionCount = 0;
    private static final Logger logger = Logger.getLogger(PopupMenuItemSendRequest.class);


    public PopupMenuItemSendRequest(String label) {
        super(label, true);
        Configuration configuration = Configuration.getSingleton();
        logger.info("Sendo to: "+ configuration.getServer());
        faradayClient = new FaradayClient(configuration.getServer(), configuration.isIgnoreSslErrors());
    }

    @Override
    public void performAction(HistoryReference href) {
        try {
            Alert alert = new Alert(new RecordAlert(), href);
            alert.setName(href.getSiteNode().getName());
            alert.setUri(href.getURI().toString());
            alert.setMessage(href.getHttpMessage());
            alert.setDescription("");
            alert.setRiskConfidence(0, 0);

            Configuration configuration = Configuration.getSingleton();
            String workspace = configuration.getWorkspace();
            String session = configuration.getSession();
            if (!workspace.equals("") && !session.equals("")) {
                int responseCode = faradayClient.AddVulnerability(alert, configuration.getWorkspace(), session);
                String message = "";
                int iconMessage = 1;
                switch (responseCode) {
                    case 403:
                        message = Constant.messages.getString("faraday.send.alert.permissions.error");
                        iconMessage = JOptionPane.WARNING_MESSAGE;
                        break;
                    case 409:
                        message = Constant.messages.getString("faraday.send.request.conflict");
                        iconMessage = JOptionPane.WARNING_MESSAGE;
                        break;
                    case 500:
                        message = "Unable to send " + alert.getName() + " to Faraday";
                        iconMessage = JOptionPane.ERROR_MESSAGE;
                        break;
                    case 201:
                        message = Constant.messages.getString("faraday.send.request.success");
                        break;
                }

                if (this.selectionCount == 1) {
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
                JOptionPane.showMessageDialog(
                        this,
                        Constant.messages.getString("faraday.send.alert.permissions.error"),
                        Constant.messages.getString("faraday.button.send.request"),
                        JOptionPane.ERROR_MESSAGE);

                logger.error(Constant.messages.getString("faraday.send.alert.permissions.error"));
                if (View.isInitialised()) {
                    // Report info to the Output tab
                    View.getSingleton().getOutputPanel().append(Constant.messages.getString("faraday.send.alert.permissions.error") + "\n");
                }
            }


        } catch (HttpMalformedHeaderException e) {
            e.printStackTrace();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void performHistoryReferenceActions(List<HistoryReference> hrefs) {
        this.selectionCount = hrefs.size();

        for (HistoryReference href : hrefs) {
            this.performAction(href);
        }
    }

    @Override
    public boolean isEnableForInvoker(Invoker invoker, HttpMessageContainer httpMessageContainer) {
        if (Configuration.getSingleton().getSession() == null || Configuration.getSingleton().getSession().equals("") ||
                invoker.name().equals("ALERTS_PANEL")) {
            return false;
        }
        return super.isEnableForInvoker(invoker, httpMessageContainer);
    }

    @Override
    public boolean isButtonEnabledForHistoryReference(HistoryReference href) {
        if (Configuration.getSingleton().getSession() == null || Configuration.getSingleton().getSession().equals("")) {
            return false;
        }

        return href.getSiteNode() != null && super.isButtonEnabledForHistoryReference(href);
    }
}