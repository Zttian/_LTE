package com.tky.lte.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tky.lte.R;
import com.tky.lte.base.BaseActivity;
import com.tky.lte.lte.GlobalPara;
import com.tky.lte.lte.LogInstance;
import com.tky.lte.lte.LteHandle;
import com.tky.lte.util.ToastUtil;
import com.tky.lte.util.Utils;
import com.tky.lte.widget.CTitleBar;
import com.tky.lte.widget.ClearEditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ttz on 2018/6/11.
 */

public class JiTrainActivity extends BaseActivity implements CTitleBar.CTitleBarContainer {


    @BindView(R.id.tv_Jcm)
    TextView tvJcm;
    @BindView(R.id.et_Jch)
    ClearEditText etJch;
    @BindView(R.id.tv_gnm)
    TextView tvGnm;
    @BindView(R.id.btCalling)
    Button btCalling;
    private List<String> jicheList = new ArrayList<>();
    private List<String> gnengList = new ArrayList<>();

    @Override
    public void initializeTitleBar(CTitleBar titleBar) {
        titleBar.setTitle("机车功能号呼叫");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_ji_train;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        jicheList.add("225");
        jicheList.add("231");
        jicheList.add("232");

        gnengList.add("本务机司机(车载台)");
        gnengList.add("补机司机1(车载台)");
        gnengList.add("补机司机2(车载台)");
        gnengList.add("补机司机3(车载台)");
        gnengList.add("补机司机4(车载台)");
        gnengList.add("列车长1");
        gnengList.add("列车长2");
        gnengList.add("乘警长1");
        gnengList.add("乘警长2");
        gnengList.add("ETCS/CTCS使用");

        gnengList.add("本务机司机(手持终端)");
        gnengList.add("补机1司机(手持终端)");
        gnengList.add("补机2司机(手持终端)");
        gnengList.add("补机3司机(手持终端)");
        gnengList.add("补机4司机(手持终端)");

        gnengList.add("运转车长1随车机械师1");
        gnengList.add("运转车长2随车机械师2");
    }

    /**
     * 功能码输入进入后包含 01本务机司机（车载台）、
     * 02补机司机1（车载台）、03补机司机2（车载台）、
     * 04补机司机3（车载台）、05补机司机4（车载台）、
     * 10列车长1、11列车长2、31乘警长1、32乘警长2、
     * 40ETCS/CTCS使用、81本务机司机（手持终端）、
     * 82补机1司机（手持终端）、83补机2司机（手持终端）、
     * 84补机3司机（手持终端）、85补机4司机（手持终端）、
     * 86运转车长1随车机械师1、87运转车长2随车机械师2
     * @param str
     * @return
     */
    private String getGnm(String str){
        String gnm = "";
        if (str.equals("本务机司机(车载台)")){
            gnm = "01";
        }else if(str.equals("补机司机1(车载台)")){
            gnm = "02";
        }else if(str.equals("补机司机2(车载台)")){
            gnm = "03";
        }else if(str.equals("补机司机3(车载台)")){
            gnm = "04";
        }else if(str.equals("补机司机4(车载台)")){
            gnm = "05";
        }else if(str.equals("列车长1")){
            gnm = "10";
        }else if(str.equals("列车长2")){
            gnm = "11";
        }else if(str.equals("乘警长1")){
            gnm = "31";
        }else if(str.equals("乘警长2")){
            gnm = "32";
        }else if(str.equals("ETCS/CTCS使用")){
            gnm = "40";
        }else if(str.equals("本务机司机(手持终端)")){
            gnm = "81";
        }else if(str.equals("补机1司机(手持终端)")){
            gnm = "82";
        }else if(str.equals("补机2司机(手持终端)")){
            gnm = "83";
        }else if(str.equals("补机3司机(手持终端)")){
            gnm = "84";
        }else if(str.equals("补机4司机(手持终端)")){
            gnm = "85";
        }else if(str.equals("运转车长1随车机械师1")){
            gnm = "86";
        }else if(str.equals("运转车长2随车机械师2")){
            gnm = "87";
        }
        return gnm;
    }

    @OnClick({R.id.tv_Jcm, R.id.tv_gnm, R.id.btCalling})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_Jcm:
                Utils.initOptionsPickerView(this,jicheList,tvJcm);
                break;
            case R.id.tv_gnm:
                Utils.initOptionsPickerView(this,gnengList,tvGnm);
                break;
            case R.id.btCalling:
                if (TextUtils.isEmpty(tvJcm.getText().toString().trim())) {
                    ToastUtil.showShortToastSafe("机车码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(etJch.getText().toString().trim())) {
                    ToastUtil.showShortToastSafe("机车号不能为空");
                    return;
                }
                if (TextUtils.isEmpty(tvGnm.getText().toString().trim())) {
                    ToastUtil.showShortToastSafe("功能码不能为空");
                    return;
                }

                if (!TextUtils.isEmpty(etJch.getText().toString().trim())) {
                    if (etJch.getText().toString().trim().length() < 5) {
                        ToastUtil.showShortToastSafe("机车号不足5位,前面补0");
                        return;
                    }
                }

                String strJcm = tvJcm.getText().toString().trim();
                String strJch = etJch.getText().toString().trim();
                String strGnm = getGnm(tvGnm.getText().toString().trim());


                String strEngineNum = strJcm + strJch;
                int iStatus = Integer.parseInt(strGnm);
                String runnumberstatus = "";
                if (iStatus < 10) {
                    runnumberstatus = "0" + String.valueOf(iStatus);
                } else {
                    runnumberstatus = String.valueOf(iStatus);
                }
                String strNumber = GlobalPara.strPreEfnNumber + strEngineNum + runnumberstatus;

                LogInstance.debug(GlobalPara.Tky, "呼叫:" + strNumber);

                LteHandle mLteHandle = LteHandle.getInstance();

                //
                if(!mLteHandle.mblPOCRegister){
                    ToastUtil.showShortToastSafe("网络受限,无法进行操作");
                    return;
                }

                int ret = mLteHandle.StartCall(0x01, -1, strNumber);
                if (ret == -1) {
                    ToastUtil.showShortToast("呼叫失败");
                } else {
                    launchActivity(DialingActivity.class);
                }
                break;
        }
    }
}
