package nz.kapsy.okobotoke;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

public class RecordBar extends NormalCircle {
	
	

	private long totaltime;
	
	
	private float rec_left;
	private float rec_top;
	private float rec_right;
	private float rec_bottom;
	
	public float totalheight;
	public float totalwidth;
	
	private int frameinterval;//アニメーションの間隔
	private float incperframe;
		
	private FrameRecorder framerec;
	private MySurfaceView mysurfv;
	
	
	public RecordBar
		(float swidth, float sheight, long totaltime, int frameinterval, 
				FrameRecorder fr, MySurfaceView mysurfv) {
				
		Paint p = this.getPaint();
		
        p.setStyle(Paint.Style.FILL);
        //p.setStrokeWidth((float)(150 - rnd.nextInt(100)));
        p.setAntiAlias(false);
        p.setDither(false);
        
        this.totaltime = totaltime;
        this.frameinterval = frameinterval;
        
         
        this.totalwidth = swidth;
        this.totalheight = sheight;		
        
        this.calcIncPerFrame();
        
		this.framerec = fr;
		this.mysurfv = mysurfv;
		
		// if restoring after home button pressed
		if (!fr.isPlayingback() && !fr.isRecordingnow()) {
			this.setAlive(false);	
		}
		else {
			this.init();
			fr.startPlayBack();
		}
	}
	
	public void calcIncPerFrame() {
		this.incperframe = this.totalwidth / ((float)this.totaltime / (float)this.frameinterval);
	}
		
	//called when rec or play started
	@Override
	public void init() {
				
		rec_left = 0F;
		rec_top = totalheight - 6F;
		
		rec_right = 0F;
		rec_bottom = totalheight;
			
		
		if (this.framerec.isRecordingnow()) {
			this.setARGB(127, 255, 0, 0);
			
		}
		if (this.framerec.isPlayingback()) {
			this.setARGB(127, 0, 0, 255);
			
		}
		
		
		
		super.init();
		
		
	}

	
	public void startFrameRecorder() {
		
		this.framerec.startRecord();
		
	}
	
	

	
	@Override
	public void drawSequence(Canvas c) {
		if (this.isAlive()) {
			
			
		
			this.progressAnim();
			this.drawProgressBar(c);
		}
	}
	
	public void drawProgressBar(Canvas c) {
		
		
    	this.getPaint().setColor(Color.argb
    			(this.getAlpha(), this.getRed(), this.getGrn(), this.getBlu()));
    	
    	c.drawRect(this.rec_left, this.rec_top, this.rec_right, this.rec_bottom, this.getPaint());
    	
	}
	
	public void progressAnim() {
		// int cf = this.getCurrframe();

		if (this.rec_right == 0 && this.framerec.isRecordingnow()) {

			// this.mysurfv.setBackGrRed();
			this.mysurfv.recsymbol.init();
		}
		if (this.rec_right == this.incperframe) {

			// this.mysurfv.setBackGrBlack();
		}

		if (this.rec_right < this.totalwidth) {

			this.incBar();

		} else {

			if (this.framerec.isRecordingnow()) {

				// タッチ無効する
				this.mysurfv.setTouchenabled(false);
				this.mysurfv.releaseAllTouchAnims();
				// this.framerec.forceLastFrameOff();
				this.framerec.startPlayBack();
				
				//大きの方がいい
				this.mysurfv.playsymbol.init();
				
				//this.mysurfv.setBackGrBlue();
			}
			// 再生ループ
			if (this.framerec.isPlayingback()) {
				this.mysurfv.releaseAllTouchAnims();
				this.framerec.startPlayBack();
				this.mysurfv.playsymbol.init();
			}

			this.init();
		}

	}
	
	public void incBar() {
		
		this.rec_right = (this.rec_right + this.incperframe);
	}
	
	
	// fills all rec frames from current frame till the correct list array size
	// (only used when home button is pressed while recording)
	
	public void fillFramesEmpty() {

		if (this.framerec.isRecordingnow()) {
			int totalframes = 0;

			for (float f = 0F; f < this.totalwidth; f = f + this.incperframe) {
				totalframes++;
			}

			this.framerec.setMotionevent(MotionEvent.ACTION_CANCEL);
			this.framerec.setTouchpts(0);

			float c1_x = mysurfv.circtouchfirst[mysurfv.getCurcirctouchfirst()].getPosX();
			float c1_y = mysurfv.circtouchfirst[mysurfv.getCurcirctouchfirst()].getPosY();
			float c2_x = mysurfv.circtouchsecond[mysurfv.getCurcirctouchsecond()].getPosX();
			float c2_y = mysurfv.circtouchsecond[mysurfv.getCurcirctouchsecond()].getPosY();

			int remaining = totalframes - this.framerec.getCurrentframe();

			for (int i = 0; i < remaining; i++) {
				framerec.setFrame(false, c1_x, c1_y, false, c2_x, c2_y);
			}
		}

	}
	
	

}