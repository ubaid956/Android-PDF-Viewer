package com.example.pdfviewer;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;

import java.io.File;

public class PDFItem {
    String file_name;
    String file_path;
    String file_size;

    Bitmap pdf_bitmap;
    String file_date;
    public PDFItem(){

    }


    public Bitmap getPdf_bitmap() {
        return pdf_bitmap;
    }

    public void setPdf_bitmap(File file) {
        Bitmap bitmap = null;
        try {
            PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));
            final int pageCount = renderer.getPageCount();
            if(pageCount>0){
                PdfRenderer.Page page = renderer.openPage(0);

                int width = 150;
                int height = 200;
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                page.close();
                renderer.close();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        this.pdf_bitmap = bitmap;
    }
    public void setFile_location(String file_location) {
        this.file_location = file_location;
    }

    String file_location;

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }



    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }



    public void setFile_date(String file_date) { // Change the parameter type to String
        this.file_date = file_date;
    }






}
