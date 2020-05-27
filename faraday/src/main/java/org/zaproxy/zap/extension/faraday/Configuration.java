/*
 *  Zed Attack Proxy (ZAP) and its related class files.
 *
 *  ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 *  Copyright 2018 The ZAP Development Team
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package faraday.src.main.java.org.zaproxy.zap.extension.faraday;
import org.apache.log4j.Logger;
import org.parosproxy.paros.Constant;

import javax.swing.*;
import java.io.*;
import java.util.Properties;

public class Configuration {
    private String server;
    private String user;
    private String password;
    private String session;
    private String workspace;
    private boolean autoImport;
    private boolean ignoreSslErrors;
    private static Configuration _instance;
    private static final Logger logger = Logger.getLogger(Configuration.class);
    private final File configurationFile;

    private Configuration() {
        logger.info("Init Configuration");
        this.user = "";
        this.password = "";
        this.server = "http://127.0.0.1:5985/";
        this.autoImport = false;
        this.ignoreSslErrors = false;
        this.session = "";
        this.workspace = "";
        String userHome = System.getProperty("user.home");
        String outputFolder = Constant.getZapHome() + "faraday";
        File folder = new File(outputFolder);
        if (!folder.exists()) {
            folder.mkdir();
        }
        this.configurationFile = new File(outputFolder + File.separator + "faraday.properties");
        if (! this.configurationFile.exists()){
            logger.info("Configuration file dont exists, generate default");
            save();
        } else
        {
            this.load();
        }


    }

    public static Configuration getSingleton() {
        if (_instance == null)
            _instance = new Configuration();
        return _instance;
    }

    private void load(){
        if (this.configurationFile.exists()){
            try {
                Properties prop = new Properties();
                InputStream input = null;
                input = new FileInputStream(this.configurationFile.getPath());
                prop.load(input);
                this.setUser(prop.getProperty("fuser"));
                this.setPassword(prop.getProperty("fpassword"));
                this.setServer(prop.getProperty("fserver"));
                this.setWorkspace(prop.getProperty("fworkspace"));
                this.setIgnoreSslErrors(prop.getProperty("fignore_ssl_errors").equals("1"));
                input.close();
            } catch (Exception e){
                logger.error(e);
            }

        } else {
            logger.error("Configuration file missing");
        }

    }

    public boolean save(){
        try{
            Properties prop = new Properties();
            OutputStream output = null;
            output = new FileOutputStream(configurationFile.getPath());
            // set the properties value
            prop.setProperty("fuser", this.getUser());
            prop.setProperty("fpassword", this.getPassword());
            prop.setProperty("fserver", this.getServer());
            prop.setProperty("fworkspace", this.getWorkspace());
            prop.setProperty("fignore_ssl_errors", this.isIgnoreSslErrors() ? "1" : "0");
            // save properties to project root folder
            prop.store(output, null);
            logger.info("Configuration saved");

        } catch (Exception e){
            logger.error("Error saving configuration: "+ e);
        }
        return true;
    }


    public void restoreDefaultConfiguration() {
        logger.info("Restore default configuration");
        this.setUser("");
        this.setPassword("");
        this.setServer("http://127.0.0.1:5985/");
        this.setWorkspace("");
        this.setIgnoreSslErrors(false);
        this.session = "";
        this.save();

    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public boolean isAutoImport() {
        return autoImport;
    }

    public void setAutoImport(boolean autoImport) {
        this.autoImport = autoImport;
    }

    public boolean isIgnoreSslErrors() {
        return ignoreSslErrors;
    }

    public void setIgnoreSslErrors(boolean ignoreSslErrors) {
        this.ignoreSslErrors = ignoreSslErrors;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }
}
