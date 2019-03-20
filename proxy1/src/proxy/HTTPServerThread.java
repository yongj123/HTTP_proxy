package proxy;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

//本线程类用于 HTTP 代理中,从内网读数据,并发送给外网 

public class HTTPServerThread extends Thread {

	private Socket connection;

	public HTTPServerThread(Socket _connection) {

		connection = _connection;
		start();
	}

	public void run() { // 线程运行函数
		byte buf[] = new byte[10000], buf2[] = new byte[10000];

		int readbytes = 0, readbytes1 = 0;

		String s = null, s1 = null, s2 = null;
		Socket client = null;

		int port = 80;// 默认端口

		DataInputStream in = null, in1 = null;
		DataOutputStream out = null, out1 = null;
		int method = 0;

		try {
			in = new DataInputStream(connection.getInputStream());
			out = new DataOutputStream(connection.getOutputStream());

			if (in != null && out != null) {
				readbytes = in.read(buf, 0, 10000); // 从客户端读数据

				if (readbytes > 0) { // 读到数据
					s = new String(buf);
					System.out.println("request:\r\n" + s);
					if (s.contains("Accept-Encoding: gzip, deflate")) {// 禁止发送压缩请求
						s = s.replace("Accept-Encoding: gzip, deflate\r\n", "");
						// System.out.println("s2=" + s);
						buf = Arrays.copyOf(s.getBytes(), 10000);
						readbytes -= 32;
					}

					if (s.indexOf("\r\n") != -1)
						s = s.substring(0, s.indexOf("\r\n"));

					if (s.indexOf("GET") != -1) // 如读到 GET 请求
						method = 0;

					if (s.indexOf("CONNECT") != -1) {
						System.out.println("11111111111111111111111111111111");
						// 读到 CONNECT 请求,返回 HTTP应答
						s1 = s.substring(s.indexOf("CONNECT") + 8,
								s.indexOf("HTTP/"));
						s2 = s1;
						s1 = s1.substring(0, s1.indexOf(":"));
						s2 = s2.substring(s2.indexOf(":") + 1);
						s2 = s2.substring(0, s2.indexOf(" "));
						port = Integer.parseInt(s2);
						method = 1;

						s2 = "HTTP/1.0 200 Connection established\r\n";
						s2 = s2 + "Proxy-agent: proxy\r\n\r\n";
						buf2 = s2.getBytes();
						out.write(buf2);
						out.flush();
					}

					if (s.indexOf("POST") != -1)// 如读到 POST 请求
						method = 2;

					if (s.indexOf("http://") != -1 && s.indexOf("HTTP/") != -1) {
						// 从所读数据中取域名和端口号
						s1 = s.substring(s.indexOf("http://") + 7,
								s.indexOf("HTTP/"));

						s1 = s1.substring(0, s1.indexOf("/"));
						if (s1.indexOf(":") != -1) {
							s2 = s1;
							s1 = s1.substring(0, s1.indexOf(":"));
							s2 = s2.substring(s2.indexOf(":") + 1);
							port = Integer.parseInt(s2);
							// System.out.println("port=" + port);
							method = 0;
						}
					}
					if (FilterUtil.URLFilter.length() != 0
							&& s1.contains(FilterUtil.URLFilter)) {// 要拦截的URL
						String notFound = "HTTP/1.1 404 Not Found\r\n"
								+ "Server: Apache-Coyote/1.1\r\n"
								+ "Content-Type: text/html;charset=utf-8\r\n"
								+ "Content-Language: en\r\n"
								+ "Content-Length: 1011\r\n\r\n";
						out.write(notFound.getBytes());
						System.out.println("成功拦截:" + s1);
						s1 = null;
						out.flush();
					}
					if (s1 != null) {
						// System.out.println("域名：" + s1);
						// 根据读到的域名和端口号建立套接字
						client = new Socket(s1, port);
						client.setSoTimeout(80000);

						in1 = new DataInputStream(client.getInputStream());
						out1 = new DataOutputStream(client.getOutputStream());

						if (in1 != null && out1 != null && client != null) {
							if (method == 0) {
								// 如读到 GET 请求，向外网发出 GET 请求
								out1.write(buf, 0, readbytes);

								out1.flush();
								while (true) { // 循环
									try {
										if (readbytes1 == -1)// 无数据则退出循环
											break;
										// 从外网读数据,并返回给内网相应客户端
										readbytes1 = in1.read(buf, 0, 10000);

										if (readbytes1 > 0) {
											String ss = new String(buf, "utf-8")
													.substring(0, readbytes1);
											if (FilterUtil.SFilter.length() != 0
													&& ss.contains(FilterUtil.SFilter)) {
												ss = ss.replace(
														FilterUtil.SFilter,
														FilterUtil.Rep);
												buf = Arrays.copyOf(
														ss.getBytes(), 10000);
												readbytes1 -= FilterUtil
														.getDelta();
												System.out.println("过滤成功："
														+ FilterUtil.SFilter);
											}
											out.write(buf, 0, readbytes1);

											System.out.println("response1:\r\n"
													+ ss);
											out.flush();

										}

									} catch (Exception e) {// 异常则退出
										break;
									}
								}
							}

							if (method == 1) { // 如读到 CONNECT 请求
								// 建立线程,用于从外网读数据,并返回给内网客户端
								System.out
										.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
								HTTPServerThread1 thread1 = new HTTPServerThread1(
										in1, out);

								while (true) { // 循环
									try {
										if (readbytes1 == -1)// 无数据则退出循环
											break;
										readbytes1 = in.read(buf, 0, 10000);
										// 从内网读数据
										if (readbytes1 > 0) { // 读到数据,则发送给外网

											out1.write(buf, 0, readbytes1);

											out1.flush();
										}
									} catch (Exception e1) {
										break;
									}
								}
							}

							if (method == 2) { // 如读到 POST 请求

								// 向外网发送 POST 请求
								out1.write(buf, 0, readbytes);

								out1.flush();

								// 建立线程,用于从外网读数据,并返回给内网客户端

								HTTPServerThread1 thread1 = new HTTPServerThread1(
										in1, out);

								while (true) { // 循环
									try {
										if (readbytes1 == -1)// 无数据则退出循环
											break;
										readbytes1 = in.read(buf, 0, 10000);
										// 从内网读数据

										if (readbytes1 > 0) { // 读到数据,则发送给外网

											out1.write(buf, 0, readbytes1);

											out1.flush();
										}

									} catch (Exception e1) {
										break;
									}
								}
							}
						}

					}

				}
			}

			// 执行关闭操作
			if (in1 != null)
				in1.close();

			if (out1 != null)
				out1.close();
			if (client != null)
				client.close();
			if (in != null)
				in.close();

			if (out != null)
				out.close();

			if (connection != null)
				connection.close();
		} catch (IOException e) {
		}
	}
}