package com.futureworkshops.android.harborboulevard.presentation.accounts.open;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.futureworkshops.android.harborboulevard.R;
import com.futureworkshops.android.harborboulevard.presentation.accounts.create.FundWithCheckWizardActivity;
import com.futureworkshops.datacap.common.ScaleTransformation;
import com.squareup.picasso.Picasso;


public class OpenAccountActivity extends AppCompatActivity {

    private ImageView mBanner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_account);

        mBanner = findViewById(R.id.banner);

        View fundWithCheckView = findViewById(R.id.fund_with_check);
        fundWithCheckView.setOnClickListener(view -> startActivity(new Intent(OpenAccountActivity.this, FundWithCheckWizardActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();

        int pixelSize = (int) (getResources().getDisplayMetrics().density * 300);

        Picasso.with(this)
                .load(R.drawable.second)
                .transform(ScaleTransformation.getScaleHeightTransformation(pixelSize))
                .placeholder(R.drawable.picasso_placeholder)
                .into(mBanner);
    }

}
