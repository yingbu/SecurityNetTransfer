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
 * GFW.Press服务器线程
 *
 * @author chinashiyu ( chinashiyu@gfw.press ; http://gfw.press )
 *
 */
public class ServerThread implements PointThread {

    private String proxyHost = null;

    private int proxyPort = 0;

    private Socket clientSocket = null;

    private Socket proxySocket = null;

    private SecretKey key = null;

    private boolean forwarding = false;

    public ServerThread(Socket clientSocket, String proxyHost, int proxyPort, SecretKey key) {

        this.clientSocket = clientSocket;

        this.proxyHost = proxyHost;

        this.proxyPort = proxyPort;

        this.key = key;

    }

    /**
     * 打印信息
     *
     * @param o
     */
    private void log(Object o) {

        String time = (new Timestamp(System.currentTimeMillis())).toString().substring(0, 19);

        System.out.println("[" + time + "] " + o.toString());

    }

    /**
     * 关闭所有连接，此线程及转发子线程调用
     */
    public synchronized void over() {

        try {

            proxySocket.close();

        } catch (Exception e) {

        }

        try {

            clientSocket.close();

        } catch (Exception e) {

        }

        if (forwarding) {

            forwarding = false;

        }

    }

    /**
     * 启动服务器与客户端之间的转发线程，并对数据进行加密及解密
     */
    public void run() {

        InputStream clientIn = null;

        OutputStream clientOut = null;

        InputStream proxyIn = null;

        OutputStream proxyOut = null;

        try {

            // 连接代理服务器
            proxySocket = new Socket(proxyHost, proxyPort);

            // 设置3分钟超时
            proxySocket.setSoTimeout(180000);
            clientSocket.setSoTimeout(180000);

            // 打开 keep-alive
            proxySocket.setKeepAlive(true);
            clientSocket.setKeepAlive(true);

            // 获取输入输出流
            clientIn = clientSocket.getInputStream();
            clientOut = clientSocket.getOutputStream();

            proxyIn = proxySocket.getInputStream();
            proxyOut = proxySocket.getOutputStream();

        } catch (IOException ex) {

            log("连接代理服务器出错：" + proxyHost + ":" + proxyPort);

            over();

            return;

        }

        // 开始转发
        forwarding = true;

        DecryptForwardThread forwardProxy = new DecryptForwardThread(this, clientIn, proxyOut, key);
        ThreadPool.currentThreadPool().submitWorkThread(forwardProxy);

        EncryptForwardThread forwardClient = new EncryptForwardThread(this, proxyIn, clientOut, key);
        ThreadPool.currentThreadPool().submitWorkThread(forwardClient);

    }

}
