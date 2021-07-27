package pt.unl.fct.di.example.sharencare.user.main_menu.ui.gallery;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;

public class ImageGallery {

    public static ArrayList<String> listOfImages(Context context){
        String galleryFolderName = "/Share&Care/";

        ArrayList<String> allImagesList = new ArrayList<>();

        File imagesFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), galleryFolderName);
        File[] files = imagesFolder.listFiles();

        if(files != null) {
            for (int i = 0; i < files.length; i++) {
                Uri uri = Uri.parse(files[i].toString());
                allImagesList.add(uri.getPath());
            }
        }

        return allImagesList;
    }
}
