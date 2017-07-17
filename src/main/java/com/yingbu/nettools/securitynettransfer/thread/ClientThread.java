/**
 * GFW.Press
 * Copyright (C) 2016  chinashiyu ( chinashiyu@gfw.press ; http://gfw.press )
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/
package com.yingbu.nettools.securitynettransfer.thread;

import com.yingbu.nettools.securitynettransfer.util.ThreadPool;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Timestamp;

/**
 *
 * GFW.Press客户端线程
 *
 * @author chinashiyu ( chinashiyu@gfw.press ; http://gfw.press )
 *
 */
public class ClientThread implements PointThread {
    private String serverHost = null;
    private int serverPort = 0;
    private SecretKey key = null;
    private Socket agentSocket = null;
    private Socket serverSocket = null;
    private boolean forwarding = false;
    public ClientThread(Socket agentSocket, String serverHost, int serverPort, SecretKey key) {
        this.agentSocket = agentSocket;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.key = key;
    }

    /**
     * 打印信息
     *
     * @param o
     */
    private void log(Object o) {
        /*
        String time = (new Timestamp(System.currentTimeMillis())).toString().substring(0, 19);
        System.out.println("[" + time + "] " + o.toString());
        */
    }

    /**
     * 关闭所有连接，此线程及转发子线程调用
     */
    public synchronized void over() {
        try {
            serverSocket.close();
        } catch (Exception e) {
        }
        try {
            agentSocket.close();
        } catch (Exception e) {
        }
        if (forwarding) {
            forwarding = false;
            log("ClientThread: socket.closed and set forwarding = false.");
        }
    }

    /**
     * 启动客户端与服务器之间的转发线程，并对数据进行加密及解密
     */
    public void run() {
        log("ClientThread: run begin......");
        InputStream agentIn = null;
        OutputStream agentOut = null;
        InputStream serverIn = null;
        OutputStream serverOut = null;
        try {
            // 连接服务器
            serverSocket = new Socket(serverHost, serverPort);
            // set SoTimeout 30 Sec.
            serverSocket.setSoTimeout(30000);
            agentSocket.setSoTimeout(30000);
            // 打开 keep-alive
            serverSocket.setKeepAlive(true);
            agentSocket.setKeepAlive(true);
            // 获取输入输出流
            agentIn = agentSocket.getInputStream();
            agentOut = agentSocket.getOutputStream();
            serverIn = serverSocket.getInputStream();
            serverOut = serverSocket.getOutputStream();
        } catch (IOException ex) {
            log("连接服务器出错：" + serverHost + ":" + serverPort);
            over();
            return;
        }
        // 开始转发
        ThreadPool threadPool = ThreadPool.currentThreadPool();
        forwarding = true;
        EncryptForwardThread forwardServer = new EncryptForwardThread(this, agentIn, serverOut, key);
        threadPool.submitWorkThread(forwardServer);
        DecryptForwardThread forwardAgent = new DecryptForwardThread(this, serverIn, agentOut, key);
        threadPool.submitWorkThread(forwardAgent);
        log("ClientThread:run() finshed.");
    }
}
