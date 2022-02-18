package com.cspoint.radheshyam.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cspoint.radheshyam.R;
import com.cspoint.radheshyam.adepter.ProductAdapter;
import com.cspoint.radheshyam.model.Medicine;
import com.cspoint.radheshyam.ui.ProductDetailsActivity;
import com.cspoint.radheshyam.utiles.CustPrograssbar;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProductFragment extends Fragment implements ProductAdapter.RecyclerTouchListener {

    @BindView(R.id.recycler_product)
    RecyclerView recyclerProduct;
    @BindView(R.id.txt_notfount)
    TextView txtNotfount;
    @BindView(R.id.lvl_notfound)
    LinearLayout lvlNotfound;

    private int mPosition;
    ProductAdapter productAdapter;
    CustPrograssbar custPrograssbar;
    private boolean loading = true;
    private int productListSize;


    public ProductFragment() {
        // Required empty public constructor
    }

    public static ProductFragment newInstance(int param1, String param2) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putInt("position", param1);
        args.putString("cid", param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProductFragment newInstance(String param1, String param2) {
        ProductFragment fragment = new ProductFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt("position");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        ButterKnife.bind(this, view);
        custPrograssbar = new CustPrograssbar();
//        Log.d("lll", "onCreateView: "+SubCategoryFragment.getInstance().categoryProducts.toString());
        if (SubCategoryFragment.getInstance().categoryProducts != null) {
            productListSize = SubCategoryFragment.getInstance().categoryProducts.get(mPosition).getProductlist().size();
        }
        recyclerProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if (!recyclerView.canScrollVertically(1)) {
                        if (loading) {
                            loading = false;
                            // Do pagination.. i.e. fetch new data
                            if (productListSize >= 50)
                                SubCategoryFragment.getInstance().loadMore(ProductFragment.this);
                        }
                    }
                }
            }
        });

        dataset();
        return view;
    }

    public void updateDataset(int s) {
        productAdapter.notifyItemRangeInserted(Math.max((s - 50), 0), s);
        loading = true;
    }


    private void dataset() {
        recyclerProduct.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerProduct.setItemAnimator(new DefaultItemAnimator());
        if (productListSize != 0) {
            lvlNotfound.setVisibility(View.GONE);
        } else {
            lvlNotfound.setVisibility(View.VISIBLE);
        }

        productAdapter = new ProductAdapter(getActivity(), SubCategoryFragment.getInstance().categoryProducts.get(mPosition).getProductlist(), this);
        recyclerProduct.setAdapter(productAdapter);

    }

    @Override
    public void onClickProductItem(String titel, Medicine medicine) {
        startActivity(new Intent(getActivity(), ProductDetailsActivity.class).putExtra("MyClass", medicine).putParcelableArrayListExtra("PriceList", medicine.getProductInfo()).putStringArrayListExtra("ImageList", medicine.getProductImage()));
    }

    @Override
    public void onResume() {
        super.onResume();

        if (recyclerProduct != null) {
            dataset();
        }
    }
}