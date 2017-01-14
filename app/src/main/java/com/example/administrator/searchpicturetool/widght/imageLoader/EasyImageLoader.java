package com.example.administrator.searchpicturetool.widght.imageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;


import java.util.concurrent.ThreadPoolExecutor;

/**
 * 供外层使用的图片加载类，通过它实现图片加载
 * Created by Wenhuaijun on 2016/4/22 0022.
 */
public class EasyImageLoader {
    private static EasyImageLoader instance=null;
    private static ImageLrucache imageLrucache;
    private static ImageDiskLrucache imageDiskLrucache;
    //创建一个静态的线程池对象
    private static ThreadPoolExecutor THREAD_POOL_EXECUTOR = null;
    //创建一个更新ImageView的UI的Handler
    private static TaskHandler mMainHandler;
    private Context mContext;
    //私有的构造方法，防止在外部实例化该ImageLoader
    private EasyImageLoader(Context context){
        mContext =context.getApplicationContext();
        THREAD_POOL_EXECUTOR = ImageThreadPoolExecutor.getInstance();
        imageLrucache = new ImageLrucache();
        imageDiskLrucache = new ImageDiskLrucache(mContext);
        mMainHandler = new TaskHandler();
    }

    public static EasyImageLoader getInstance(Context context){
        if(instance==null){
            synchronized (EasyImageLoader.class){
                if(instance == null){
                    instance = new EasyImageLoader(context);
                }
            }
        }
        return instance;
    }

    //返回内存缓存类
    public static ImageLrucache getImageLrucache(){
        if(imageLrucache==null){
            synchronized (EasyImageLoader.class){
                if(imageLrucache==null){
                    imageLrucache = new ImageLrucache();
                }
            }
        }
        return imageLrucache;
    }

    //返回本地缓存类
    public static ImageDiskLrucache getImageDiskLrucache(Context context){
        if(imageDiskLrucache==null){
            synchronized (EasyImageLoader.class) {
                if(imageDiskLrucache==null) {
                    imageDiskLrucache = new ImageDiskLrucache(context);
                }
            }
        }
        return imageDiskLrucache;
    }

    public void bindBitmap(final String url, final ImageView imageView){
        bindBitmap(url, imageView, 0, 0,null);
    }

    public void bindBitmap(final String url, final ImageView imageView,BindBitmapErrorCallBack errorCallBack){
        bindBitmap(url, imageView, 0, 0,errorCallBack);
    }

    public void bindBitmap(final String url, final ImageView imageView,final int reqWidth,final int reqHeight){
        bindBitmap(url, imageView, reqWidth, reqWidth,null);
    }

    public void bindBitmap(final String uri,final ImageView imageView,final int reqWidth,final int reqHeight,BindBitmapErrorCallBack errorCallback){
        //设置加载loadding图片
      //  imageView.setImageResource(R.drawable.ic_loading2);
        //防止加载图片的时候数据错乱
       // imageView.setTag(TAG_KEY_URI, uri);
        imageView.setTag(uri);
        //从内存缓存中获取bitmap
        Bitmap bitmap = imageLrucache.loadBitmapFromMemCache(uri);
        if(bitmap!=null){
            imageView.setImageBitmap(bitmap);
            return;
        }
        LoadBitmapTask loadBitmapTask =new LoadBitmapTask(mContext,mMainHandler,imageView,uri,reqWidth,reqHeight,errorCallback);
       //使用线程池去执行Runnable对象
        THREAD_POOL_EXECUTOR.execute(loadBitmapTask);

    }

    /**
     *
     * @param url 图片链接
     * @param callback bitmap回调接口
     * @param reqWidth 需求宽度
     * @param reqHeight 需求高度
     */
    public synchronized void getBitmap(final String url, final BitmapCallback callback,int reqWidth,int reqHeight){
        //从内存缓存中获取bitmap
        final Bitmap bitmap = imageLrucache.loadBitmapFromMemCache(url);
        if(bitmap!=null){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onResponse(bitmap);
                }
            });
        return ;
        }
        LoadBitmapTask loadBitmapTask =new LoadBitmapTask(mContext,callback,url,reqWidth,reqHeight);
        //使用线程池去执行Runnable对象
        THREAD_POOL_EXECUTOR.execute(loadBitmapTask);

    }

    public void getBitmap(final String url,BitmapCallback callback){
       getBitmap(url,callback,0,0);

    }

    public void clearMemoryCache(){
        getImageLrucache().evictAll();
    }
    public interface BitmapCallback{
       public void onResponse(Bitmap bitmap);
    }
    public interface BindBitmapErrorCallBack{
        public void onError(ImageView imageView);
    }
}
