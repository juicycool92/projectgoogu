package kr.googu.googu.resourceController;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;

/**
 * Created by Jay on 2018-01-18.
 */

public class ImageProcesser {
    public static Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 250;//(int)mContext.getResources().getDimension(R.dimen.profile_pic_size);
        int targetHeight = 250;//(int)mContext.getResources().getDimension(R.dimen.profile_pic_size);
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        canvas.drawBitmap(scaleBitmapImage,
                new Rect(0, 0, scaleBitmapImage.getWidth(),
                        scaleBitmapImage.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }
}
