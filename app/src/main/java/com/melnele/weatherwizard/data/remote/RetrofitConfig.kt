package com.melnele.weatherwizard.data.remote

import android.content.Context
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

object RetrofitConfig {
    private fun getInstance(context: Context): Retrofit {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val httpCacheDirectory = File(context.cacheDir, "offlineCache")
        val cache = Cache(httpCacheDirectory, 10 * 1024 * 1024)

        val okHttpClient = OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(offlineCacheInterceptor())
            .addNetworkInterceptor(onlineInterceptor())
            .addInterceptor(httpLoggingInterceptor)
            .build()

        return Retrofit.Builder().baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient).build()
    }

    fun getApiService(context: Context): ApiService {
        return getInstance(context).create(ApiService::class.java)
    }

    private fun onlineInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val response: Response = chain.proceed(request)
            val maxAge = 60 * 30
            return@Interceptor response.newBuilder()
                .header("Cache-Control", "public, max-age=$maxAge")
                .removeHeader("Pragma")
                .build()
        }
    }

    private fun offlineCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            try {
                return@Interceptor chain.proceed(chain.request())
            } catch (e: Exception) {
                val maxStale = 60 * 60 * 24 // Offline cache available for 1 day
                val request = chain.request().newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                    .removeHeader("Pragma")
                    .build()

                return@Interceptor chain.proceed(request)
            }
        }
    }
}