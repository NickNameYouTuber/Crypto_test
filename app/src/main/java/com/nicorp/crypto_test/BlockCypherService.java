package com.nicorp.crypto_test;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BlockCypherService {
    @GET("addrs/{address}/balance")
    Call<BlockCypherBalance> getBalance(@Path("address") String address);
}
