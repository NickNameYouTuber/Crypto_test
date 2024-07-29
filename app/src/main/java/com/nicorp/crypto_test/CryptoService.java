package com.nicorp.crypto_test;

import com.nicorp.crypto_test.objects.CryptoData;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CryptoService {

    @GET("simple/price?ids=bitcoin,ethereum,tether&vs_currencies=usd")
    Call<CryptoData> getCryptoData();
}