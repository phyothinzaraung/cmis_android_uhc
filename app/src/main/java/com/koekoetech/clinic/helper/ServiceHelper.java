package com.koekoetech.clinic.helper;

import android.content.Context;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.koekoetech.clinic.BuildConfig;
import com.koekoetech.clinic.model.AuthenticationModel;
import com.koekoetech.clinic.model.FeedbackModel;
import com.koekoetech.clinic.model.ICDTermGroup;
import com.koekoetech.clinic.model.ICDTermsListResponse;
import com.koekoetech.clinic.model.LoginModel;
import com.koekoetech.clinic.model.RHShortTerm;
import com.koekoetech.clinic.model.StaffProfileModel;
import com.koekoetech.clinic.model.SuggestionWordModel;
import com.koekoetech.clinic.model.UhcPatientViewModel;

import java.io.File;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.realm.RealmList;
import io.realm.RealmObject;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by minthittun on 12/30/2015.
 **/
public class ServiceHelper {

    private static ApiService gitApiInterface;
//    private static Cache cache;

    public static ApiService getClient(final Context context) {
        if (gitApiInterface == null) {

//            Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
//                @Override
//                public Response intercept(Interceptor.Chain chain) throws IOException {
//                    Response originalResponse = chain.proceed(chain.request());
//                    int maxAge = 300; // read from cache for 5 minute
//                    return originalResponse.newBuilder()
//                            .header("Cache-Control", "public, max-age=" + maxAge)
//                            .build();
//                }
//            };

            //setup cache
//            File httpCacheDirectory = new File(context.getCacheDir(), "responses");
//            int cacheSize = 10 * 1024 * 1024; // 10 MiB
            //Cache cache = new Cache(httpCacheDirectory, cacheSize);
//            cache = new Cache(httpCacheDirectory, cacheSize);

            final OkHttpClient.Builder okClientBuilder = new OkHttpClient.Builder();
            okClientBuilder.readTimeout(5, TimeUnit.MINUTES);
            okClientBuilder.connectTimeout(5, TimeUnit.MINUTES);
            okClientBuilder.writeTimeout(5, TimeUnit.MINUTES);

            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new PrettyOkHttpLogger());
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                okClientBuilder.addInterceptor(logging);
                okClientBuilder.addInterceptor(new OkHttpFileLoggingInterceptor(getNetworkLogsDir(context)));
            }


            OkHttpClient okClient = okClientBuilder.build();
//            okClient.networkInterceptors().add(REWRITE_CACHE_CONTROL_INTERCEPTOR);
//            okClient.setCache(cache);

            //http://192.168.1.102:3000/api/
            //http://sfj2dapi.azurewebsites.net/
            //http://uhc.azurewebsites.net/api/
            String baseUrl = SharePreferenceHelper.getHelper(context).getBaseUrl();
            Retrofit client = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okClient)
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .build();

            gitApiInterface = client.create(ApiService.class);
        }
        return gitApiInterface;
    }

//    public static void removeFromCache(String url) {
//        try {
//            Iterator<String> it = cache.urls();
//            while (it.hasNext()) {
//                String next = it.next();
//                if (next.contains("http://hmiskkt.azurewebsites.net/api/" + url)) {
//                    it.remove();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static Gson getGson() {
        try {
            return new GsonBuilder()
                    .setExclusionStrategies(new ExclusionStrategy() {
                        @Override
                        public boolean shouldSkipField(FieldAttributes f) {
                            return f.getDeclaringClass().equals(RealmObject.class);
                        }

                        @Override
                        public boolean shouldSkipClass(Class<?> clazz) {
                            return false;
                        }
                    })
                    .registerTypeAdapter(new TypeToken<RealmList<String>>() {
                    }.getType(), new RealmListConverter<String>())
                    .registerTypeAdapter(new TypeToken<RealmList<ICDTermGroup>>() {
                    }.getType(), new RealmListConverter<ICDTermGroup>())
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
        } catch (Exception e) {
            e.printStackTrace();
            return new GsonBuilder().create();
        }
    }

    @NonNull
    private static File getNetworkLogsDir(final Context context) {
        @Nullable File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = context.getCacheDir();
        }
        return new File(cacheDir, "NetworkLogs");
    }

    public interface ApiService {

        @POST("staff/authenticate")
        Call<AuthenticationModel> postStaffLogin(@Body LoginModel model);

        @GET("staff/getById")
        Call<StaffProfileModel> getStaffById(@Query("id") int staffId);

        @POST("staff/create")
        Call<StaffProfileModel> createUpdateStaffProfile(@Body StaffProfileModel staffProfileModel);

        @POST("RH_ShortTerm/create")
        Call<RHShortTerm> postRHShortTerm(@Body RHShortTerm rh_shortTerm);

        @GET("RH_ShortTerm/getbyprogresscode")
        Call<RHShortTerm> getRHShortTermByProgressCode(@Query("progresscode") String progressCode);

        @POST("sync/upload")
        Call<UhcPatientViewModel> patientSyncUpstream(@Body UhcPatientViewModel patientViewModel);

        @POST("Word/createorupdate")
        Call<SuggestionWordModel> postSuggestions(@Body SuggestionWordModel suggestionWordModel);

        @GET("sync/zipdownload")
        Call<String> requestClinicDataFile(@Query("createdby") int staffId);

        @POST("feedback/createorupdate")
        Call<FeedbackModel> postFeedback(@Body FeedbackModel model);

        @GET("icd10/get")
        Call<ICDTermsListResponse> getICDTenTerms();
    }

}
