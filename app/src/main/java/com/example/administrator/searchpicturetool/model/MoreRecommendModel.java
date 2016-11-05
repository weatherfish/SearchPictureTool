package com.example.administrator.searchpicturetool.model;

import android.content.Context;

import com.example.administrator.searchpicturetool.model.bean.NewBanner;
import com.example.administrator.searchpicturetool.model.bean.NewRecommendContent;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/5/13 0013.
 */
public class MoreRecommendModel {
    public static Observable<List<NewRecommendContent>> getMoreRecommend(final Context app,String tip,float type){
        return  Observable.create(new Observable.OnSubscribe<List<NewRecommendContent>>() {
            @Override
            public void call(Subscriber<? super List<NewRecommendContent>> subscriber) {
                BmobQuery<NewRecommendContent> query = new BmobQuery<>();
                query.order("-createdAt");
                query.setLimit(1000);
                query.addWhereEqualTo("tip",tip);
                query.addWhereNotEqualTo("type", type);
                query.findObjects(app, new FindListener<NewRecommendContent>() {
                    @Override
                    public void onSuccess(List<NewRecommendContent> list) {
                        subscriber.onNext(list);
                    }

                    @Override
                    public void onError(int i, String s) {
                        subscriber.onError(new Throwable(s+"i: "+i));
                    }
                });
            }
        });
    }

    public static Observable<List<NewBanner>> getRecommendBanners(final Context app){
        return  Observable.create(new Observable.OnSubscribe<List<NewBanner>>() {
            @Override
            public void call(Subscriber<? super List<NewBanner>> subscriber) {
                BmobQuery<NewBanner> query = new BmobQuery<>();
                query.order("-createdAt");
                query.setLimit(1000);
                query.findObjects(app, new FindListener<NewBanner>() {
                    @Override
                    public void onSuccess(List<NewBanner> list) {
                        subscriber.onNext(list);
                    }

                    @Override
                    public void onError(int i, String s) {
                        subscriber.onError(new Throwable(s+"i: "+i));
                    }
                });
            }
        });
    }
}
