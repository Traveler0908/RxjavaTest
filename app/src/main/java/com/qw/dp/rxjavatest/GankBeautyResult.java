package com.qw.dp.rxjavatest;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 类名称：
 * 类功能：
 * 类作者：Qw
 * 类日期：2017/3/27
 **/

public class GankBeautyResult {
    public boolean error;
    public @SerializedName("results") List<GankBeauty> beauties;
}
