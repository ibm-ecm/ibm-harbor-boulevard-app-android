package com.futureworkshops.android.harborboulevard.presentation.accounts.pick;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.futureworkshops.android.harborboulevard.R;
import com.futureworkshops.android.harborboulevard.presentation.accounts.open.OpenAccountActivity;
import com.futureworkshops.datacap.common.ScaleTransformation;
import com.squareup.picasso.Picasso;


public class SelectAccountActivity extends AppCompatActivity {

    private ImageView mBanner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_account);

        mBanner = findViewById(R.id.banner);

        final View openRetirementAccountView = findViewById(R.id.open_retirement_account);
        openRetirementAccountView.setOnClickListener(view -> startActivity(new Intent(SelectAccountActivity.this,
                OpenAccountActivity.class)));

        final View loading = findViewById(R.id.loading);
        loading.setVisibility(View.GONE);
        openRetirementAccountView.setEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        int pixelSize = (int) (getResources().getDisplayMetrics().density * 300);

        Picasso.with(this)
                .load(R.drawable.first)
                .transform(ScaleTransformation.getScaleHeightTransformation(pixelSize))
                .placeholder(R.drawable.picasso_placeholder)
                .into(mBanner);
    }
}
