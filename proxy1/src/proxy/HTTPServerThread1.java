package proxy;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.util.Arrays;

//本线程类用于 HTTP 代理中,从外网读数据,并发送给内网客户端

public class HTTPServerThread1 extends Thread {
	private DataInputStream in; // 读数据
	private DataOutputStream out; // 写数据

	public HTTPServerThread1(DataInputStream _in, DataOutputStream _out) {
		in = _in;
		out = _out;
		start();
	}

	public void run() { // 线程运行函数,循环读取返回数据,并发送给相关客户端
		int readbytes = 0;
		byte buf[] = new byte[10000];
		while (true) { // 循环
			try {
				if (readbytes == -1)
					break; // 无数据则退出循环
				readbytes = in.read(buf, 0, 10000);
				if (readbytes > 0) {
					String ss = new String(buf, "utf-8")
							.substring(0, readbytes);
					if (FilterUtil.SFilter.length() != 0
							&& ss.contains(FilterUtil.SFilter)) {
						ss = ss.replace(FilterUtil.SFilter, FilterUtil.Rep);
						buf = Arrays.copyOf(ss.getBytes(), 10000);
						readbytes -= FilterUtil.getDelta();
						System.out.println("过滤成功：" + FilterUtil.SFilter);
					}
					out.write(buf, 0, readbytes);
					System.out.println("response2:\r\n" + ss);

					out.flush();
				}
			} catch (Exception e) {// 异常则退出循环
				break;
			}
		}
	}
}
