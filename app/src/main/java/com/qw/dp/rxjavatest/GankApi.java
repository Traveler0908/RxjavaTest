package com.qw.dp.rxjavatest;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * 类名称：
 * 类功能：
 * 类作者：Qw
 * 类日期：2017/3/27
 **/

public interface GankApi {
    @GET("data/福利/{number}/{page}")
    Observable<GankBeautyResult> getBeauties(@Path("number") int number, @Path("page") int page);
}
