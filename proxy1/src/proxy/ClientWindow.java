package proxy;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.ServerSocket;

public class ClientWindow extends JFrame {
	private JTextField txtPort;
	private JTextField txtKeyWords;
	private JTextField txtURL;
	private JTextField txtRep;
	ServerSocket httpserver = null;
	HTTPdaemon httpproxy = null;

	public ClientWindow() {
		setTitle("MyHTTPProxy1.0");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 434, 261);
		panel.setLayout(null);

		JLabel label = new JLabel("代理服务器端口：");
		label.setFont(new Font("宋体", Font.PLAIN, 14));
		label.setBounds(64, 34, 112, 30);
		panel.add(label);

		txtPort = new JTextField();
		txtPort.setFont(new Font("宋体", Font.PLAIN, 14));
		txtPort.setBounds(196, 34, 62, 30);
		panel.add(txtPort);
		txtPort.setColumns(10);

		final JLabel lbMsg = new JLabel();
		lbMsg.setBounds(164, 74, 193, 15);
		lbMsg.setForeground(Color.RED);
		panel.add(lbMsg);

		final JButton button = new JButton("启动");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {

					if (button.getText().equals("启动")) {
						FilterUtil.setURLFilter(txtURL.getText().trim());
						FilterUtil.setSFilter(txtKeyWords.getText().trim());
						FilterUtil.setRep(txtRep.getText().trim());

						// 建立 HTTP 侦听套接字
						httpserver = new ServerSocket(Integer.parseInt(txtPort
								.getText()));
						System.out.println("HTTP Proxy started on "
								+ httpserver.getLocalPort());
						// 建立 HTTP 侦听线程
						httpproxy = new HTTPdaemon(httpserver);
						button.setText("关闭");
						txtPort.setEditable(false);
						txtKeyWords.setEditable(false);
						txtURL.setEditable(false);
						txtRep.setEditable(false);
						lbMsg.setText("HTTP Proxy started on:"
								+ txtPort.getText());
					} else {
						// httpproxy.stop();
						httpserver.close();
						lbMsg.setText("");
						txtPort.setEditable(true);
						txtKeyWords.setEditable(true);
						txtURL.setEditable(true);
						txtRep.setEditable(true);
						button.setText("启动");
						System.out.println("HTTP Proxy closed.");
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(ClientWindow.this,
							"请输入合法的端口号！");
				}
			}
		});
		button.setFont(new Font("宋体", Font.PLAIN, 14));
		button.setBounds(295, 33, 62, 30);
		panel.add(button);

		JLabel lblUrl = new JLabel("URL拦截：");
		lblUrl.setFont(new Font("宋体", Font.PLAIN, 14));
		lblUrl.setBounds(64, 99, 73, 30);
		panel.add(lblUrl);

		JLabel label_1 = new JLabel("关键字过滤：");
		label_1.setFont(new Font("宋体", Font.PLAIN, 14));
		label_1.setBounds(64, 153, 90, 30);
		panel.add(label_1);

		txtKeyWords = new JTextField();
		txtKeyWords.setFont(new Font("宋体", Font.PLAIN, 14));
		txtKeyWords.setBounds(164, 154, 193, 30);
		panel.add(txtKeyWords);
		txtKeyWords.setColumns(10);

		txtURL = new JTextField();
		txtURL.setFont(new Font("宋体", Font.PLAIN, 14));
		txtURL.setColumns(10);
		txtURL.setBounds(164, 100, 193, 30);
		panel.add(txtURL);

		JLabel label_2 = new JLabel("替换为：");
		label_2.setFont(new Font("宋体", Font.PLAIN, 14));
		label_2.setBounds(64, 209, 90, 30);
		panel.add(label_2);

		txtRep = new JTextField("哈哈哈");
		txtRep.setFont(new Font("宋体", Font.PLAIN, 14));
		txtRep.setColumns(10);
		txtRep.setBounds(164, 209, 193, 30);
		panel.add(txtRep);

		add(panel);

	}
}
