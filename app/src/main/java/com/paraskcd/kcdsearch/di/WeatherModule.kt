package com.paraskcd.kcdsearch.di

import com.paraskcd.kcdsearch.BuildConfig
import com.paraskcd.kcdsearch.constants.PirateWeatherApiConstants
import com.paraskcd.kcdsearch.data.api.weather.PirateWeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PirateWeatherApiKey

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PirateWeatherRetrofit

@Module
@InstallIn(SingletonComponent::class)
object WeatherModule {
    @Provides
    @Singleton
    @PirateWeatherApiKey
    fun providePrivateWeatherApiKey(): String = BuildConfig.PIRATE_WEATHER_API_KEY

    @Provides
    @Singleton
    @PirateWeatherRetrofit
    fun providePirateWeatherRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(PirateWeatherApiConstants.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun providePirateWeatherApi(@PirateWeatherRetrofit retrofit: Retrofit): PirateWeatherApi =
        retrofit.create(PirateWeatherApi::class.java)
}