package com.futureworkshops.android.autocapture.presentation.camera.view.model;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.futureworkshops.datacap.common.model.Page;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by stelian on 01/08/2017.
 */

public final class PageItem {

    public static final int CAPTURING = 0;
    public static final int LOADED = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CAPTURING, LOADED})
    public @interface PageState {
    }

    @PageState
    private int state;
    private String pageId;
    private Page page;

    public PageItem(String pageId) {
        this.pageId = pageId;
        this.state = CAPTURING;
    }

    public PageItem(@NonNull Page page) {
        this.page = page;
        this.pageId = page.getId();
        this.state = LOADED;
    }

    @PageState
    public int getState() {
        return state;
    }

    public void setState(@PageState int state) {
        this.state = state;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
