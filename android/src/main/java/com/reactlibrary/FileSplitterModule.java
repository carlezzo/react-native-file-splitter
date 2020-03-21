package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;

public class FileSplitterModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public static final long floppySize = (long)(1.4 * 1024 * 1024);

    public FileSplitterModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "FileSplitter";
    }

    @ReactMethod
    public void sampleMethod(String filepath, Integer chunkSize, Callback callback) {

        try {
            // open the file
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(filepath));

            // get the file length
            File f = new File(filepath);
            long fileSize = f.length();

            int subfile;

            for (subfile = 0; subfile < fileSize / chunkSize; subfile++)
            {
                // open the output file
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("/data/user/0/com.testapp/files/foo_" + subfile));

                // write the right amount of bytes
                for (int currentByte = 0; currentByte < chunkSize; currentByte++)
                {
                    // load one byte from the input file and write it to the output file
                    out.write(in.read());
                }

                // close the file
                out.close();
            }

            // loop for the last chunk (which may be smaller than the chunk size)
            if (fileSize != chunkSize * (subfile - 1))
            {
                // open the output file
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("/data/user/0/com.testapp/files/foo_" + subfile));

                // write the rest of the file
                int b;
                while ((b = in.read()) != -1)
                    out.write(b);

                // close the file
                out.close();
            }

            in.close();

            join("/data/user/0/com.testapp/files/foo");

            callback.invoke("ok");
        } catch(Exception e) {
            System.out.println("------------------------ teste");
            System.out.println(e.getMessage());
        }
    }

    public static void join(String baseFilename) throws IOException
    {
        // int numberParts = getNumberParts(baseFilename);
        int numberParts = 24;

        System.out.println("------------------------ ");
        System.out.println(numberParts);
        System.out.println("------------------------ ");

        // now, assume that the files are correctly numbered in order (that some joker didn't delete any part)
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(baseFilename));
        for (int part = 0; part < numberParts; part++)
        {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(baseFilename + "_" + part));

            int b;
            while ( (b = in.read()) != -1 )
                out.write(b);

            in.close();
        }
        out.close();
    }

    private static int getNumberParts(String baseFilename) throws IOException
    {
        // list all files in the same directory
        File directory = new File("/data/user/0/com.testapp/files/foo_1").getAbsoluteFile().getParentFile();
        final String justFilename = new File("/data/user/0/com.testapp/files/foo").getName();
        String[] matchingFiles = directory.list(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.startsWith(justFilename) && name.substring(justFilename.length()).matches("^\\.\\d+$");
            }
        });
        return matchingFiles.length;
    }
}
