package com.cspoint.radheshyam.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cspoint.radheshyam.R;
import com.cspoint.radheshyam.adepter.ProductAdapter;
import com.cspoint.radheshyam.model.AddressList;
import com.cspoint.radheshyam.model.BProduct;
import com.cspoint.radheshyam.model.Medicine;
import com.cspoint.radheshyam.model.User;
import com.cspoint.radheshyam.retrofit.APIClient;
import com.cspoint.radheshyam.retrofit.GetResult;
import com.cspoint.radheshyam.ui.ProductDetailsActivity;
import com.cspoint.radheshyam.utiles.CustPrograssbar;
import com.cspoint.radheshyam.utiles.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;


public class PopularProductFragment extends Fragment implements ProductAdapter.RecyclerTouchListener, GetResult.MyListener {

    @BindView(R.id.recycler_product)
    RecyclerView recyclerProduct;

    ProductAdapter productAdapter;
    SessionManager sessionManager;
    User user;
    AddressList address;

    CustPrograssbar custPrograssbar;
    public PopularProductFragment() {
        // Required empty public constructor
    }

    public static PopularProductFragment newInstance(String pincode, String bid) {
        PopularProductFragment fragment = new PopularProductFragment();
        Bundle args = new Bundle();
        args.putString("titel", pincode);
        args.putString("bid", bid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        ButterKnife.bind(this, view);
        custPrograssbar=new CustPrograssbar();
        sessionManager = new SessionManager(getActivity());
        user = sessionManager.getUserDetails("");
        address=sessionManager.getAddress();
        recyclerProduct.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerProduct.setItemAnimator(new DefaultItemAnimator());
        getAllProduct();
        return view;
    }

    private void getAllProduct() {
        custPrograssbar.prograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());
            jsonObject.put("lats", address.getLatMap());
            jsonObject.put("longs", address.getLongMap());
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().getRandomProduct(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");
    }

    @Override
    public void onClickProductItem(String titel, Medicine medicine) {
        startActivity(new Intent(getActivity(), ProductDetailsActivity.class).putExtra("MyClass", medicine).putParcelableArrayListExtra("PriceList", medicine.getProductInfo()).putStringArrayListExtra("ImageList", medicine.getProductImage()));
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            if (callNo.equalsIgnoreCase("1")) {
                custPrograssbar.closePrograssBar();
                Gson gson = new Gson();
                BProduct product = gson.fromJson(result.toString(), BProduct.class);
                if (product.getResult().equalsIgnoreCase("true")) {
                    productAdapter = new ProductAdapter(getActivity(), product.getResultData(), this);
                    recyclerProduct.setAdapter(productAdapter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}