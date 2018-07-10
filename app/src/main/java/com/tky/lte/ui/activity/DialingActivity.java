package com.tky.lte.ui.activity;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiaxun.android.lte_r.ServiceConstant;
import com.tky.lte.R;
import com.tky.lte.base.BaseActivity;
import com.tky.lte.constants.Config;
import com.tky.lte.db.CallJiDao;
import com.tky.lte.lte.CALL;
import com.tky.lte.lte.GlobalFunc;
import com.tky.lte.lte.GlobalPara;
import com.tky.lte.lte.LogInstance;
import com.tky.lte.lte.LteHandle;
import com.tky.lte.ui.entity.CallJilEntity;
import com.tky.lte.ui.event.PttStateBean;
import com.tky.lte.util.ToastUtil;
import com.tky.lte.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ttz on 2018/6/19.
 */

public class DialingActivity extends BaseActivity {
    @BindView(R.id.tvPhoneNum)
    TextView tvPhoneNum;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.llClose)
    LinearLayout llClose;
    @BindView(R.id.imgMt)
    ImageView imgMt;
    @BindView(R.id.ll_MT)
    LinearLayout llMT;
    @BindView(R.id.llCenterView)
    LinearLayout llCenterView;
    @BindView(R.id.llOpen)
    LinearLayout llOpen;

    @BindView(R.id.imgTopTxD)
    ImageView imgTopTxD;
    @BindView(R.id.imgTopTxS)
    ImageView imgTopTxS;
    @BindView(R.id.imgTopTxMkf)
    ImageView imgTopTxMkf;
    @BindView(R.id.imgTopTxZy)
    ImageView imgTopTxZy;
    @BindView(R.id.imgTopTxKx)
    ImageView imgTopTxKx;
    @BindView(R.id.imgTopTxSb)
    ImageView imgTopTxSb;
    @BindView(R.id.llJingYin)
    LinearLayout llJingYin;
    @BindView(R.id.ll_Bhp)
    LinearLayout llBhp;
    @BindView(R.id.ll_Add)
    LinearLayout llAdd;
    @BindView(R.id.ll_Keep)
    LinearLayout llKeep;
    private boolean isMt = false;
    private LteHandle mLteHandle;
    private MediaPlayer mediaPlayer;


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_dialing;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        isMt = false;
        mLteHandle = LteHandle.getInstance();
        Utils.CheckScreen(this);
        mediaPlayer = new MediaPlayer();

        llJingYin.setAlpha(0.4f);
        llBhp.setAlpha(0.4f);
        llAdd.setAlpha(0.4f);
        llKeep.setAlpha(0.4f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    CALL currentCall = new CALL();//

    public void initView() {
        LteHandle mLteHandle = LteHandle.getInstance();
        if (mLteHandle.GetCallListSize() > 0) {
            CALL call = mLteHandle.callList.get(0);

            currentCall.CopyFromOne(call);//

            LogInstance.debug(GlobalPara.Tky, "this call is: strCallId=" + call.strCallId + ", callType=" + call.callType + ", peerNumber=" + call.peerNumber
                    + ", groupId=" + call.groupId + ", funNumber=" + call.funNumber + ", statusCode=" + call.statusCode
                    + ", priority=" + call.priority + ", call_way=" + call.call_way + ", call_handout=" + call.call_handout);
            //个呼
            if (call.callType == ServiceConstant.CALL_TYPE_SINGLE) {
                //通话中
                if (call.statusCode == 3) {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        mediaPlayer.stop();
                        LogInstance.debug(GlobalPara.Tky, "mediaPlayer.stop() 2");
                    }
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                    audioManager.setSpeakerphoneOn(true);
                    imgMt.setImageResource(R.drawable.mianti);
                    isMt = true;
                    tvTime.setText("通话中");

                    //
                    llCenterView.setVisibility(View.VISIBLE);
                    llClose.setVisibility(View.VISIBLE);
                    llOpen.setVisibility(View.GONE);


                    LogInstance.debug(GlobalPara.Tky, "calling for call");
                } else if (call.statusCode == 6) {//保持
                    tvTime.setText("保持中");
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        mediaPlayer.stop();
                        LogInstance.debug(GlobalPara.Tky, "mediaPlayer.stop() 3");
                    }

                    LogInstance.debug(GlobalPara.Tky, "hold for call");

                } else if (call.statusCode == 5) {//来呼
                    tvTime.setText("来电");

                    LogInstance.debug(GlobalPara.Tky, "a call is coming");
                    llCenterView.setVisibility(View.INVISIBLE);
                    llClose.setVisibility(View.VISIBLE);
                    llOpen.setVisibility(View.VISIBLE);
                    Utils.setVoicePrompt(this, mediaPlayer, "callin_ring.wav");
                } else if (call.statusCode == 1 || call.statusCode == 2) {//去呼
                    tvTime.setText("正在呼叫");

                    llCenterView.setVisibility(View.VISIBLE);
                    llClose.setVisibility(View.VISIBLE);
                    llOpen.setVisibility(View.GONE);
                    LogInstance.debug(GlobalPara.Tky, "a call is outing");
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                    audioManager.setSpeakerphoneOn(true);
                    imgMt.setImageResource(R.drawable.mianti);
                    isMt = true;
                } else {
                    LogInstance.debug(GlobalPara.Tky, "call.call_way is wrong");
                }
                getViewVisibility(Config.GH);


                //
                if (!call.funNumber.equals("")) {
                    tvPhoneNum.setText(GlobalFunc.translateFN(call.funNumber, call.callType));
                } else {
                    tvPhoneNum.setText(call.peerNumber);//strNumber
                }

            } else {//组呼广播
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                audioManager.setSpeakerphoneOn(true);
                imgMt.setImageResource(R.drawable.mianti);
                isMt = true;
                tvPhoneNum.setText(GlobalFunc.translateFN(call.groupId, call.callType));

                getViewVisibility(Config.ZH);
                llCenterView.setVisibility(View.VISIBLE);
                llClose.setVisibility(View.VISIBLE);
                llOpen.setVisibility(View.GONE);
            }
        } else {
            LogInstance.debug(GlobalPara.Tky, "call is finished");
            tvTime.setText("通话结束");
            LogInstance.debug(GlobalPara.Tky, "close speaker");
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(false);
            imgMt.setImageResource(R.drawable.ic_volume_up_24dp);
            isMt = false;

            //
            CallJilEntity callJilEntity = new CallJilEntity();
            if (currentCall.callType == 1) {
                if (!currentCall.funNumber.equals("")) {
                    callJilEntity.setPeerNumber(currentCall.funNumber);//callJilEntity.setPeerNumber(currentCall.funNumber);
                    callJilEntity.setFunNumber(currentCall.funNumber);
                    callJilEntity.setFunNumberName(GlobalFunc.translateFN(currentCall.funNumber, currentCall.callType));
                } else {
                    callJilEntity.setPeerNumber(currentCall.peerNumber);
                    callJilEntity.setFunNumberName("");
                    callJilEntity.setFunNumber("");
                }
            } else {
                callJilEntity.setPeerNumber(currentCall.groupId);
                callJilEntity.setFunNumberName(GlobalFunc.translateFN(currentCall.groupId, currentCall.callType));
                callJilEntity.setFunNumber("");
            }
            callJilEntity.setCall_way(currentCall.call_way);
            callJilEntity.setCallType(currentCall.callType);
            CallJiDao.insertCallJilEntity(callJilEntity);


            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                public void run() {
                    finish();
                }
            };
            timer.schedule(task, 1000);
        }
    }

    @Subscribe
    public void onEventBus(PttStateBean bean) {
        if (bean != null) {
            int iPttState = bean.getiPttState();
            Message msg = new Message();
            msg.what = Config.PttStateCode;
            msg.obj = iPttState;
            handler.sendMessage(msg);
        }
    }

    private void getViewVisibility(String str) {
        if (str.equals(Config.GH)) {
            imgTopTxD.setVisibility(View.VISIBLE);
            imgTopTxS.setVisibility(View.GONE);
            imgTopTxMkf.setVisibility(View.GONE);
            imgTopTxZy.setVisibility(View.GONE);
            imgTopTxKx.setVisibility(View.GONE);
            imgTopTxSb.setVisibility(View.GONE);
        } else if (str.equals(Config.ZH)) {
            imgTopTxD.setVisibility(View.GONE);
            imgTopTxS.setVisibility(View.VISIBLE);
            imgTopTxMkf.setVisibility(View.GONE);
            imgTopTxZy.setVisibility(View.GONE);
            imgTopTxKx.setVisibility(View.GONE);
            imgTopTxSb.setVisibility(View.GONE);
        } else if (str.equals(Config.KX)) {
            imgTopTxD.setVisibility(View.GONE);
            imgTopTxS.setVisibility(View.GONE);
            imgTopTxMkf.setVisibility(View.GONE);
            imgTopTxZy.setVisibility(View.GONE);
            imgTopTxKx.setVisibility(View.VISIBLE);
            imgTopTxSb.setVisibility(View.GONE);
        } else if (str.equals(Config.ZY)) {
            imgTopTxD.setVisibility(View.GONE);
            imgTopTxS.setVisibility(View.GONE);
            imgTopTxMkf.setVisibility(View.GONE);
            imgTopTxZy.setVisibility(View.VISIBLE);
            imgTopTxKx.setVisibility(View.GONE);
            imgTopTxSb.setVisibility(View.GONE);
        } else if (str.equals(Config.SB)) {
            imgTopTxD.setVisibility(View.GONE);
            imgTopTxS.setVisibility(View.GONE);
            imgTopTxMkf.setVisibility(View.GONE);
            imgTopTxZy.setVisibility(View.GONE);
            imgTopTxKx.setVisibility(View.GONE);
            imgTopTxSb.setVisibility(View.VISIBLE);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //0:PTT空闲，1:PTT占用忙；2:PTT请求占用成功，3:PTT请求失败  4:其他人正在占用
            switch (msg.what) {
                case Config.PttStateCode:
                    int ptt = (int) msg.obj;
                    if (ptt == 0) {
                        tvTime.setText("空闲");
                        getViewVisibility(Config.GH);
                    } else if (ptt == 1) {
                        tvTime.setText("话权抢占失败,请松开PTT");
                        getViewVisibility(Config.ZY);
                    } else if (ptt == 2) {
                        tvTime.setText("发言");
                        getViewVisibility(Config.ZY);
                    } else if (ptt == 3) {
                        tvTime.setText("话权抢占失败,请松开PTT");
                        getViewVisibility(Config.SB);
                    } else if (ptt == 4) {
                        tvTime.setText("聆听");
                        getViewVisibility(Config.ZY);
                    }
                    break;
            }
        }
    };


    @OnClick({R.id.ll_MT, R.id.llClose, R.id.llOpen})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_MT:
                if (mLteHandle.GetCallListSize() > 0) {
                    if (isMt) {

                        //
                        //LogInstance.debug(GlobalPara.Tky, "open speaker");
                        //AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        //audioManager.setMode(AudioManager.MODE_NORMAL);
                        //audioManager.setSpeakerphoneOn(false);
                        //imgMt.setImageResource(R.drawable.ic_volume_up_24dp);//关闭免提
                        //isMt = false;

                    } else {

                        //
                        //LogInstance.debug(GlobalPara.Tky, "close speaker");
                        //AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        //audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                        //audioManager.setSpeakerphoneOn(true);
                        //imgMt.setImageResource(R.drawable.mianti);//免提
                        //isMt = true;

                    }
                }
                break;
            case R.id.llClose:
                LteHandle lteClose = LteHandle.getInstance();
                if (lteClose.GetCallListSize() > 0) {
                    LogInstance.debug(GlobalPara.Tky, "end a call");
                    lteClose.AnswerOrEndCall((byte) 0x00);
                }
                break;
            case R.id.llOpen:
                if (mLteHandle.GetCallListSize() > 0) {
                    LogInstance.debug(GlobalPara.Tky, "answer a call");

                    //
                    if (!mLteHandle.mblPOCRegister) {
                        ToastUtil.showShortToastSafe("网络受限,无法进行操作");
                        return;
                    }

                    mLteHandle.AnswerOrEndCall((byte) 0x01);
                    llCenterView.setVisibility(View.VISIBLE);
                    llClose.setVisibility(View.VISIBLE);
                    llOpen.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.stop();
        }
        if (mediaPlayer != null)
            mediaPlayer.release();//释放资源
        EventBus.getDefault().unregister(this);
    }


    private boolean isPtt = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//PTT按键
        if (keyCode == 268) {

            if (!isPtt) {
                if (mLteHandle != null && mLteHandle.mblPOCRegister) {
                    if (mLteHandle.mCurrentCall.callType != 0x01) {
                        LogInstance.debug(GlobalPara.Tky, "dispatchKeyEvent Down PTT:" + keyCode);
                        mLteHandle.PttOperation(0x00);
                    }
                }
            }
            isPtt = true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 268) {

            if (isPtt) {
                if (mLteHandle != null && mLteHandle.mblPOCRegister) {
                    if (mLteHandle.mCurrentCall.callType != 0x01) {
                        LogInstance.debug(GlobalPara.Tky, "dispatchKeyEvent Up PTT:" + keyCode);
                        mLteHandle.PttOperation(0x01);
                    }
                }
            }
            isPtt = false;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
