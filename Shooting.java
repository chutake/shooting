package shooting;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.ImageIcon;

public class Shooting {
	public static ShootingFrame shootingFrame;
	public static boolean loop;

	public static void main(String[] args) throws IOException {
		shootingFrame=new ShootingFrame();
		loop=true;

		Graphics gra=shootingFrame.panel.image.getGraphics();

		//FPS
		//処理時間と待機時間を合わせて33msにする->30FPS
		long startTime;
		long fpsTime=0;
		int fps=30;
		int FPS=0;
		int FPSCount=0;

		EnumShootingScreen screen=EnumShootingScreen.START;//列挙型

		//GAME
		int playerX=0,playerY=0;
		int bulletInterval=0;
		int score=0;
		int level=0;
		long levelTimer=0;
		ArrayList<Bullet> bullets_player=new ArrayList<>();
		ArrayList<Bullet> bullets_enemy=new ArrayList<>();
		ArrayList<Enemy> enemies=new ArrayList<>();
		Random random=new Random();
		ImageIcon imgicon= new ImageIcon("src/shooting/background.jpg");
		Image img=imgicon.getImage();
		File bgm=new File("src/shooting/bgm.wav"),shootsound=new File("src/shooting/shoot.wav")
				,resultsound=new File("src/shooting/result.wav")
				,downsound=new File("src/shooting/enemydown.wav");
		AudioInputStream stream;
		AudioFormat format;
		DataLine.Info info;
		Clip clipbgm = null,clip1=null,clip2=null,clip3=null;

		//フォント(cursive,SansSerif)


		while(loop) {
			if((System.currentTimeMillis()-fpsTime)>=1000) {
				fpsTime=System.currentTimeMillis();
				FPS=FPSCount;
				FPSCount=0;
				//System.out.println(FPS);
			}
			FPSCount++;
			startTime=System.currentTimeMillis();
			/*
			gra.setColor(Color.white);//背景色
			gra.fillRect(0, 0, 500, 500);
			*/

			switch(screen){
				case START:
					/*
					gra.setColor(Color.white);//背景色
					gra.fillRect(0, 0, 500, 500);
					*/
					gra.clearRect(0, 0, 500, 500);
					gra.drawImage(img, 0, 0, null);

					gra.setColor(Color.black);
					Font font =new Font("cursive",Font.BOLD,50);
					gra.setFont(font);
					FontMetrics metrics=gra.getFontMetrics(font);
					gra.drawString("Shooting Game", 250-(metrics.stringWidth("Shooting Game")/2), 200);
					font=new Font("cursive",Font.BOLD,20);
					gra.setFont(font);
					metrics=gra.getFontMetrics(font);
					gra.drawString("Press SPACE to start", 250-(metrics.stringWidth("Press SPACE to start")/2), 300);
					//"Shooting"に必要なpxcelを出す。中央に表示）
					if(Keyboard.isKeyPressed(KeyEvent.VK_SPACE)) {
						screen=EnumShootingScreen.GAME;//画面遷移
						//ゲーム開始時に初期化
						bullets_player=new ArrayList<>();
						bullets_enemy=new ArrayList<>();
						enemies=new ArrayList<>();
						playerX=235;
						playerY=430;
						score=0;//リセット
						level=0;
						try {
							//BGM
							stream=AudioSystem.getAudioInputStream(bgm);
							format=stream.getFormat();
							info=new DataLine.Info(Clip.class, format);
							clipbgm=(Clip)AudioSystem.getLine(info);
							clipbgm.open(stream);
							clipbgm.start();
						}catch(Exception e) {
							//
						}
					}
					break;
				case GAME:
					//背景設定
					gra.clearRect(0, 0, 500, 500);
					gra.drawImage(img, 0, 0, null);

					//ゲームのレベル
					if(System.currentTimeMillis()-levelTimer>10*1000) {//10秒でレベルが1あがる
						levelTimer=System.currentTimeMillis();
						level++;
					}

					//player
					gra.setColor(Color.WHITE);
					gra.fillRect(playerX+10, playerY, 10, 10);
					gra.fillRect(playerX, playerY+10, 30, 10);

					//自分の弾
					for(int i=0;i<bullets_player.size();i++) {
						Bullet bullet=bullets_player.get(i);
						gra.fillRect(bullet.x, bullet.y, 5, 5);
						bullet.y-=10;
						if(bullet.y<0) {
							bullets_player.remove(i);
							i--;//removeした分iを減らす
						}

						for(int j=0;j<enemies.size();j++) {
							Enemy enemy=enemies.get(j);
							if(bullet.x>=enemy.x && bullet.x<=enemy.x+30 &&
									bullet.y>=enemy.y && bullet.y<=enemy.y+20) {
								enemies.remove(j);
								bullets_player.remove(i);
								score+=100;
								try {
									//効果音（倒した）
									stream=AudioSystem.getAudioInputStream(downsound);
									format=stream.getFormat();
									info=new DataLine.Info(Clip.class, format);
									clip3=(Clip)AudioSystem.getLine(info);
									clip3.open(stream);
									clip3.start();
								}catch(Exception e) {
									//
								}
							}
						}
					}


					//敵
					gra.setColor(Color.magenta);
					for(int i=0;i<enemies.size();i++) {
						Enemy enemy=enemies.get(i);
						gra.fillRect(enemy.x, enemy.y,30,10);
						gra.fillRect(enemy.x+10, enemy.y+10,10,10);
						enemy.y=enemy.y+2+level;//敵の動く速さ
						if(enemy.y>500) {
							enemies.remove(i);
							i--;
						}
						if(random.nextInt(level<20?80-level*3:20)==1)bullets_enemy.add(new Bullet(enemy.x,enemy.y));//敵の攻撃確率
						//playerと敵の衝突判定
						if((enemy.x>=playerX && enemy.x<=playerX+30 &&
								enemy.y>=playerY&&enemy.y<=playerY+20)||
								(enemy.x+30>=playerX && enemy.x+30<=playerX+30 &&
								enemy.y+20>=playerY && enemy.y+20<=playerY+20)) {
							screen=EnumShootingScreen.GAME_OVER;
							clipbgm.close();
							try {
								//効果音（結果）
								stream=AudioSystem.getAudioInputStream(resultsound);
								format=stream.getFormat();
								info=new DataLine.Info(Clip.class, format);
								clip2=(Clip)AudioSystem.getLine(info);
								clip2.open(stream);
								clip2.start();
							}catch(Exception e) {
								//
							}
						}
					}

					if(random.nextInt(level<10?30-level:10)==1)enemies.add(new Enemy(random.nextInt(470),0));//敵の出現率

					//敵の弾
					for(int i=0;i<bullets_enemy.size();i++) {
						Bullet bullet=bullets_enemy.get(i);
						gra.setColor(Color.magenta);
						gra.fillRect(bullet.x, bullet.y, 5, 5);
						if(level>3) {
							bullet.y=bullet.y+10+level-3;
						}else {
							bullet.y+=10;
						}
						if(bullet.y>500) {
							bullets_enemy.remove(i);
							i--;//removeした分iを減らす
						}
						//playerと敵の弾の衝突判定
						if(bullet.x>=playerX && bullet.x<=playerX+30 &&
								bullet.y>=playerY&&bullet.y<=playerY+20) {
							screen=EnumShootingScreen.GAME_OVER;
							clipbgm.close();
							try {
								//効果音(結果)
								stream=AudioSystem.getAudioInputStream(resultsound);
								format=stream.getFormat();
								info=new DataLine.Info(Clip.class, format);
								clip2=(Clip)AudioSystem.getLine(info);
								clip2.open(stream);
								clip2.start();
							}catch(Exception e) {
								//
							}
						}
					}

					if(Keyboard.isKeyPressed(KeyEvent.VK_LEFT)&&playerX>0)playerX-=8;
					if(Keyboard.isKeyPressed(KeyEvent.VK_RIGHT)&&playerX<470)playerX+=8;
					if(Keyboard.isKeyPressed(KeyEvent.VK_UP)&&playerY>30)playerY-=8;
					if(Keyboard.isKeyPressed(KeyEvent.VK_DOWN)&&playerY<450)playerY+=8;

					if(Keyboard.isKeyPressed(KeyEvent.VK_SPACE)&&bulletInterval==0) {
						bullets_player.add(new Bullet(playerX+12,playerY));
						bulletInterval=5;//弾の撃つ速さ
						score-=10;
						try {//効果音（発射音）
							stream=AudioSystem.getAudioInputStream(shootsound);
							format=stream.getFormat();
							info=new DataLine.Info(Clip.class, format);
							clip1=(Clip)AudioSystem.getLine(info);
							clip1.open(stream);//発射音
							clip1.start();
						}catch(Exception e) {
							//
						}
					}
					if(bulletInterval>0)bulletInterval--;

					gra.setColor(Color.BLACK);
					font=new Font("cursive",Font.BOLD,20);
					metrics=gra.getFontMetrics(font);
					gra.setFont(font);
					gra.drawString("SCORE:"+score, 470-metrics.stringWidth("SCORE:"+score), 430);
					gra.drawString("Level:"+level, 470-metrics.stringWidth("Level:"+level), 450);
					break;
				case GAME_OVER:
					gra.setColor(Color.white);//背景色
					gra.fillRect(0, 0, 500, 500);

					gra.setColor(Color.black);
					font =new Font("cursive",Font.BOLD,50);
					gra.setFont(font);
					metrics=gra.getFontMetrics(font);
					gra.drawString("Game Over", 250-(metrics.stringWidth("game Over")/2), 100);
					font =new Font("cursive",Font.BOLD,20);
					gra.setFont(font);
					metrics=gra.getFontMetrics(font);
					gra.drawString("Score increases with Level!", 250-metrics.stringWidth("Score increases with Level!")/2, 250);
					gra.drawString("Score: "+score+" , Level: "+level, 250-(metrics.stringWidth("Score: "+score+" , Level: "+level)/2), 300);
					gra.drawString("All Score:"+(score+level*100), 250-metrics.stringWidth("All Score:"+(score+level*100))/2, 350);
					gra.drawString("Press ESC to Retry", 250-(metrics.stringWidth("Press ESC to Retry")/2), 400);
					if(Keyboard.isKeyPressed(KeyEvent.VK_ESCAPE)) {
						screen=EnumShootingScreen.START;
						clipbgm.close();
					}
					break;
			}


			gra.setColor(Color.BLACK);
			gra.setFont(new Font("SansSerif",Font.BOLD,15));
			gra.drawString(FPS+"FPS", 10, 450);

			shootingFrame.panel.draw();

			try {
				long runTime=System.currentTimeMillis()-startTime;
				if(runTime<=(1000/fps)) {
					Thread.sleep((1000/fps)-(runTime));
				}
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
