package drive_control;

import hardware.Hardware;

public class BrightMeasure {
	public float measureBrightness() {

		return Hardware.getBrightness();
	}
	
	public float measureNormalizedBrightness(float White,float Black) { 
		//引数:白および黒の輝度値基準
		float brightness;
		brightness = Hardware.getBrightness();
		return (White - brightness)/(White - Black);	//白を1、黒を0として正規化
	}
}
