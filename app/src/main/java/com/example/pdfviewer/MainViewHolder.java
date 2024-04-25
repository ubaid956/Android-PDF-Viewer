package com.example.pdfviewer;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.barteksc.pdfviewer.PDFView;

public class MainViewHolder extends RecyclerView.ViewHolder {


    public TextView textName, date_tv, doc_size, file_location;
    public CardView cardView;
    public TextView address;
    ImageView pdf_imgView, option;
    public MainViewHolder(@NonNull View itemView) {
        super(itemView);
        doc_size = itemView.findViewById(R.id.doc_size);
        file_location = itemView.findViewById(R.id.file_location);
        address = itemView.findViewById(R.id.address);
        date_tv = itemView.findViewById(R.id.date_tv);
        textName = itemView.findViewById(R.id.pdf_txtName);
        cardView = itemView.findViewById(R.id.pdf_cardView);
        pdf_imgView = itemView.findViewById(R.id.pdf_imgView);
        option = itemView.findViewById(R.id.option);
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}
