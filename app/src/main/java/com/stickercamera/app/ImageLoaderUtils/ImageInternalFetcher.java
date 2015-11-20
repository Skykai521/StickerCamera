package aubry.chromio.com.dressup.ImageLoaderUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;


/**
 * Created by Gil on 07/06/2014.
 */
public class ImageInternalFetcher extends ImageResizer {

    Context mContext;

    public ImageInternalFetcher(Context context, int imageWidth, int imageHeight) {
        super(context, imageWidth, imageHeight);
        init(context);
    }

    public ImageInternalFetcher(Context context, int imageSize) {
        super(context, imageSize);
        init(context);
    }

    private void init(Context context){
        mContext = context;
    }



    protected Bitmap processBitmap(Uri uri){
        return decodeSampledBitmapFromFile(uri.getPath(), mImageWidth, mImageHeight, getImageCache());
    }

    @Override
    protected Bitmap processBitmap(Object data) {
        return processBitmap((Uri)data);
    }
}
