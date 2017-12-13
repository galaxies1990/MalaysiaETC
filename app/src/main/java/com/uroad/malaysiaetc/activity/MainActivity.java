package com.uroad.malaysiaetc.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uroad.lib.adapter.BasePageAdapter;
import com.uroad.malaysiaetc.R;
import com.uroad.malaysiaetc.common.BaseActivity;
import com.uroad.malaysiaetc.util.LocaleUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private Locale appLocale;
    private ViewPager vpCarousel;
    private List<ImageView> views;
    private ImageView[] counts;
    private BasePageAdapter pageAdapter;
    private LinearLayout llCount;
    private ImageButton ibSwitchLanguage;
    private RelativeLayout rlCardCharge,rlCardQuery,rlMyDevice,
            rlChargeRecord,rlConsumeRecord,rlBusinessInstroduction,
            rlUserGuide,rlCustomerService,rlUserCenter;
    private Dialog dialog;
    private RadioGroup group;
    private TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appLocale = LocaleUtils.getUserLocale(this);
        if ("".equals(appLocale)) {
            appLocale = LocaleUtils.getCurrentLocale(this);
        }
        LocaleUtils.updateLocale(this, appLocale);
        super.onCreate(savedInstanceState);
        setBaseContentLayoutWithoutTitle(R.layout.activity_main);
        initView();
        initDialog();
        initListener();
        loadData();
    }

    private void initView() {
        ibSwitchLanguage = (ImageButton) findViewById(R.id.ib_swith_language);
        vpCarousel = (ViewPager) findViewById(R.id.vp_carousel);
        llCount = (LinearLayout) findViewById(R.id.ll_count);

        rlCardCharge = (RelativeLayout) findViewById(R.id.rl_card_charge);
        rlCardQuery = (RelativeLayout) findViewById(R.id.rl_card_query);
        rlMyDevice = (RelativeLayout) findViewById(R.id.rl_my_device);

        rlChargeRecord = (RelativeLayout) findViewById(R.id.rl_charge_record);
        rlConsumeRecord = (RelativeLayout) findViewById(R.id.rl_consume_record);
        rlBusinessInstroduction = (RelativeLayout) findViewById(R.id.rl_business_instroduction);

        rlUserGuide = (RelativeLayout) findViewById(R.id.rl_user_guide);
        rlCustomerService = (RelativeLayout) findViewById(R.id.rl_customer_service);
        rlUserCenter = (RelativeLayout) findViewById(R.id.rl_user_center);
    }

    private Locale selectLocale;
    private String content = "";
    private boolean isSetLanguage = false;

    private void setLanugage(Locale locale) {
        if (LocaleUtils.updateLocale(this, locale)) {
            //修改成功
            if ("zh".equals(locale.toString())) {
                ibSwitchLanguage.setImageResource(R.mipmap.ic_language_zh);
            }else if("en".equals(locale.toString())) {
                ibSwitchLanguage.setImageResource(R.mipmap.ic_language_en);
            }else if("ms".equals(locale.toString())) {
                ibSwitchLanguage.setImageResource(R.mipmap.ic_language_ms);
            }
            recreate();
//        } else {
            //无需修改
//            showShortToast("当前语言与所选语言一致，无须切换");
        }
    }

    private void initListener() {
        ibSwitchLanguage.setOnClickListener(this);

        rlCardCharge.setOnClickListener(this);
        rlCardQuery.setOnClickListener(this);
        rlMyDevice.setOnClickListener(this);

        rlChargeRecord.setOnClickListener(this);
        rlConsumeRecord.setOnClickListener(this);
        rlBusinessInstroduction.setOnClickListener(this);

        rlUserGuide.setOnClickListener(this);
        rlCustomerService.setOnClickListener(this);
        rlUserCenter.setOnClickListener(this);

        vpCarousel.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < counts.length; i++) {
                    if (i == position) {
                        counts[i].setSelected(true);
                    }else{
                        counts[i].setSelected(false);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_swith_language:
                isSetLanguage = false;
                group.setVisibility(View.VISIBLE);
                tvContent.setVisibility(View.GONE);
                dialog.show();
                break;
            case R.id.rl_card_charge:
                //卡片充值
                break;
            case R.id.rl_card_query:
                //卡片查询
                break;
            case R.id.rl_my_device:
                //我的设备
                break;

            case R.id.rl_charge_record:
                //充值记录
                break;
            case R.id.rl_consume_record:
                //消费记录
                break;
            case R.id.rl_business_instroduction:
                //业务介绍
                break;

            case R.id.rl_user_guide:
                //使用指南
                break;
            case R.id.rl_customer_service:
                //客户服务
                break;
            case R.id.rl_user_center:
                //个人中心
                break;
        }
    }

    private void loadData() {
        views = new ArrayList<>();
        getData();
        pageAdapter = new BasePageAdapter(this, views);
        vpCarousel.setAdapter(pageAdapter);
        setCount();
    }

    private void getData() {
        for (int i = 0; i < 3; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.mipmap.ic_main_pager);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            views.add(imageView);
        }
    }
    //设置viewpager指示器
    private void setCount() {
        llCount.removeAllViews();
        if (views != null && views.size() > 0) {
            counts = new ImageView[views.size()];
            for (int i = 0; i < views.size(); i++) {
                ImageView imageView = new ImageView(this);
//                imageView.setLayoutParams(new ViewGroup.LayoutParams(20, 20));
                imageView.setPadding(10,0,10,0);
                imageView.setImageResource(R.drawable.bg_circle_gray_green_selector);
                if (i == 0) {
                    imageView.setSelected(true);
                }else{
                    imageView.setSelected(false);
                }
                counts[i] = imageView;

                llCount.addView(counts[i]);
            }
        }
    }
    //清除viewpager下方指示器
    private void clearCount() {
        llCount.removeAllViews();
    }
    private void initDialog() {
        dialog = new Dialog(this, R.style.baseCustomDialog);
        View view = View.inflate(this, R.layout.view_switch_language, null);
        group = (RadioGroup) view.findViewById(R.id.rg_language);
        RadioButton rbLanguageZh = (RadioButton) view.findViewById(R.id.rb_language_zh);
        RadioButton rbLanguageEn = (RadioButton) view.findViewById(R.id.rb_language_en);
        RadioButton rbLanguageMs = (RadioButton) view.findViewById(R.id.rb_language_ms);
        TextView tvYes = (TextView) view.findViewById(R.id.tv_yes);
        tvContent = (TextView) view.findViewById(R.id.tv_content);
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        group.setVisibility(View.VISIBLE);
        tvContent.setVisibility(View.GONE);

        if ("en".equals(appLocale.toString())) {
            rbLanguageEn.setChecked(true);
            content = getResources().getString(R.string.switch_to_english_version);
            ibSwitchLanguage.setImageResource(R.mipmap.ic_language_en);
        }else if("ms".equals(appLocale.toString())){
            rbLanguageMs.setChecked(true);
            content = getResources().getString(R.string.switch_to_malaysia_version);
            ibSwitchLanguage.setImageResource(R.mipmap.ic_language_ms);
        } else if ("zh".equals(appLocale.toString())) {
//        } else {
            rbLanguageZh.setChecked(true);
            content = getResources().getString(R.string.switch_to_chinese_version);
            ibSwitchLanguage.setImageResource(R.mipmap.ic_language_zh);
        }
        tvContent.setText(content);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_language_zh:
                        selectLocale = LocaleUtils.LOCALE_CHINESE;
                        content = getResources().getString(R.string.switch_to_chinese_version);
                        break;
                    case R.id.rb_language_en:
                        selectLocale = LocaleUtils.LOCALE_ENGLISH;
                        content = getResources().getString(R.string.switch_to_english_version);
                        break;
                    case R.id.rb_language_ms:
                        selectLocale = LocaleUtils.LOCALE_MALAYSIA;
                        content = getResources().getString(R.string.switch_to_malaysia_version);
                        break;
                }
            }
        });
        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                tvContent.setText(content);
                group.setVisibility(View.GONE);
                tvContent.setVisibility(View.VISIBLE);
                if (isSetLanguage) {
                    if (selectLocale != null) {
                        setLanugage(selectLocale);
                    }
                }else {
                    dialog.show();
                }
                isSetLanguage = !isSetLanguage;
//                DialogHelper.showComfirmDialog(MainActivity.this,
//                        getResources().getString(R.string.hint),
//                        content,
//                        getResources().getString(R.string.text_cancel),
//                        getResources().getString(R.string.text_confirm),
//                        new ComfirmDialog.DialogOnclick() {
//                    @Override
//                    public void PerDialogclick(Dialog dialog) {
//                        dialog.dismiss();
//                    }
//                }, new ComfirmDialog.DialogOnclick() {
//                    @Override
//                    public void PerDialogclick(Dialog dialog) {
//                        dialog.dismiss();
//                        if (selectLocale != null) {
//                            setLanugage(selectLocale);
//                        }
//                    }
//                });
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
    }
}
