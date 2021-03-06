package starter;

import hardware.Hardware;

import java.util.Timer;
import java.util.TimerTask;

import lejos.hardware.Battery;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.BasicMotorPort;
import lejos.utility.Delay;
import motor_control.tailCtrl;
import Balancer.Balancer;

public class Starter {

	StartCommandDetecter scd = new StartCommandDetecter();
	tailCtrl tail = new tailCtrl();

	public void start(){

		init();

		Timer CommandTimer = new Timer();
		TimerTask CommandTask = new TimerTask(){

			public void run(){
				scd.esta();
			}
		};

		CommandTimer.scheduleAtFixedRate(CommandTask, 0, 20);

		while(true){

			if(scd.checkCommand() == true){
				break;
			}

			tail.tailThree();

			Delay.msDelay(20);
		}

		CommandTimer.cancel();

		while(true){

			if(Hardware.motorPortT.getTachoCount() >= 96){
				tail.tailTwo();
				break;
			}

			tail.tailStart();
		}
	}

	static void init(){
		LCD.drawString("Please Wait...  ", 0, 4);
		Hardware.gyro.reset();
	    Hardware.sonar.enable();
	    Hardware.motorPortL.setPWMMode(BasicMotorPort.PWM_BRAKE);
	    Hardware.motorPortR.setPWMMode(BasicMotorPort.PWM_BRAKE);
	    Hardware.motorPortT.setPWMMode(BasicMotorPort.PWM_BRAKE);

	    // Java の初期実行性能が悪く、倒立振子に十分なリアルタイム性が得られない。
	    // 走行によく使うメソッドについて、HotSpot がネイティブコードに変換するまで空実行する。
	    // HotSpot が起きるデフォルトの実行回数は 1500。
	    for (int i=0; i < 1500; i++) {
	        Hardware.motorPortL.controlMotor(0, 0);
	        Hardware.getBrightness();
	        Hardware.getSonarDistance();
	        Hardware.getGyroValue();
	        Battery.getVoltageMilliVolt();
	        Balancer.control(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8000);
	    }
	    Delay.msDelay(10000);       // 別スレッドで HotSpot が完了するだろう時間まで待つ。

	    Hardware.motorPortL.controlMotor(0, 0);
	    Hardware.motorPortR.controlMotor(0, 0);
	    Hardware.motorPortT.controlMotor(0, 0);
	    Hardware.motorPortL.resetTachoCount();   // 左モータエンコーダリセット
	    Hardware.motorPortR.resetTachoCount();   // 右モータエンコーダリセット
	    Hardware.motorPortT.resetTachoCount();   // 尻尾モータエンコーダリセット
	    Balancer.init();            // 倒立振子制御初期化
	    LCD.clear();
	}

}
