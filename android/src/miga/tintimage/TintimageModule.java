package miga.tintimage;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.annotations.Kroll;
import java.util.HashMap;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.view.TiUIView;
import org.appcelerator.titanium.view.TiDrawableReference;

import android.util.Log;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import java.io.FileInputStream;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.IOException;
import org.appcelerator.kroll.common.TiConfig;

@Kroll.module(name="Tintimage", id="miga.tintimage")
public class TintimageModule extends KrollModule
{
	// Standard Debugging variables
	boolean tileImage=true;
	Activity activity;

	private static Mode getFilter(String mod){
	  Mode filterMode;
	  if (mod.equals("add")) {
	      filterMode = Mode.ADD;
	  } else if (mod.equals("clear")) {
	      filterMode = Mode.CLEAR;
	  } else if (mod.equals("darken")) {
	      filterMode = Mode.DARKEN;
	  } else if (mod.equals("dst")) {
	      filterMode = Mode.DST;
	  } else if (mod.equals("dst_atop")) {
	      filterMode = Mode.DST_ATOP;
	  } else if (mod.equals("dst_in")) {
	      filterMode = Mode.DST_IN;
	  } else if (mod.equals("dst_out")) {
	      filterMode = Mode.DST_OUT;
	  } else if (mod.equals("dst_over")) {
	      filterMode = Mode.DST_OVER;
	  } else if (mod.equals("lighten")) {
	      filterMode = Mode.LIGHTEN;
	  } else if (mod.equals("multiply")) {
	      filterMode = Mode.MULTIPLY;
	  } else if (mod.equals("overlay")) {
	      filterMode = Mode.OVERLAY;
	  } else if (mod.equals("screen")) {
	      filterMode = Mode.SCREEN;
	  } else if (mod.equals("src")) {
	      filterMode = Mode.SRC;
	  } else if (mod.equals("src_atop")) {
	      filterMode = Mode.SRC_ATOP;
	  } else if (mod.equals("src_in")) {
	      filterMode = Mode.SRC_IN;
	  } else if (mod.equals("src_out")) {
	      filterMode = Mode.SRC_OUT;
	  } else if (mod.equals("src_over")) {
	      filterMode = Mode.SRC_OVER;
	  } else if (mod.equals("xor")) {
	      filterMode = Mode.XOR;
	  } else {
	      filterMode = Mode.MULTIPLY;
	  }
	  return filterMode;
	}

	private String convertPath(String path) {
			if (path.startsWith("file://") || path.startsWith("content://") || path.startsWith("appdata://") || path.startsWith("appdata-private://")) {
				path = path.replaceAll("file://", "");
				path = path.replaceAll("content://", "");
	        	path = path.replaceAll("appdata:///?", "/mnt/sdcard/" + TiApplication.getInstance().getPackageName() + "/");
	        	path = path.replaceAll("appdata-private:///?", "/data/data/" + TiApplication.getInstance().getPackageName() + "/app_appdata/");

	        	//Log.i("tint", "Converted path to: " + path);
			}

			return path;
		}

	public TintimageModule() {
		super();
		TiApplication appContext = TiApplication.getInstance();
		activity = appContext.getCurrentActivity();
		//context=activity.getApplicationContext();
	}


	private Bitmap tintImage(Bitmap image, Bitmap mask, KrollDict args) {
		String col = args.optString("color", "");
		String mod1 = args.optString("mode", "multiply");
		String mod2 = args.optString("modeMask", "multiply");
		Boolean grad = args.optBoolean("vignette", false);

		if (image==null){
			// no image, so mask will be background
			image = mask;
			mask = null;
		}

		Mode filterMode1 = getFilter(mod1);
		Mode filterMode2 = getFilter(mod2);
		int width =  image.getWidth();
		int height =  image.getHeight();

		Bitmap workingBitmap = Bitmap.createScaledBitmap(image,width,height,true);
		Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
		Canvas canvas = new Canvas(mutableBitmap);

		Bitmap resultBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
		Canvas canvas2 = new Canvas(resultBitmap);

		// add second image
		if (mask!=null){
		  Paint Compose = new Paint();
		  Compose.setXfermode(new PorterDuffXfermode(filterMode2));
		  canvas.drawBitmap(mask, 0,0, Compose);
		}

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);


