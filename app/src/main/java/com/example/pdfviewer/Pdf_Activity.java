package com.example.pdfviewer;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.File;

public class Pdf_Activity extends AppCompatActivity {

    String filePath = "";
    int width = 1000;
    int height = 1000;
    Canvas bigCanvas;
    int scrollPosX = 0;
    int scrollPosY = 0;
    public  PDFView pdfView;
    TextView page_no,pdf_nameTv;

    Button share,pages,button_cancel,ok_btn,viewMode,horizontal_btn,vertical_btn;
    File file;
    CardView page_cardView,view_mode_card;
    EditText editTextNumber;

    RelativeLayout toplayout,bottom_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pdf);
        toplayout = findViewById(R.id.toplayout);
        bottom_layout = findViewById(R.id.bottom_layout);
        horizontal_btn = findViewById(R.id.horizontal_btn);
        vertical_btn = findViewById(R.id.vertical_btn);
        page_cardView = findViewById(R.id.page_cardView);
        view_mode_card = findViewById(R.id.view_mode_card);
        viewMode = findViewById(R.id.viewMode);
        ok_btn = findViewById(R.id.ok_btn);
        editTextNumber = findViewById(R.id.editTextNumber);
        button_cancel = findViewById(R.id.button_cancel);

        pages = findViewById(R.id.pages);
        share = findViewById(R.id.share);
        pdf_nameTv = findViewById(R.id.pdf_nameTv);
        page_no = findViewById(R.id.page_no);
        pdfView = findViewById(R.id.pdfView);

        filePath = getIntent().getStringExtra("path");
        file= new File(filePath);
        pdf_nameTv.setText(file.getName());

        pdfView.fromFile(file)
                .enableDoubletap(true)

                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        updatePageNumber(page, pageCount);


                    }
                })
                .pageFitPolicy(FitPolicy.BOTH)
                .pageSnap(true)  // Enable snapping to pages
                .swipeHorizontal(false)  // Enable horizontal swipe
                .enableSwipe(true)
                .spacing(10)
                .enableDoubletap(true)
                .load();



        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSharePdf(view.getContext());
            }
        });
        pdfView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animation();
            }
        });


        pages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextNumber.setSelected(true);
                page_cardView.setVisibility(VISIBLE);
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                page_cardView.setVisibility(View.GONE);



            }
        });
        viewMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_mode_card.setVisibility(VISIBLE);

            }
        });
        horizontal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleViewMode(view.getContext());
                view_mode_card.setVisibility(View.GONE);
            }
        });

        vertical_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleViewMode(view.getContext());
                view_mode_card.setVisibility(View.GONE);
            }
        });

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextNumber.setSelected(false);
                page_cardView.setVisibility(View.GONE);
                int jumpPage = Integer.parseInt(editTextNumber.getText().toString());
                goToPage(jumpPage);

            }
        });
    }

    public void animation(){

        if(page_cardView.getVisibility()== VISIBLE){
            page_cardView.setVisibility(View.GONE);
            toplayout.setVisibility(VISIBLE);
            bottom_layout.setVisibility(VISIBLE);
        }

        if(view_mode_card.getVisibility()== VISIBLE){
            view_mode_card.setVisibility(View.GONE);
            toplayout.setVisibility(VISIBLE);
            bottom_layout.setVisibility(VISIBLE);
        }
        if(toplayout.getVisibility()== VISIBLE){
            toplayout.setVisibility(View.GONE);
            bottom_layout.setVisibility(View.GONE);
        }
        else {
            toplayout.setVisibility(VISIBLE);
            bottom_layout.setVisibility(VISIBLE);
        }
    }
    private void toggleViewMode(Context context) {
        if (pdfView != null) {
            // Toggle between horizontal and vertical swipe directions
            if (pdfView.isSwipeVertical()) {
                pdfView.recycle(); // Recycle the current PDFView
                pdfView.fromFile(file)
                        .onPageChange(new OnPageChangeListener() {
                            @Override
                            public void onPageChanged(int page, int pageCount) {
                                updatePageNumber(page, pageCount);


                            }
                        })
                        .swipeHorizontal(true)
                        .load(); // Load the PDF with vertical swipe
            } else {
                pdfView.recycle(); // Recycle the current PDFView
                pdfView.fromFile(file)
                        .onPageChange(new OnPageChangeListener() {
                            @Override
                            public void onPageChanged(int page, int pageCount) {
                                updatePageNumber(page, pageCount);

                            }
                        })
                        .swipeHorizontal(false)
                        .load(); // Load the PDF with horizontal swipe
            }
        }
    }

    public void onSharePdf(Context context){
        File shareFile = new File(file.getPath());
        Uri uri;
        if(shareFile.exists()){
            uri = FileProvider.getUriForFile(context, "com.example.pdfviewer.fileProvider", shareFile);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            context.startActivity(Intent.createChooser(shareIntent, "Share File"));
        }else {
            Toast.makeText(this, "PDF file not found", Toast.LENGTH_SHORT).show();

        }
    }
    private void goToPage(int page) {
        try {

            pdfView.jumpTo(page - 1); // PDF pages are zero-indexed
        } catch (NumberFormatException e) {
            // Handle invalid input (not a number)
            e.printStackTrace();
        }
    }




    private void updatePageNumber(int currentPage, int pageCount) {
        String pageNumberText = "Page " + (currentPage + 1) + " of " + pageCount;
        page_no.setText(pageNumberText);
    }

}
