package io.a2xe.experiments.imageprocessing.activities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.a2xe.experiments.imageprocessing.R;
import io.a2xe.experiments.imageprocessing.helpers.ActivityHelper;
import io.a2xe.experiments.imageprocessing.helpers.BitmapHelper;


public class DetectLightActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.sample_image_panorama)
    ImageView panoramaImageView;

    @Bind(R.id.detect_light_image_view)
    ImageView detectLightImageView;

    @Bind(R.id.gaussian_blur_image_view)
    ImageView gaussianBlurImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detect_light);

        ButterKnife.bind(this);
        ActivityHelper.setupToolbar(this, toolbar);

        Uri path = getIntent().getExtras().getParcelable(KEY_BITMAP);

        Glide.with(this).load(R.drawable.original_road)
                .into(new GlideDrawableImageViewTarget(panoramaImageView){
                    @Override public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        // here it's similar to RequestListener, but with less information (e.g. no model available)
                        super.onResourceReady(resource, animation);
                        // here you can be sure it's already set
                        detectLight(BitmapHelper.readBitmapFromImageView(panoramaImageView), 45);
                    }
                    // +++++ OR +++++
                    @Override protected void setResource(GlideDrawable resource) {
                        // this.getView().setImageDrawable(resource); is about to be called
                        super.setResource(resource);
                        // here you can be sure it's already set
                    }
                });


        try {

            // detectLight(BitmapHelper.readBitmapFromPath(this, path), 45);
            // detectLight(BitmapHelper.readBitmapFromImageView(panoramaImageView), 45);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void detectLight(Bitmap bitmap, double gaussianBlurValue) {
        Mat rgba = new Mat();
        Utils.bitmapToMat(bitmap, rgba);

        Mat grayScaleGaussianBlur = new Mat();
        Imgproc.cvtColor(rgba, grayScaleGaussianBlur, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(grayScaleGaussianBlur, grayScaleGaussianBlur, new Size(gaussianBlurValue, gaussianBlurValue), 0);

        Core.MinMaxLocResult minMaxLocResultBlur = Core.minMaxLoc(grayScaleGaussianBlur);
        Imgproc.circle(rgba, minMaxLocResultBlur.maxLoc, 30, new Scalar(255), 3);

        // Don't do that at home or work it's for visualization purpose.
        Bitmap resultBitmap = Bitmap.createBitmap(rgba.cols(), rgba.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rgba, resultBitmap);
        BitmapHelper.showBitmap(this, resultBitmap, detectLightImageView);

        Bitmap blurryBitmap = Bitmap.createBitmap(grayScaleGaussianBlur.cols(), grayScaleGaussianBlur.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(grayScaleGaussianBlur, blurryBitmap);
        BitmapHelper.showBitmap(this, blurryBitmap, gaussianBlurImageView);

    }

}