		// add color filter
		if (col!=""){
		  PorterDuffColorFilter cf = new PorterDuffColorFilter(Color.parseColor(col), filterMode1);
		  paint.setColorFilter(cf);
		}

		// gradient
		if (grad){
		  int[] Colors = {0x00000000, 0xFF000000};
		  float[] ColorPosition = {0.10f, 0.99f};
		  RadialGradient gradient = new RadialGradient(width / 2,height / 2, width - width /2, Colors, ColorPosition, android.graphics.Shader.TileMode.CLAMP);
		  paint.setDither(true);
		  paint.setShader(gradient);
		}

		canvas2.drawBitmap(mutableBitmap, 0,0, paint);
		return resultBitmap;

	}

	private Bitmap mask(Bitmap image, Bitmap mask) {
	  // todo: image wiederholen und skalierung richtig

	  Bitmap bitmapOut = Bitmap.createBitmap(mask.getWidth(),mask.getHeight(), Bitmap.Config.ARGB_8888);
	  Canvas canvas = new Canvas(bitmapOut);
	  Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

	  if (tileImage){
	    BitmapDrawable background = new BitmapDrawable(image);
	    //in this case, you want to tile the entire view
	    background.setBounds(0, 0, mask.getWidth(),mask.getHeight());
	    background.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
	    background.draw(canvas);
	  } else {
	    canvas.drawBitmap(image,(int)(mask.getWidth()*0.5 - image.getWidth()*0.5), 0, paint);
	  }


	  Paint xferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	  xferPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));


	  canvas.drawBitmap(mask, 0, 0, xferPaint);
	  xferPaint.setXfermode(null);
	  return bitmapOut;
	}

	@Kroll.method
	public TiBlob grayscale(HashMap args) {
		KrollDict arg = new KrollDict(args);
		TiDrawableReference ref_image = null; 
		Bitmap bmp_img = null;
		TiBlob result = null;

		if (arg.get("image") !="" && arg.get("image") != null) {
			ref_image = TiDrawableReference.fromBlob(activity, (TiBlob)arg.get("image"));
			bmp_img = ref_image.getBitmap();
			Bitmap bmpGrayscale = Bitmap.createBitmap(bmp_img.getWidth(), bmp_img.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(bmpGrayscale);
			Paint paint = new Paint();
			ColorMatrix cm = new ColorMatrix();
			cm.setSaturation(0);
			ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
			paint.setColorFilter(f);
			c.drawBitmap(bmp_img, 0, 0, paint);
			result = TiBlob.blobFromImage(bmpGrayscale);
		}

		return result;
	}


	@Kroll.method
	public TiBlob tint(HashMap args) {
		KrollDict arg = new KrollDict(args);
		TiDrawableReference ref_image = null; 
		TiDrawableReference ref_mask = null; 
		Bitmap bmp_mask = null;
		Bitmap bmp_img = null;
		TiBlob result = null;
		
		if (arg.get("image") !="" && arg.get("image") != null) {
			ref_image = TiDrawableReference.fromBlob(activity, (TiBlob)arg.get("image"));
			bmp_img = ref_image.getBitmap();
		}
		
		if (arg.get("mask") !="" && arg.get("mask") != null ) {
			ref_mask = TiDrawableReference.fromBlob(activity, (TiBlob)arg.get("mask"));
			bmp_mask = ref_mask.getBitmap();
		}

		if (bmp_img != null) {
			Bitmap img = tintImage(bmp_img,bmp_mask,arg);
			result = TiBlob.blobFromImage(img);
		}
		return result;
	}


	@Kroll.method
	public TiBlob mask(HashMap args) {
		KrollDict arg = new KrollDict(args);
		TiDrawableReference ref = TiDrawableReference.fromBlob(getActivity(), (TiBlob)arg.get("image"));
		Bitmap ref_img1 = ref.getBitmap();
		ref = TiDrawableReference.fromBlob(getActivity(), (TiBlob)arg.get("mask"));
		Bitmap ref_mask1 =  ref.getBitmap();

		Bitmap result = mask(ref_img1,ref_mask1);
		TiBlob blb = TiBlob.blobFromImage(result);
		return blb;
	}


}
