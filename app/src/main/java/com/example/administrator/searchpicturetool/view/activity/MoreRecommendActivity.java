package com.example.administrator.searchpicturetool.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.example.administrator.searchpicturetool.R;
import com.example.administrator.searchpicturetool.config.Constant;
import com.example.administrator.searchpicturetool.model.bean.NewRecommendContent;
import com.example.administrator.searchpicturetool.presenter.activityPresenter.MoreAcitivityPresenter;
import com.example.administrator.searchpicturetool.view.viewHolder.MoreViewHolder;
import com.jude.beam.bijection.RequiresPresenter;
import com.jude.beam.expansion.list.BeamListActivity;
import com.jude.beam.expansion.list.ListConfig;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/5/10 0010.
 */
@RequiresPresenter(MoreAcitivityPresenter.class)
public class MoreRecommendActivity extends BeamListActivity<MoreAcitivityPresenter,NewRecommendContent> implements View.OnClickListener{
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_more;
    }

    @Override
    protected BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new MoreViewHolder(parent);
    }
    @Override
    protected ListConfig getConfig() {
        return Constant.getUnloadMoreConfig();
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    public void showRefreshing(boolean shouldShow){
        getListView().getSwipeToRefresh().post(() -> getListView().getSwipeToRefresh().setRefreshing(shouldShow));
    }


    @Override
    public void onClick(View view) {
        if(view.getId() ==R.id.view_net_btn){
            showRefreshing(true);
            getPresenter().onRefresh();
        }else if(view.getId() ==R.id.view_empty_btn){
            showRefreshing(true);
            getPresenter().onRefresh();
        }
    }
}
