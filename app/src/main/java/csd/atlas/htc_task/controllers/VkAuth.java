package csd.atlas.htc_task.controllers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import csd.atlas.htc_task.R;

/**
 * Created by FRAME on 2/25/2018.
 */

public class VkAuth extends DialogFragment {

    private WebView mWebView;
    public static final String EXTRA_TOKEN = "csd.atlas.HTCtest.token";
    final static private String LOGIN_PAGE = "https://oauth.vk.com/authorize?client_id=6373081&redirect_uri=&response_type=token&scope=friends,groups&v=5.73&state=123456&display=touch";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_webview,
                null);
        mWebView = (WebView) v.findViewById(R.id.webView);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.contains("https://oauth.vk.com/blank.html#access_token=")) {
                    String token = getToken(url);
                    sendResult(Activity.RESULT_OK, token);
                    VkAuth.this.dismiss();
                }
                Log.i("Current url = ", url);
            }
        });
        mWebView.loadUrl(LOGIN_PAGE);
        return new AlertDialog.Builder(getActivity()).setView(v)
                .create();
    }

    public static VkAuth newInstance() {
        VkAuth fragment = new VkAuth();
        return fragment;
    }

    private String getToken(String url) {
        String[] arr = url.split("=|&");
        return arr[1];
    }

    private void sendResult(int resultCode, String token) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TOKEN, token);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}

