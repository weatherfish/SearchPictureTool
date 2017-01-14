package com.example.administrator.searchpicturetool.widght.imageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import com.example.administrator.searchpicturetool.config.API;
import com.jakewharton.disklrucache.DiskLruCache;
import com.jude.utils.JUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 图片本地缓存类
 * Created by wenhuaijun on 2016/4/23 0023.
 */
public class ImageDiskLrucache {
    public static final String TAG ="TAG";
    private static final long DISK_CACHE_SIZE = 1024*1024*80;
    private static final int DISK_CACHE_INDEX = 0;
    private DiskLruCache mDiskLruCache;
    private  boolean mIsDiskLruCacheCreated;

    public ImageDiskLrucache(Context mContext){
        File diskCacheDir = getDidkCacheDir(mContext, "bitmap");
        if(!diskCacheDir.exists()){
            diskCacheDir.mkdirs();
        }

        //sd卡的可用空间大于本地缓存的大小
        if(getUsableSpace(diskCacheDir)>DISK_CACHE_SIZE){
            //实例化diskLrucache
            try {
                mDiskLruCache =DiskLruCache.open(diskCacheDir,1,1,DISK_CACHE_SIZE);
                mIsDiskLruCacheCreated =true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            mIsDiskLruCacheCreated =false;
        }

    }

    /**
     * 从本地缓存加载bitmap
     * @param url
     * @param reqWidth
     * @param reqHeight
     * @return
     * @throws IOException
     */
    public Bitmap loadBitmapFromDiskCache(String url,int reqWidth,int reqHeight) throws IOException {
        if(mDiskLruCache ==null){
            return null;
        }
        Bitmap bitmap=null;
        String key = MD5Utils.hashKeyFromUrl(url);
        DiskLruCache.Snapshot snapshot =mDiskLruCache.get(key);
        if(snapshot!=null){
            FileInputStream fileInputStream =(FileInputStream)snapshot.getInputStream(DISK_CACHE_INDEX);
            if(reqWidth<=0||reqHeight<=0){
                //不压缩图片
                BitmapFactory.Options
                        bfOptions=new BitmapFactory.Options();
                bfOptions.inDither=false;
                bfOptions.inPurgeable=true;
                bfOptions.inTempStorage=new byte[12 *1024];
                try{
                    bitmap = BitmapFactory.decodeFileDescriptor(fileInputStream.getFD(),null,bfOptions);
                }catch (Throwable e){
                    bitmap =null;
                    JUtils.Log("ImageDiskLrucache->BitmapFactory.decodeFileDescriptor Exception!");
                }

            }else{
                //按需求分辨率压缩图片
                try{
                bitmap =BitmapUtils.getSmallBitmap(fileInputStream.getFD(),reqWidth,reqHeight);
                }catch (Exception e){
                    bitmap =null;
                    JUtils.Log("ImageDiskLrucache->BitmapUtils.getSmallBitmap Exception!");
                }
            }
        }
        return bitmap;
    }

    /**
     * 下载图片放入本地缓存
     * @param urlString 下载图片的链接
     * @throws IOException
     */
    public void downloadImageToDiskCache(String urlString) throws IOException {
        if(mDiskLruCache == null){
            return ;
        }
        String key =MD5Utils.hashKeyFromUrl(urlString);
        DiskLruCache.Editor editor =mDiskLruCache.edit(key);
        if(editor != null){
            //打开本地缓存的输入流
            OutputStream outputStream =editor.newOutputStream(DISK_CACHE_INDEX);
            //将从网络下载并写入输出流中
            if(NetRequest.downloadUrlToStream(urlString,outputStream)){
                //提交数据，并是释放连接
                editor.commit();
            }else{
                //释放连接
                editor.abort();
            }
            mDiskLruCache.flush();
        }
    }


    //获取本地缓存的File目录
    public File getDidkCacheDir(Context context, String uniqueName){
        //判断是否含有sd卡
        boolean externalStorageAvailable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        final String cachePath;
        if(externalStorageAvailable&&context.getExternalCacheDir()!=null){
            cachePath =context.getExternalCacheDir().getPath();
        }else if(context.getCacheDir()!=null){
            //获取app自带的缓存目录
            cachePath =context.getCacheDir().getPath();
        }else {
            cachePath = API.diskLrucacheCachePath;
        }

        return new File(cachePath +File.separator+uniqueName);
    }
    //获取该目录可用空间大小
    private long getUsableSpace(File path){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.GINGERBREAD){
            return path.getUsableSpace();
        }
        final StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize()*(long) stats.getAvailableBlocks();
    }

    public DiskLruCache getmDiskLruCache() {
        return mDiskLruCache;
    }

    public boolean ismIsDiskLruCacheCreated() {
        return mIsDiskLruCacheCreated;
    }
}
