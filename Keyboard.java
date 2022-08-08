package shooting;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Keyboard extends KeyAdapter{
	private static ArrayList<Integer> pressedButtons=new ArrayList<>();



	public static boolean isKeyPressed(int keyCode) {//押されたかの判定
		return pressedButtons.contains(keyCode);
	}

	@Override
	public void keyPressed(KeyEvent e) {//押した
		super.keyPressed(e);
		if(!pressedButtons.contains(e.getKeyCode())) pressedButtons.add(e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {//離す
		super.keyReleased(e);
		pressedButtons.remove((Integer)e.getKeyCode());
	}
}
