package edu.vuum.mocca.ui.story;

import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

public class Utilities {
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }
	    return inSampleSize;
    }
    
	public static Bitmap createSizeVidoThumbnail(String videoFile,
			int reqWidth, int reqHeight)
	{
		Bitmap thumb;
		if(reqWidth > 96 || reqHeight > 96)
			thumb = ThumbnailUtils.createVideoThumbnail(videoFile, MediaStore.Images.Thumbnails.MINI_KIND);
		else //saves memory if desired size if above MICRO which is 96x96
			thumb = ThumbnailUtils.createVideoThumbnail(videoFile, MediaStore.Images.Thumbnails.MICRO_KIND);
		
		return thumb;
	}
	
	public static String getRealVideoPathFromURI(Context context, Uri contentUri) {
		  Cursor cursor = null;
		  try { 
		    String[] proj = { MediaStore.Video.Media.DATA };
		    cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
		    if(cursor!=null){
			    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
			    cursor.moveToFirst();
			    return cursor.getString(column_index);
		    }
		    return "";
		  } finally {
		    if (cursor != null) {
		      cursor.close();
		    }
		  }
		}
	
    public static Bitmap createSizedThumbnail(String fileName,
            int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap thumbnail =  BitmapFactory.decodeFile(fileName, options);
    
        //find original picture orientation and rotate
        Matrix matrix = new Matrix();
        int rotation = getExifOrientation(fileName);
        if (rotation != 0f) {matrix.preRotate(getBitmapRotation(rotation));}
    
        return Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true);
    }
    
    private static int getExifOrientation(String fileName) {
    	  ExifInterface exif;
    	  int orientation = 0;
    	  try {
    	    exif = new ExifInterface( fileName );
    	    orientation = exif.getAttributeInt( ExifInterface.TAG_ORIENTATION, 1 );
    	  } catch ( IOException e ) {
    	    e.printStackTrace();
    	  }
    	  Log.d("iRemember", "got orientation " + orientation);
    	  return orientation;
    	}
    
    private static int getBitmapRotation(int exifOrientation) {
    	  int rotation = 0;
    	  switch ( exifOrientation ) {
    	    case ExifInterface.ORIENTATION_ROTATE_180:
    	      rotation = 180;
    	      break;
    	    case ExifInterface.ORIENTATION_ROTATE_90:
    	      rotation = 90;
    	      break;
    	    case ExifInterface.ORIENTATION_ROTATE_270:
    	      rotation = 270;
    	      break;
    	  }

    	  return rotation;
    	}
}
