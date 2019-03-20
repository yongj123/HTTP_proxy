package proxy;

import java.net.ServerSocket;
import java.net.Socket;

//本线程类用于 HTTP 代理中,侦听客户端连接请求,并建立服务线程

public class HTTPdaemon extends Thread {
	private ServerSocket server;

	public HTTPdaemon(ServerSocket _server) {
		server = _server;
		start();
	}

	public void run() { // 线程运行函数
		Socket connection;
		while (true) {
			try {
				connection = server.accept();
				HTTPServerThread handler = new HTTPServerThread(connection);
			} catch (Exception e) {
			}
		}
	}
}
