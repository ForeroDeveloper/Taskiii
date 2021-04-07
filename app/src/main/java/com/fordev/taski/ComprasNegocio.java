package com.fordev.taski;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ComprasNegocio extends AppCompatActivity {

    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    //List all permission required
    public static String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static int PERMISSION_ALL = 12;


    List<Constants> paymentUsersList;
    public static File pFile;
    private File payfile;
    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compras_negocio);
        pdfView = findViewById(R.id.payment_pdf_viewer);

        paymentUsersList = new ArrayList<>();

        Button reportButton = findViewById(R.id.button_disable_report);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                int permisoRead = ContextCompat.checkSelfPermission(ComprasNegocio.this,Manifest.permission.READ_EXTERNAL_STORAGE);
                int permisoWrite = ContextCompat.checkSelfPermission(ComprasNegocio.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permisoRead == PackageManager.PERMISSION_GRANTED && permisoWrite == PackageManager.PERMISSION_GRANTED){
                    Toasty.info(ComprasNegocio.this,"Cargando...",Toast.LENGTH_LONG, true).show();


                    payfile = new File(Environment.getExternalStorageDirectory(), "Report/");

                    //check if they exist, if not create them(directory)
                    if ( !payfile.exists()) {
                        payfile.mkdir();
                    }
                    //create files in charity care folder

                    pFile = new File(payfile, "PaymentUsers.pdf");

                    fetchPaymentUsers();
                    DisplayReport();

                }else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
                }

//                previewDisabledUsersReport();
            }
        });

        databaseReference = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("facturas").
                child("facturasCreadas");
        databaseReference.keepSynced(true);







        //fetch payment and disabled users details;


    }

    private void fetchPaymentUsers() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

