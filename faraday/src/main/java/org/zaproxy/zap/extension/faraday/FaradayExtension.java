/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.zap.extension.faraday;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.control.Control;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.parosproxy.paros.extension.ExtensionPopupMenuItem;
import org.zaproxy.zap.view.ZapMenuItem;

import javax.swing.KeyStroke;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
//import java.io.*;

public class FaradayExtension extends ExtensionAdaptor {
    public static final String NAME = "Faraday Extension";
    private static final Logger logger = LogManager.getLogger(FaradayExtension.class);
    private ZapMenuItem menuItemFaradayConfig;
    private ConfigurationDialog configurationDialog;
    private PopupMenuItemSendAlert popupMenuItemSendAlert;
    private PopupMenuItemSendRequest popupMenuItemSendRequest;



    public FaradayExtension(String name) {
        super(name);
    }


    public FaradayExtension() {
        super(NAME);
    }

    @Override
    public void init() {
        logger.info("Init Extension");
        super.init();
        initialize();
    }

    private void initialize() {
        this.setName(Constant.messages.getString("faraday.extension.name"));
        Configuration configuration =  this.initConfiguration();
        if (configuration.getUser().equals("") && configuration.getPassword().equals("") && configuration.getServer().equals(""))
        {
            logger.info("Try to login to faraday with stored credentials");
        }

        }



    @Override
    public String getAuthor() {
        return Constant.messages.getString("faraday.extension.author");
    }

    @Override
    public void hook(ExtensionHook extensionHook) {
        super.hook(extensionHook);

        if (getView() != null) {
            extensionHook.getHookMenu().addToolsMenuItem(getMenuItemFaradayConfig());
            extensionHook.getHookMenu().addPopupMenuItem(this.getPopupMenuItem());
            extensionHook.getHookMenu().addPopupMenuItem(this.getPopupMenuItemRequest());
        }
    }

    @Override
    public boolean canUnload() {
        return true;
    }

    @Override
    public String getDescription() {
        return Constant.messages.getString("faraday.extension.description");
    }

    private ZapMenuItem getMenuItemFaradayConfig() {
        if (menuItemFaradayConfig == null) {
            menuItemFaradayConfig = new ZapMenuItem(
                    "faraday.menu.tools.label",
                    KeyStroke.getKeyStroke(
                            KeyEvent.VK_F,
                            Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | KeyEvent.ALT_DOWN_MASK,
                            false));
            menuItemFaradayConfig.setEnabled(Control.getSingleton().getMode() != Control.Mode.safe);

            menuItemFaradayConfig.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showConfigurationDialog();
                }
            });
        }
        return menuItemFaradayConfig;
    }


    private void showConfigurationDialog() {
        if (configurationDialog == null) {
            logger.info("Create configuration dialog");
            configurationDialog = new ConfigurationDialog(Constant.messages.getString("faraday.config.dialog.title"));
            configurationDialog.init();
        }
        configurationDialog.setVisible(true);
    }


    private ExtensionPopupMenuItem getPopupMenuItem() {
        if (popupMenuItemSendAlert == null) {
            popupMenuItemSendAlert = new PopupMenuItemSendAlert(Constant.messages.getString("faraday.button.send.alert"));
        }

        return popupMenuItemSendAlert;

    }


    private ExtensionPopupMenuItem getPopupMenuItemRequest() {
        if (popupMenuItemSendRequest == null) {
            popupMenuItemSendRequest = new PopupMenuItemSendRequest(Constant.messages.getString("faraday.button.send.request"));
        }

        return popupMenuItemSendRequest;

    }


    private Configuration initConfiguration() {
        Configuration configuration = Configuration.getSingleton();
        return configuration;

    }

}
