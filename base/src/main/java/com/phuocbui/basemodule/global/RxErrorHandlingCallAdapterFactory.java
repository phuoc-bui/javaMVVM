package com.phuocbui.basemodule.global;

import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.Nullable;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class RxErrorHandlingCallAdapterFactory extends CallAdapter.Factory {
    private RxJava2CallAdapterFactory origin = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io());

    public static CallAdapter.Factory create() {
        return new RxErrorHandlingCallAdapterFactory();
    }

    @Nullable
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        CallAdapter<?, ?> wrapped = origin.get(returnType, annotations, retrofit);
        return new RxCallAdapterWrapper<>(wrapped, retrofit);
    }

    private static class RxCallAdapterWrapper<R> implements CallAdapter<R, Observable<R>> {

        private CallAdapter wrappedCallAdapter;
        private Retrofit retrofit;

        public RxCallAdapterWrapper(CallAdapter wrappedCallAdapter, Retrofit retrofit) {
            this.wrappedCallAdapter = wrappedCallAdapter;
            this.retrofit = retrofit;
        }

        @Override
        public Type responseType() {
            return wrappedCallAdapter.responseType();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Observable<R> adapt(@NonNull Call<R> call) {
            Observable<R> adapted = (Observable<R>) wrappedCallAdapter.adapt(call);
            return adapted.onErrorResumeNext(throwable -> {
                return Observable.error(asRetrofitException(throwable));
            });
        }

        private RetrofitException asRetrofitException(Throwable throwable) {
            // We had non-200 http error
            if (throwable instanceof HttpException) {
                HttpException exception = (HttpException) throwable;
                Response response = exception.response();
                return RetrofitException.httpError(response.raw().request().url().toString(), response, retrofit);
            }

            // A network error happened
            if (throwable instanceof IOException) {
                return RetrofitException.networkError((IOException) throwable);
            }

            if (throwable instanceof JsonSyntaxException) {
                return RetrofitException.jsonSyntaxError((JsonSyntaxException) throwable);
            }

            // We don't know what happened. We need to simply convert to an unknown error
            return RetrofitException.unexpectedError(throwable);
        }

    }
}
