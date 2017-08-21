package io.a2xe.experiments.imageprocessing.activities;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import io.a2xe.experiments.imageprocessing.R;
import io.a2xe.experiments.imageprocessing.helpers.ActivityHelper;

public class ProcessingOptionsActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing_options);
        ButterKnife.bind(this);
        ActivityHelper.setupToolbar(this, toolbar);

        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                getResources().getResourcePackageName(R.drawable.original_road) + '/' +
                getResources().getResourceTypeName(R.drawable.original_road) + '/' +
                getResources().getResourceEntryName(R.drawable.original_road) );

        bundle = new Bundle();
        bundle.putParcelable(KEY_BITMAP, uri);
    }

    @OnClick({R.id.histogram, R.id.edges, R.id.light, R.id.hough})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.histogram:
                ActivityHelper.startActivity(this, HistogramActivity.class, bundle);
                break;
            case R.id.edges:
                ActivityHelper.startActivity(this, DetectEdgesActivity.class, bundle);
                break;
            case R.id.light:
                ActivityHelper.startActivity(this, DetectLightActivity.class, bundle);
                break;
            case R.id.hough:
                ActivityHelper.startActivity(this, HoughTransformActivity.class, bundle);
                break;
        }
    }

}
