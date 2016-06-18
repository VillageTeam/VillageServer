/*
 * Copyright © 2016 | Time of Kings (ToK) Team | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.github.ToK.ToKserver.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.ToK.ToKserver.configuration.ConfigurationManager;

/**
 * @author DeathsGun
 * @date Jun 12, 2016
 *
 */
public class TelnetServer {

    private final Logger logger = Logger.getLogger(TelnetServer.class.getName());
    private ServerSocket server = null;
    private final ExecutorService executor = Executors
            .newFixedThreadPool(ConfigurationManager.INSTANCE.getMaxThreads());
    private final String workingDir = System.getProperty("user.dir");

    private final int GIVEN_PORT;

    /**
     * @param string
     */
    public TelnetServer(String port) {

        GIVEN_PORT = port != null ? Integer.valueOf(port).intValue() : 0;
    }

    /**
     * The main method to start the telnet server
     */
    public void run() {

        try {
            // establish a connection
            server = new ServerSocket(GIVEN_PORT == 0 ? ConfigurationManager.INSTANCE.getPort() : GIVEN_PORT);
            logger.info("Server running and listening on port : "
                    + (GIVEN_PORT == 0 ? ConfigurationManager.INSTANCE.getPort() : GIVEN_PORT));

            while (true) {
                Socket s = server.accept();
                executor.execute(new ClientWorker(s, workingDir));
            }

        } catch (IOException e) {
            logger.log(Level.WARNING, "Shutting down the server..");
        } finally {
            executor.shutdown();
        }

    }

    /**
     * Checks if the server is running.
     * 
     * @return
     */
    public boolean isRunning() {
        return !server.isClosed();
    }

    /**
     * Shutdowns all the connection and the server
     * 
     * @throws IOException
     */
    public void shutDown() throws IOException {
        if (server != null) {

            server.close();

        }

    }
}