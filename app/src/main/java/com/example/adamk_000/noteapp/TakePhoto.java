package com.example.adamk_000.noteapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Marek on 11. 9. 2016.
 */
public final class TakePhoto {
    public static final int TAKE_PICTURE = 1;
    private static String name;
    private static String nameBefore;
    private static int counter;
    private static File file;
    private static Uri outputFileUri;
    private static Intent intent;
    private static ExifInterface exif;
    private static ArrayList<String> names = new ArrayList<>();


    public static Intent preparePhotoIntent() {
        setFile(new File(MainActivity.getPath(), createName() + ".jpeg"));
        setOutputFileUri(Uri.fromFile(getFile()));

        //Generate intent
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getOutputFileUri());
        return intent;
    }

    public static boolean photoResults(int requestCode) {
        Bitmap thumbnail = NoteAdapter.makeThumbnail(name);
        if (requestCode == TAKE_PICTURE && thumbnail != null) {
            writeInformation();
            readNames(MainActivity.getPath(), getNames());
            return true;

        } else {
            return false;
        }
    }

    public static void restartIntent(Activity activity) {
        Intent restart = new Intent(activity, activity.getClass());
        activity.finish();
        activity.startActivity(restart);
    }

    private static String createName() {
        Calendar c = Calendar.getInstance();
        Date time = c.getTime();

        name = String.valueOf(time.getTime());
        if (name == getNameBefore()) {
            counter++;
            name = time.toString() + "_" + counter;
        } else {
            counter = 0;
        }
        setNameBefore(name);
        return name;
    }


    private static void writeInformation() {
        String latitude = saveLocation(getFile())[0];
        String longitude = saveLocation(getFile())[1];
        createTextFile(name + ".txt", MainActivity.getPath(), latitude, longitude);
        createNameFile(name, MainActivity.getPath());
    }

    private static String[] saveLocation(File file) {

        try {
            exif = new ExifInterface(String.valueOf(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        GeoDegree mGeo = new GeoDegree(exif, MainActivity.mLastLatitude, MainActivity.mLastLongitude);
        String[] returnString = new String[2];
        if (mGeo.isValid()) {
            returnString[0] = String.valueOf(mGeo.getLatitude());
            returnString[1] = String.valueOf(mGeo.getLongitude());
        } else {
            returnString[0] = MainActivity.mLastLatitude;
            returnString[1] = MainActivity.mLastLongitude;
        }
        return returnString;
    }

    private static void createTextFile(String fileName, File path, String latitude, String longitude) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.getPath() + "/" + fileName))) {
            writer.write(fileName);
            writer.newLine();
            writer.write(latitude);
            writer.newLine();
            writer.write(longitude);
            writer.newLine();
        } catch (IOException e) {
        }
    }

    private static void createNameFile(String fileName, File path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.getPath() + "/names.txt", true))) {

            writer.append(fileName);
            writer.newLine();
        } catch (IOException e) {
        }
    }

    private static void readNames(File path, ArrayList<String> arrayList) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.getPath() + "/names.txt"))) {
            String add = reader.readLine();
            while (add != null) {
                arrayList.add(add);
                add = reader.readLine();
            }
        } catch (IOException e) {
        }
    }


    public static void setName(String name) {
        TakePhoto.name = name;
    }

    public static String getNameBefore() {
        return nameBefore;
    }

    public static void setNameBefore(String nameBefore) {
        TakePhoto.nameBefore = nameBefore;
    }

    public static File getFile() {
        return file;
    }

    public static void setFile(File file) {
        TakePhoto.file = file;
    }

    public static Uri getOutputFileUri() {
        return outputFileUri;
    }

    public static void setOutputFileUri(Uri outputFileUri) {
        TakePhoto.outputFileUri = outputFileUri;
    }

    public static ArrayList<String> getNames() {
        return names;
    }

    public static void setNames(ArrayList<String> names) {
        TakePhoto.names = names;
    }
}
