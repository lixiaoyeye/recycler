package health.rubbish.recycler.util;

import java.io.IOException;

import com.example.mt5000_jar_demo.R;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Vibrator;
import android.util.Log;

public class BeepManager {

	// 常量
	private final float BEEP_VOLUME = 0.3f;
	private final long VIBRATE_DURATION = 200L;

	// 变量
	private boolean playBeep = false;
	private boolean vibrate = false;

	// 控制�?
	private Context mContext;
	// private MediaPlayer mMediaPlayer;
	private int loadId1;
	private SoundPool mSoundPool;
	private Vibrator mVibrator;

	public BeepManager(Context context, boolean playBeep, boolean vibrate) {
		super();
		this.mContext = context;
		this.playBeep = playBeep;
		this.vibrate = vibrate;

		initial();
	}

	public boolean isPlayBeep() {
		return playBeep;
	}

	public void setPlayBeep(boolean playBeep) {
		this.playBeep = playBeep;
	}

	public boolean isVibrate() {
		return vibrate;
	}

	public void setVibrate(boolean vibrate) {
		this.vibrate = vibrate;
	}

	private void initial() {
		if (null == mSoundPool) {
			mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		}
		loadId1 = mSoundPool.load(mContext, R.raw.beep, 1);

		// initialVibrator
		mVibrator = (Vibrator) mContext
				.getSystemService(Context.VIBRATOR_SERVICE);
	}

	public void play() {
		// playMusic
		// if (playBeep && !km.inKeyguardRestrictedInputMode()) {
		// mMediaPlayer.start();
		// }
		if (playBeep) {
			// mMediaPlayer.start();
			// 参数1：播放特效加载后的ID�?
			// 参数2：左声道音量大小(range = 0.0 to 1.0)
			// 参数3：右声道音量大小(range = 0.0 to 1.0)
			// 参数4：特效音乐播放的优先级，因为可以同时播放多个特效音乐
			// 参数5：是否循环播放，0只播放一�?0 = no loop, -1 = loop forever)
			// 参数6：特效音乐播放的速度�?F为正常播放，范围 0.5 �?2.0
			mSoundPool.play(loadId1, 1f, 1f, 1, 0, 1f);
		}
		// vibrate
		if (vibrate) {
			mVibrator.vibrate(VIBRATE_DURATION);
		}

	}

}
