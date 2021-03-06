package com.mzp.player;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.mzp.player.http.FileCallBack;
import com.mzp.player.http.FileObserver;
import com.mzp.player.http.RetrofitHttpClient;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * author : ice-coffee.
 * <p>
 * date : 2018/9/11.
 * <p>
 * description :
 */

public class TestJavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] myList = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_PHONE_STATE};

        test("", null);
    }

    private final File cacheFile = getExternalCacheDir();

    private void test(final String url, FileCallBack<File> callBack) {
        Observable.defer(new Callable<ObservableSource<InputStream>>() {
            @Override
            public ObservableSource<InputStream> call() throws Exception {
                return Observable.just(RetrofitHttpClient.Companion.getRetrofitApi().getGoodsDetails(url).execute().body().byteStream());
            }
        }).map(new Function<InputStream, M3U8>() {
            @Override
            public M3U8 apply(InputStream inputStream) throws Exception {
                return M3U8Utils.INSTANCE.parseM3u8Url(url, inputStream);
            }
        }).flatMap(new Function<M3U8, ObservableSource<M3U8Ts>>() {
            @Override
            public ObservableSource<M3U8Ts> apply(M3U8 m3U8) throws Exception {
                return Observable.fromIterable(m3U8.getTsList());
            }
        }).map(new Function<M3U8Ts, File>() {
            @Override
            public File apply(M3U8Ts m3U8Ts) throws Exception {
                InputStream inputStream = RetrofitHttpClient.Companion.getRetrofitApi().getGoodsDetails(m3U8Ts.getTsUrl()).execute().body().byteStream();
                String tsCachePath = cacheFile.getAbsolutePath() + File.separator + m3U8Ts.getName();

                return StorageUtil.saveFile(inputStream, new File(tsCachePath));
            }
        }).toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<File>>() {
                    @Override
                    public void accept(List<File> files) throws Exception {

                    }
                });

        Observable.just(1)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e("", integer + "");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("", throwable.getMessage() + "");
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.e("", "success");
                    }
                });
    }
}


