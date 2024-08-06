package com.websarva.wings.android.asynccounthandlersample;


import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Callable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    @Override
    @UiThread
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ボタンの取得
        Button btSync = findViewById(R.id.btSync);
        Button btAsync = findViewById(R.id.btAsync);

        //ボタンにリスナーを登録
        btSync.setOnClickListener(new SyncClickListener());
        btAsync.setOnClickListener(new AsyncClickListener());
    }

    //Logcatにカウント状況を出力するメソッド
    public void CountMethod(String str){
        for(int i= 0; i < 1000; i++){
            System.out.println(str + i);
        }
    }

    //同期ボタン
    private class SyncClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            CountMethod("count1: ");
            CountMethod("count2: ");
        }
    }


    //非同期ボタン
    private class AsyncClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            //LooperとHandler
            Looper mainLooper = Looper.getMainLooper();
            Handler handler = HandlerCompat.createAsync(mainLooper);
            Receiver receiver = new Receiver(handler);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            //戻り値なし
            executorService.submit(receiver);

            CountMethod("ui: ");

            String result ="";

            TextView tvMsg = findViewById(R.id.tvMsg);
            tvMsg.setText(result);

        }


        //ワーカースレッド用のクラスとその処理（Runnable）戻り値なし
        private class Receiver implements Runnable{

            private final Handler _handler;

            public Receiver(Handler _handler) {
                this._handler = _handler;
            }

            @WorkerThread
            @Override
            public void run() {

                ProgressUpdateExecutor progressUpdateExecutor = new ProgressUpdateExecutor("処理開始");
                _handler.post(progressUpdateExecutor);

                CountMethod("worker: ");
                //UIスレッドに渡すデータ
                progressUpdateExecutor = new ProgressUpdateExecutor("完了");
                _handler.post(progressUpdateExecutor);

            }
        }

        //処理開始を
        private class   ProgressUpdateExecutor implements Runnable{

            private String _msg;

            public ProgressUpdateExecutor(String msg) {
                _msg = msg;
            }

            @Override
            public void run() {
                TextView tvMsg = findViewById(R.id.tvMsg);
                tvMsg.setText(_msg);
            }
        }

    }
}