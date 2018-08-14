package com.futureworkshops.datacap.common.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.media.ExifInterface;

import com.futureworkshops.datacap.common.utils.BitmapUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.Completable;
import io.reactivex.Observable;


/**
 * Created by stelian on 07/12/2016.
 */

public class FileManager {

    public static final String BATCH_FOLDER = "datacap_batch";

    private File mBatchFolder;

    private Context mContext;

    public FileManager(@NonNull Context context) {
        mContext = context;
        mBatchFolder = new File(context.getFilesDir(), BATCH_FOLDER);

        if (!mBatchFolder.exists()) {
            mBatchFolder.mkdirs();
        }
    }

    /**
     * Get the folder where all Batches will be saved.
     *
     * @return
     */
    public File getBatchRootFolder() {
        return mBatchFolder;
    }

    /**
     * Add an image to internal storage of the app.
     */
    public Observable<File> saveImageFile(@NonNull final String batchId, @NonNull final String pageId, final Bitmap image) {
        return Observable.fromCallable(() -> saveImage(batchId, pageId, image));
    }

    public File saveImage(@NonNull final String batchId, @NonNull final String pageId, final Bitmap image) throws IOException {
        File batchFolder = new File(mBatchFolder, batchId);
        if (!batchFolder.exists()) {
            batchFolder.mkdir();
        }
        String filename = pageId + ".jpg";

        File img = new File(batchFolder, filename);
        FileOutputStream fos = new FileOutputStream(img);

        image.compress(Bitmap.CompressFormat.JPEG, 100, fos);

        fos.flush();
        fos.close();

        return img;
    }

    /**
     * Save an image to a file.
     */
    public Observable<File> saveImageFile(@NonNull final String batchId, @NonNull final String pageId,
                                          final byte[] imageData, final int cameraOrientation) {
        return Observable.fromCallable(() -> {
            File batchFolder = new File(mBatchFolder, batchId);
            String filename = pageId + ".jpg";

            File img = new File(batchFolder, filename);

            Bitmap bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            final Bitmap correctedImage = BitmapUtils.rotateBitmap(bmp, cameraOrientation);

            // overwrite existing file to save corrected image
            FileOutputStream fos = new FileOutputStream(img);
            correctedImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();

            return img;
        });
    }

    public void copyFile(File source, File destination) throws IOException {
        InputStream in = new FileInputStream(source);
        OutputStream out = new FileOutputStream(destination);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    /**
     * Move an image to internal storage of the app and delete the source File.
     */
    public Observable<File> moveImageFile(@NonNull final String batchId, @NonNull final String pageId, final String sourcePath) {
        return Observable.fromCallable(() -> {
            File batchFolder = new File(mBatchFolder, batchId);
            String filename = pageId + ".jpg";

            File newImageFile = new File(batchFolder, filename);

            final File sourceImage = new File(sourcePath);
            copyFile(sourceImage, newImageFile);

            deleteImage(sourceImage);

            return newImageFile;
        });
    }


    /**
     * Import an image to internal storage of the app.
     */
    public Observable<File> importImageFile(@NonNull final String batchId,
                                            @NonNull final String pageId,
                                            final String imagePath) {
        return Observable.fromCallable(() -> {
            File batchFolder = new File(mBatchFolder, batchId);
            String filename = pageId + ".jpg";

            File output = new File(batchFolder, filename);

            final Uri imageUri = Uri.parse(imagePath);
            InputStream inputStream = mContext.getContentResolver().openInputStream(imageUri);
            OutputStream out = new FileOutputStream(output);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            inputStream.close();
            out.close();

            final ExifInterface exif = new ExifInterface(output.getAbsolutePath());
//
            // get rotation from exif tag
            final int rotation = getRotationAngleFromExif(exif);

            // rotate the "image"
            if (rotation > 0) {
                exif.resetOrientation();
                exif.saveAttributes();

                final Bitmap originalImage = BitmapFactory.decodeFile(output.getAbsolutePath());

                // create rotated bitmap
                final Matrix matrix = new Matrix();
                matrix.postRotate(rotation);

                final Bitmap rotated = Bitmap.createBitmap(originalImage, 0, 0,
                        originalImage.getWidth(), originalImage.getHeight(), matrix,
                        true);

                originalImage.recycle();

                final FileOutputStream fos = new FileOutputStream(output);
                rotated.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                rotated.recycle();

            }

            return output;
        });

    }

    /**
     * Overwrite an existing image.
     *
     * @param imagePath
     * @param deskewedBitmap
     * @return
     */
    public Completable overwriteImageFile(String imagePath, Bitmap deskewedBitmap) {
        return Completable.fromCallable(() -> {
            File output = new File(imagePath);
            FileOutputStream fos = new FileOutputStream(output, false);

            deskewedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();

            return output;
        });
    }

    /**
     * Delete a file .
     */
    public boolean deleteImage(File imageFile) throws SecurityException {
        if (imageFile.exists()) {
            return imageFile.delete();
        }

        return false;
    }

    public void deleteBatchFolder() {
        for (File file : mBatchFolder.listFiles()) {
            if (file.isDirectory()) {
                for (File subfile : file.listFiles()) {
                    subfile.delete();
                }
            } else {
                file.delete();
            }
        }
    }

    private int getRotationAngleFromExif(ExifInterface exif) {
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int rotate = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                rotate = 0;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
        }

        return rotate;
    }

}
