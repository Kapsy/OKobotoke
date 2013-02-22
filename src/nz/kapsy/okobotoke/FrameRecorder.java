package nz.kapsy.okobotoke;

import java.util.ArrayList;

import android.util.Log;
import android.view.MotionEvent;

public class FrameRecorder {
	
	private boolean recordingnow;
	private boolean playingback;
		
	private int currentframe;
		
	private int motionevent = MotionEvent.ACTION_CANCEL;
		
	// values that could fall between frames are forced to the next frame
	private int[] mustrecvals = {MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN,
			MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_UP};
	private boolean mustreclastevent = false;
	private boolean mustrecactiondownfirst = false; // if true, an action down MUST be recorded that frame - two touch issue
	private int lastmustrec;
		
	private int touchpts = 0;
	
	private ArrayList<FrameRecUnit> recording = new ArrayList<FrameRecUnit>();
		
	//---===---===---===---===---===---
	
	
	public void startRecord() {
		
		if (!this.recording.isEmpty())
		{
			this.recording.clear();
		}
		
		this.setPlayingback(false);
		this.setRecordingnow(true);
		this.setTouchpts(0);
		this.setMotionevent(MotionEvent.ACTION_CANCEL);
		
	}
	
	public void startPlayBack() {

		// 強制的にタッチ処理を終了ってこと

		this.setRecordingnow(false);
		
		if (this.recording.size() > 0) {
			this.setPlayingback(true);
		}
		this.setCurrentframe(0);

	}
			
	public void setFrame(boolean cirtfirstisalive, float cirtfirstx, float cirtfirsty,
			boolean cirtsecondisalive, float cirtsecondx, float cirtsecondy) {
		
		if (this.isRecordingnow()) {
			
			
			this.recording.add(new FrameRecUnit(cirtfirstisalive, cirtfirstx, cirtfirsty, 
				cirtsecondisalive, cirtsecondx, cirtsecondy, this.motionevent, this.touchpts));
			
			// crucial values that could fall between frames are forced to the next frame
			if (this.mustreclastevent) {
			
				// solves two touch at same time issue
				// this could and should be done much better 
				//ACTION_DOWN should not be in mustrecvals, as it requires special treatment
				
				// 他のいい方法がきっとあるはず
				// mustrecvalsの中にACTION_DOWNないほうがいいかも。特別な値だから。
				if(this.mustrecactiondownfirst) {
					
					this.recording.get(this.recording.size() - 1).setMotionevent(MotionEvent.ACTION_DOWN);
					this.mustreclastevent = true;
					this.mustrecactiondownfirst = false;
				} 
				else if (this.lastmustrec != MotionEvent.ACTION_DOWN) {
					this.recording.get(this.recording.size() - 1).setMotionevent(this.lastmustrec);
					this.mustreclastevent = false;
				}
			}
			
/*			FrameRecUnit fl = this.recording.get(this.recording.size() - 1);
			
			Log.d("recording",
					"RECORDED FRAME"
					+ "\n" + "isCirtfirstisalive()" + fl.isCirtfirstisalive()
					+ "\n" + "getCirtfirstx " + fl.getCirtfirstx() 
					+ "\n" + "getCirtfirsty " +  fl.getCirtfirsty() 
					+ "\n" + "isCirtsecondisalive()" + fl.isCirtsecondisalive()
					+ "\n" + "getCirtsecondx " + fl.getCirtsecondx() 
					+ "\n" + "getCirtsecondy " + fl.getCirtsecondy() 
					+ "\n" + "getTouchpts " + fl.getTouchpts() 
					+ "\n" + "getMotionevent " + fl.getMotionevent()
					+ "\n" + "recording.size() " + recording.size());*/
					
			if (this.motionevent == MotionEvent.ACTION_UP) {
				this.motionevent = MotionEvent.ACTION_CANCEL;
			}
		}
					
	}
	
	public FrameRecUnit getPlaybackFrame() {
			
		FrameRecUnit f = recording.get(this.getCurrentframe());

/*			Log.d("recording", 
					"f.getMotionevent(): " + f.getMotionevent()
					+ "\n" + "f.getTouchpts(): " + f.getTouchpts()
					+ "\n" + "this.getCurrentframe(): " + this.getCurrentframe()
					+ "\n" + " ");
*/
		this.frameAdvance();
		
		return f;
				
	}
	
	
	//not needed?
	public void forceLastFrameOff () {
		
		FrameRecUnit f = this.recording.get(this.recording.size() - 1);
		
		f.setCirtfirstisalive(false);
		f.setCirtsecondisalive(false);
		f.setTouchpts(0);
		
		
	}
	

	public void frameAdvance() {
				
		if (this.getCurrentframe() < recording.size() - 1) {
			
			this.setCurrentframe(getCurrentframe() +1);
		}
		else {
			this.setCurrentframe(0);
			//this.setPlayingback(false);
		}
				
	}
	
	

	
	
	protected boolean isRecordingnow() {
		return recordingnow;
	}

	protected boolean isPlayingback() {
		return playingback;
	}

	protected void setPlayingback(boolean playingback) {
		this.playingback = playingback;
	}

	protected void setRecordingnow(boolean recordingnow) {
		this.recordingnow = recordingnow;
	}

	protected int getCurrentframe() {
		return currentframe;
	}

	protected void setCurrentframe(int currentframe) {
		this.currentframe = currentframe;
	}

	protected int getMotionevent() {
		return motionevent;
	}

	protected void setMotionevent(int motionevent) {
	
		if(this.isRecordingnow()) {
				
			//this.mustreclastevent = false;
				
			for(int i = 0; i < mustrecvals.length; i++) {
				
				if (motionevent == mustrecvals[i]) {
					
					if (motionevent == MotionEvent.ACTION_DOWN) {
						this.mustrecactiondownfirst = true;
					}
					
					this.mustreclastevent = true;
					this.lastmustrec = mustrecvals[i];
				
				}
			}
			
			this.motionevent = motionevent;
		
		}
	}

	protected int getTouchpts() {
		return touchpts;
	}

	protected void setTouchpts(int touchpts) {
		
		if(this.isRecordingnow()) {
			
			this.touchpts = touchpts;	
    	// Log.d("recording", "pts" + touchpts);
		}
	}



	
}