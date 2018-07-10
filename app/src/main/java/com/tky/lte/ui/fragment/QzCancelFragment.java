package com.tky.lte.ui.fragment;

import android.widget.Button;
import com.tky.lte.R;
import com.tky.lte.base.BaseFragment;
import com.tky.lte.widget.ClearEditText;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ttz on 2018/5/30.
 */

public class QzCancelFragment extends BaseFragment {

    @BindView(R.id.et_Gnhm)
    ClearEditText etGnhm;
    @BindView(R.id.et_Yhhm)
    ClearEditText etYhhm;
    @BindView(R.id.btCancel)
    Button btCancel;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_qz_register;
    }

    @Override
    protected void init() {

    }


    @OnClick(R.id.btCancel)
    public void onViewClicked() {
    }
}
