package com.cspoint.radheshyam.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cspoint.radheshyam.R;
import com.cspoint.radheshyam.adepter.ProductAdapter;
import com.cspoint.radheshyam.model.AddressList;
import com.cspoint.radheshyam.model.AllBarand;
import com.cspoint.radheshyam.model.BProduct;
import com.cspoint.radheshyam.model.Brand;
import com.cspoint.radheshyam.model.Medicine;
import com.cspoint.radheshyam.model.Search;
import com.cspoint.radheshyam.retrofit.APIClient;
import com.cspoint.radheshyam.retrofit.GetResult;
import com.cspoint.radheshyam.utiles.CustPrograssbar;
import com.cspoint.radheshyam.utiles.SessionManager;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class SearchActivity extends RootActivity implements GetResult.MyListener, ProductAdapter.RecyclerTouchListener {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.img_notfound)
    ImageView imgNotfound;
    @BindView(R.id.ed_search)
    EditText edSearch;
    @BindView(R.id.img_search)
    ImageView imgSearch;
    @BindView(R.id.lvl_actionsearch)
    LinearLayout lvlActionsearch;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.recycler_product)
    RecyclerView recyclerProduct;

    @BindView(R.id.lvl_notfound)
    LinearLayout lvlNotfound;
    SessionManager sessionManager;
    CustPrograssbar custPrograssbar;
    AddressList address;
    private static String[] meds = new String[]{
            "Belgium", "France", "Italy", "Germany", "Spain"
    };
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(SearchActivity.this);
        address = sessionManager.getAddress();
        recyclerProduct.setLayoutManager(new GridLayoutManager(SearchActivity.this, 2));
        recyclerProduct.setItemAnimator(new DefaultItemAnimator());

//        SearchData sd = new SearchData();
//        sd.getBrandProduct();
//        adapter = new ArrayAdapter<>(this,
//                R.layout.item_search, meds);
//        edSearch.setAdapter(adapter);
    }

    private void getSearch(String keyword) {
        custPrograssbar.prograssCreate(SearchActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("keyword", keyword);
            jsonObject.put("lats", address.getLatMap());
            jsonObject.put("longs", address.getLongMap());
        } catch (Exception e) {
            e.printStackTrace();

        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().getSearch(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");

    }

    @OnClick({R.id.img_back, R.id.img_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_search:
                if (!edSearch.getText().toString().isEmpty()) {
                    getSearch(edSearch.getText().toString());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                Search search = gson.fromJson(result.toString(), Search.class);
                if (search.getResult().equalsIgnoreCase("true")) {
                    if (search.getSearchData().size() != 0) {
                        lvlNotfound.setVisibility(View.GONE);
                        recyclerProduct.setVisibility(View.VISIBLE);
                        ProductAdapter productAdapter = new ProductAdapter(SearchActivity.this, search.getSearchData(), this);
                        recyclerProduct.setAdapter(productAdapter);
                    } else {
                        lvlNotfound.setVisibility(View.VISIBLE);
                        Toast.makeText(SearchActivity.this, search.getResponseMsg(), Toast.LENGTH_LONG).show();
                        imgNotfound.setImageDrawable(getResources().getDrawable(R.drawable.ic_not_found));
                    }

                } else {
                    recyclerProduct.setVisibility(View.GONE);
                    lvlNotfound.setVisibility(View.VISIBLE);
                    imgNotfound.setImageDrawable(getResources().getDrawable(R.drawable.ic_not_found));
                    Toast.makeText(SearchActivity.this, search.getResponseMsg(), Toast.LENGTH_LONG).show();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClickProductItem(String titel, Medicine medicine) {
        startActivity(new Intent(SearchActivity.this, ProductDetailsActivity.class).putExtra("MyClass", medicine).putParcelableArrayListExtra("PriceList", medicine.getProductInfo()).putStringArrayListExtra("ImageList", medicine.getProductImage()));

    }

//    class SearchData implements GetResult.MyListener {
//        private void getBrandProduct() {
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("uid", sessionManager.getUserDetails("").getId());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
//            Call<JsonObject> call = APIClient.getInterface().getBrand(bodyRequest);
//            GetResult getResult = new GetResult();
//            getResult.setMyListener(this);
//            getResult.callForLogin(call, "1");
//
//        }
//
//        @Override
//        public void callback(JsonObject result, String callNo) {
//
//            try {
//                if (callNo.equalsIgnoreCase("1")) {
//                    Gson gson = new Gson();
//                    AllBarand allBarand = gson.fromJson(result.toString(), AllBarand.class);
//                    if (allBarand.getResult().equalsIgnoreCase("true")) {
//                        List<Brand> aMeds = allBarand.getBrandData();
//                        Log.d("kkk", "callback: aMeds " + aMeds.size());
//                        meds = new String[aMeds.size()];
//                        for (int i = 0; i < aMeds.size(); i++) {
//                            meds[i] = aMeds.get(i).getBname();
//                        }
//                        adapter = new ArrayAdapter<String>(SearchActivity.this,
//                                R.layout.item_search, meds);
//                        edSearch.setAdapter(adapter);
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
