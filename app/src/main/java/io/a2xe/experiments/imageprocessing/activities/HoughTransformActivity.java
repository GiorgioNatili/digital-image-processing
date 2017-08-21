package io.a2xe.experiments.imageprocessing.activities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.a2xe.experiments.imageprocessing.R;
import io.a2xe.experiments.imageprocessing.helpers.ActivityHelper;
import io.a2xe.experiments.imageprocessing.helpers.BitmapHelper;


public class HoughTransformActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.original_image)
    ImageView originalImage;

    @Bind(R.id.detect_edges_image_view)
    ImageView detectEdgesImage;

    @Bind(R.id.hough_transform_image)
    ImageView houghTransformImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hough_transform);
        ButterKnife.bind(this);
        ActivityHelper.setupToolbar(this, toolbar);
        Uri path = getIntent().getExtras().getParcelable(KEY_BITMAP);

        try {

            Bitmap edges = detectEdges(BitmapHelper.readBitmapFromPath(this, path));
            renderHoughTransform(edges);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap detectEdges(Bitmap bitmap) {

        Mat rgba = new Mat();
        Utils.bitmapToMat(bitmap, rgba);

        Mat edges = new Mat(rgba.size(), CvType.CV_8UC1);
        Imgproc.cvtColor(rgba, edges, Imgproc.COLOR_RGB2GRAY, 4);
        Imgproc.Canny(edges, edges, 70, 100);

        // Don't do that at home or work it's for visualization purpose.
        BitmapHelper.showBitmap(this, bitmap, originalImage);
        Bitmap resultBitmap = Bitmap.createBitmap(edges.cols(), edges.rows(), Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(edges, resultBitmap);
        BitmapHelper.showBitmap(this, resultBitmap, detectEdgesImage);

        return resultBitmap;
    }

    private void renderHoughTransform(Bitmap bitmap) {

        Mat rgba = new Mat();
        Utils.bitmapToMat(bitmap, rgba);

        Mat edges = new Mat();
        Mat mat = new Mat();

        Mat lines = new Mat();

        Utils.bitmapToMat(bitmap, mat);

        Imgproc.Canny(mat, edges, 50, 90);

        int threshold = 20;
        int minLineSize = 38;
        int lineGap = 30;

        Imgproc.HoughLinesP(edges, lines, 1, Math.PI / 180, threshold, minLineSize, lineGap);

        for (int x = 0; x < lines.rows(); x++){
            double[] vec = lines.get(x, 0);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);

            Imgproc.line(rgba, start, end, new Scalar(255, 0, 0), 3);

        }

        Bitmap bmp = Bitmap.createBitmap(rgba.cols(), rgba.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rgba, bmp);

        // Don't do that at home or work it's for visualization purpose.
        Bitmap resultBitmap = Bitmap.createBitmap(rgba.cols(), rgba.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rgba, resultBitmap);

        BitmapHelper.showBitmap(this, resultBitmap, houghTransformImage);
    }
}