//creating an object and setting to displlay
                    Constants pays = new Constants();
                    pays.setNombreProveedor(snapshot.child("metodoDePago").getValue().toString());
                    pays.setNombreProducto(snapshot.child("cliente").getValue().toString());
                    pays.setCantidad(snapshot.child("day").getValue().toString());
                    pays.setPrecioCosto(snapshot.child("abonado").getValue().toString());
                    pays.setPrecioVenta(snapshot.child("abonado").getValue().toString());
                    pays.setUtilidad(snapshot.child("day").getValue().toString());
                    pays.setTotalcosto(snapshot.child("abonado").getValue().toString());

                    //this just log details fetched from db(you can use Timber for logging
                    Log.d("Payment", "Name: " + pays.getNombreProveedor());
                    Log.d("Payment", "othername: " + pays.getNombreProducto());
                    Log.d("Payment", "phone: " + pays.getCantidad());

                    /* The error before was cause by giving incorrect data type
                    You were adding an object of type PaymentUsers yet the arraylist expects obejct of type DisabledUsers
                     */
                    paymentUsersList.add(pays);


                }
                //create a pdf file and catch exception beacause file may not be created
                try {
                    createPaymentReport(paymentUsersList);
                } catch (DocumentException | FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createPaymentReport(  List<Constants> paymentUsersList) throws DocumentException, FileNotFoundException{
        BaseColor colorWhite = WebColors.getRGBColor("#ffffff");
        BaseColor colorBlue = WebColors.getRGBColor("#056FAA");
        BaseColor grayColor = WebColors.getRGBColor("#425066");



        Font white = new Font(Font.FontFamily.HELVETICA, 15.0f, Font.BOLD, colorWhite);
        FileOutputStream output = new FileOutputStream(pFile);
        Document document = new Document(PageSize.A2);
        PdfPTable table = new PdfPTable(new float[]{6, 20, 20, 10,15,15,10,15});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setFixedHeight(50);
        table.setTotalWidth(PageSize.A2.getWidth());
        table.setWidthPercentage(100);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

        Chunk noText = new Chunk("No.", white);
        PdfPCell noCell = new PdfPCell(new Phrase(noText));
        noCell.setFixedHeight(50);
        noCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        noCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk nameText = new Chunk("Proveedor", white);
        PdfPCell nameCell = new PdfPCell(new Phrase(nameText));
        nameCell.setFixedHeight(50);
        nameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        nameCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk phoneText = new Chunk("Producto", white);
        PdfPCell phoneCell = new PdfPCell(new Phrase(phoneText));
        phoneCell.setFixedHeight(50);
        phoneCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        phoneCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk amountText = new Chunk("Cantidad", white);
        PdfPCell amountCell = new PdfPCell(new Phrase(amountText));
        amountCell.setFixedHeight(50);
        amountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        amountCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk costo = new Chunk("Costo Unit.", white);
        PdfPCell costoproduc = new PdfPCell(new Phrase(costo));
        costoproduc.setFixedHeight(50);
        costoproduc.setHorizontalAlignment(Element.ALIGN_CENTER);
        costoproduc.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk costotxt = new Chunk("P. Venta", white);
        PdfPCell costoproducto = new PdfPCell(new Phrase(costotxt));
        costoproducto.setFixedHeight(50);
        costoproducto.setHorizontalAlignment(Element.ALIGN_CENTER);
        costoproducto.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk utilidad_txt = new Chunk("% Utilidad", white);
        PdfPCell utilidad_producto = new PdfPCell(new Phrase(utilidad_txt));
        utilidad_producto.setFixedHeight(50);
        utilidad_producto.setHorizontalAlignment(Element.ALIGN_CENTER);
        utilidad_producto.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk total_costo_txt = new Chunk("Costo Total", white);
        PdfPCell total_costo = new PdfPCell(new Phrase(total_costo_txt));
        total_costo.setFixedHeight(50);
        total_costo.setHorizontalAlignment(Element.ALIGN_CENTER);
        total_costo.setVerticalAlignment(Element.ALIGN_CENTER);


        Chunk footerText = new Chunk("Creado con taski - Copyright @ 2021");
        PdfPCell footCell = new PdfPCell(new Phrase(footerText));
        footCell.setFixedHeight(70);
        footCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        footCell.setVerticalAlignment(Element.ALIGN_CENTER);
        footCell.setColspan(4);


        table.addCell(noCell);
        table.addCell(nameCell);
        table.addCell(phoneCell);
        table.addCell(amountCell);
        table.addCell(costoproduc);
        table.addCell(costoproducto);
        table.addCell(utilidad_producto);
        table.addCell(total_costo);
        table.setHeaderRows(1);

        PdfPCell[] cells = table.getRow(0).getCells();


        for (PdfPCell cell : cells) {
            cell.setBackgroundColor(grayColor);
        }
        for (int i = 0; i < paymentUsersList.size(); i++) {
            Constants pay = paymentUsersList.get(i);

            String id = String.valueOf(i + 1);
            String name = pay.getNombreProveedor();
            String sname = pay.getNombreProducto();
            String phone = pay.getCantidad();
            String cost = pay.getPrecioCosto();
            String pventa = pay.getPrecioVenta();
            String utilidades = pay.getUtilidad();
            String total_c = pay.getTotalcosto();


            table.addCell(id + ". ");
            table.addCell(name);
            table.addCell(sname);
            table.addCell(phone);
            table.addCell(cost);
            table.addCell(pventa);
            table.addCell(utilidades);
            table.addCell(total_c);

        }

        PdfPTable footTable = new PdfPTable(new float[]{6, 25, 20, 20});
        footTable.setTotalWidth(PageSize.A2.getWidth());
        footTable.setWidthPercentage(100);
        footTable.addCell(footCell);

        PdfWriter.getInstance(document, output);
        document.open();
        Font g = new Font(Font.FontFamily.HELVETICA, 25.0f, Font.NORMAL, grayColor);
        document.add(new Paragraph(" Reporte Financiero\n\n", g));
        document.add(table);
        document.add(footTable);

        document.close();
    }


   /* public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void previewDisabledUsersReport()
    {
        if (hasPermissions(this, PERMISSIONS)) {
            DisplayReport();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }*/

    private void DisplayReport()
    {
        pdfView.fromFile(pFile)
                .pages(0,2,1,3,3,3)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .load();
    }


}