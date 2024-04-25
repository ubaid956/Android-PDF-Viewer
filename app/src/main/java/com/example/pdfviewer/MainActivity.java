package com.example.pdfviewer;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import androidx.appcompat.widget.Toolbar;

@RequiresApi(api = Build.VERSION_CODES.R)
public class MainActivity extends AppCompatActivity implements OnPdfSelectedListener{

    public MainAdapter adapter;
    PDFItem pdfItem;
    public static  RecyclerView recyclerView;
    DrawerLayout drawerLayout;

    Toolbar tool_bar;
    NavigationView navigationView;
    public static ImageView option_img;
   public static TextView option_name,option_path;
    public static CardView option_cardView;

    public static Button option_share;
    static ArrayList<PDFItem> pdfItems = new ArrayList<>();

    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String[] permission_10 = {"android.permission.READ_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);
        setContentView(R.layout.activity_main);
        option_img = findViewById(R.id.option_img);
        option_share = findViewById(R.id.option_share);
        option_name = findViewById(R.id.option_name);
        option_path = findViewById(R.id.option_path);
        drawerLayout = findViewById(R.id.drawer_layout);
//        if(nightMode){
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            if (Environment.isExternalStorageManager()) {
                loadPDFs();
                displayPdf();
            } else {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                    startActivityForResult(intent, 200);
                } catch (Exception e) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, 200);
                    e.getStackTrace();
                }
            }

        }
        else {
            if(!checkPermission()){
                requestPermissions(permission_10, 123);
            }
            else {
                displayPdf();
            }
        }

        tool_bar = findViewById(R.id.tool_bar);
        navigationView = findViewById(R.id.navigation_view);
        option_cardView = findViewById(R.id.option_cardView);

        setSupportActionBar(tool_bar);
        ActionBarDrawerToggle toogle =new ActionBarDrawerToggle(this, drawerLayout, tool_bar, R.string.OpenDrawer, R.string.CloseDrawer);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id =item.getItemId();
                if(id ==R.id.theme){
                    if(nightMode){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        editor = sharedPreferences.edit();
                        editor.putBoolean("night", false);
                        editor.apply();
                    }
                    else{
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        editor = sharedPreferences.edit();
                        editor.putBoolean("night", true);
                        editor.apply();
                    }
                    Toast.makeText(MainActivity.this, "Theme is open", Toast.LENGTH_SHORT).show();

                }
                else if(id == R.id.privacy_policy){
                    loadFragment(new fragment_privacy());
                    Toast.makeText(MainActivity.this, "Privacy is open", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.about){
                    loadFragment(new About_Fragment());
//
                    Toast.makeText(MainActivity.this, "Privacy is open", Toast.LENGTH_SHORT).show();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });


        MenuItem themeItem = navigationView.getMenu().findItem(R.id.theme);
        if (nightMode) {
            themeItem.setIcon(R.drawable.light_mode);
        } else {
            themeItem.setIcon(R.drawable.baseline_nightlight_round_24);
        }
    }


    public void loadPDFs(){
        File docDir = Environment.getExternalStorageDirectory();
        pdfItems = getPdfFiles(docDir);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 123) {
            if(grantResults.length>0){

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadPDFs();
                    displayPdf();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public void loadFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment, null)
                .setReorderingAllowed(true)
                .addToBackStack("name") // Name can be null
                .commit();
    }
    public  static void optionClicked(PDFItem file){
        File file1 = new File(file.getFile_path());
        if(option_cardView.getVisibility()== View.GONE){
            option_cardView.setVisibility(View.VISIBLE);
            option_name.setText(file.getFile_name());
            option_path.setText(file.getFile_path());
            option_img.setImageBitmap(file.getPdf_bitmap());
            option_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSharePdf(view.getContext(), file1);
                }
            });


        }
        else if(option_cardView.getVisibility()== View.VISIBLE){
            option_cardView.setVisibility(View.GONE);
        }
    }

    public static void onSharePdf(Context context, File file) {
        Uri uri;
        if (file.exists()) {
            uri = FileProvider.getUriForFile(context, "com.example.pdfviewer.fileProvider", file);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            context.startActivity(Intent.createChooser(shareIntent, "Share File"));
        } else {
            Toast.makeText(context, "PDF file not found", Toast.LENGTH_SHORT).show();
        }
    }

    void requestPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, 200);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 200);
                e.getStackTrace();
            }

        }

    }

    boolean checkPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            return Environment.isExternalStorageManager();
        }
        else {
            int filePermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return filePermission == PackageManager.PERMISSION_GRANTED;
        }
    }

    private ArrayList<PDFItem> getPdfFiles(File pdfDir){
        ArrayList<PDFItem> myPdf = new ArrayList<>();
        String fileText = ".pdf";
        try{
            File[] listPdfFiles = pdfDir.listFiles();
            if(listPdfFiles!=null){
                for(File file: listPdfFiles){
                    if(file.isDirectory()){
                        myPdf.addAll(getPdfFiles(file));
                    }
                    else if(file.getName().toLowerCase().endsWith(fileText)){
                        pdfItem = new PDFItem();
                        pdfItem.setFile_name(file.getName());
                        pdfItem.setFile_path(file.getAbsolutePath());
                        java.util.Date date_toFormat = new java.util.Date(file.lastModified());
                        String newDate = formatDate(date_toFormat);
                        pdfItem.setFile_date(newDate);
                        long pdf_length = file.length();
                        long fileSizeInMegabytes = pdf_length / (1024);
                        pdfItem.setFile_size(String.valueOf(fileSizeInMegabytes));
                        String file_path = file.getPath();
                        String[] pathComponents = file_path.split("/");
                        String downloadPath = pathComponents[pathComponents.length - 2];
                        pdfItem.setFile_location(downloadPath);
                        pdfItem.setPdf_bitmap(file.getAbsoluteFile());
                        myPdf.add(pdfItem);
                        Log.d("PDF FILES", file.getName());
                    }
                }
                Toast.makeText(this, "Pdf Files are: "+myPdf.size(), Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return myPdf;

    }


    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }



    public void displayPdf(){

        recyclerView = findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MainAdapter(this, pdfItems, this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void OnPdfSelected(PDFItem file) {
        startActivity(new Intent(MainActivity.this, Pdf_Activity.class)
                .putExtra("path",file.getFile_path()));
    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
                loadPDFs();
                displayPdf();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }




}