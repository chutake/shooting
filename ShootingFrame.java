package shooting;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class ShootingFrame extends JFrame{
	public ShootingPanel panel;
	public ShootingFrame() {

		panel=new ShootingPanel();

		this.add(panel);


		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				Shooting.loop=true;
			}
		});


		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("Shooting");
		this.setSize(500, 500);
		this.setLocationRelativeTo(null);//中央に表示
		this.setResizable(false);//ウィンドウのサイズの変更不可
		this.setVisible(true);

		this.addKeyListener(new Keyboard());
	}
}
