package com.example.pdfviewer;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
public class MainAdapter extends RecyclerView.Adapter<MainViewHolder> {
    public Context context;
    public ArrayList<PDFItem> pdfFiles;
    private OnPdfSelectedListener listener;



    public MainAdapter(Context context, ArrayList<PDFItem> pdfFiles, OnPdfSelectedListener listener) {
        this.context = context;
        this.pdfFiles = pdfFiles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.rv_items, parent, false);
        return new MainViewHolder(view);
//        return new MainViewHolder(LayoutInflater.from(context).inflate(R.layout.rv_items,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {

        PDFItem item = pdfFiles.get(position);
        holder.textName.setText(item.file_name);
        holder.doc_size.setText(item.file_size+"KB");
        holder.date_tv.setText(item.file_date.toString());
        holder.file_location.setText(item.file_location);
        holder.textName.setSelected(true);
        holder.pdf_imgView.setImageBitmap(item.pdf_bitmap);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnPdfSelected(pdfFiles.get(position));
            }
        });
        holder.option.setOnClickListener(new View.OnClickListener() {


            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void onClick(View view) {

              MainActivity.optionClicked(pdfFiles.get(position));
                Toast.makeText(context, "Option Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return pdfFiles.size();
    }



}
