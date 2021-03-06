package com.cspoint.radheshyam.ui;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cspoint.radheshyam.R;
import com.cspoint.radheshyam.utiles.SessionManager;
import com.google.android.material.appbar.AppBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cspoint.radheshyam.utiles.SessionManager.contact;

public class ContactActivity extends AppCompatActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.txt_actiontitle)
    TextView txtActiontitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.txt_dscirtion)
    TextView tatDeception;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contac);
        ButterKnife.bind(this);
        sessionManager=new SessionManager(ContactActivity.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tatDeception.setText(Html.fromHtml(sessionManager.getStringData(contact), Html.FROM_HTML_MODE_LEGACY));
        } else {
            tatDeception.setText(Html.fromHtml(sessionManager.getStringData(contact)));
        }
    }

    @OnClick(R.id.img_back)
    public void onClick() {
        finish();
    }
}