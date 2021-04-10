package com.fordev.taski;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fordev.taski.adaptadores.AdaptadorListaComprasNegocio;
import com.fordev.taski.adaptadores.AdaptadorListaFacturas;
import com.fordev.taski.modelos.ModeloComprasNegocio;
import com.fordev.taski.modelos.ModeloFacturaCreada;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.button.MaterialButton;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ComprasNegocio extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    List<Constants> paymentUsersList;
    public static File pFile;
    private File payfile;
    private PDFView pdfView;
    MaterialButton faq_nueva_compra;
    int total = 0;
    NumberFormat nformat = new DecimalFormat("##,###,###.##");
    String nombrePdf;
    RecyclerView listaCompras;
    AdaptadorListaComprasNegocio adaptadorListaCompras;
    ImageView ic_sumar_fecha, ic_restar_fecha;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("MM");
    TextView fechaActual;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compras_negocio);
        pdfView = findViewById(R.id.payment_pdf_viewer);
        faq_nueva_compra = findViewById(R.id.faq_nueva_compra);
        listaCompras = findViewById(R.id.lista_de_compras);
        ic_sumar_fecha = findViewById(R.id.ic_sumar_fecha);
        ic_restar_fecha = findViewById(R.id.ic_restar_fehca);
        fechaActual = findViewById(R.id.txtFechaSelect);
        Button reportButton = findViewById(R.id.button_disable_report);

        calendar.get(Calendar.MONTH);
        fechaActual.setText(sdf.format(calendar.getTime()));

        databaseReference = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Compras").
                child("lista");

        int permisoRead = ContextCompat.checkSelfPermission(ComprasNegocio.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permisoWrite = ContextCompat.checkSelfPermission(ComprasNegocio.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permisoRead != PackageManager.PERMISSION_GRANTED && permisoWrite != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
        }

        reportButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                int permisoRead = ContextCompat.checkSelfPermission(ComprasNegocio.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                int permisoWrite = ContextCompat.checkSelfPermission(ComprasNegocio.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permisoRead == PackageManager.PERMISSION_GRANTED && permisoWrite == PackageManager.PERMISSION_GRANTED) {

                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Constants pays = new Constants();

                                String fecha = snapshot.child("month").getValue().toString();

                                pays.setNombreProveedor(snapshot.child("nombreProveedor").getValue().toString());
                                pays.setNombreProducto(snapshot.child("nombreProducto").getValue().toString());
                                pays.setCantidad(snapshot.child("cantidadProducto").getValue().toString());
                                pays.setPrecioCosto(snapshot.child("precioCosto").getValue().toString());
                                pays.setPrecioVenta(snapshot.child("precioVenta").getValue().toString());
                                pays.setUtilidad(snapshot.child("utilidad").getValue().toString());
                                pays.setTotalcosto(snapshot.child("costoTotal").getValue().toString());
                                pays.setFechaRegistro(snapshot.child("fechaRegistro").getValue().toString());
                                if (snapshot.child("pagado").getValue().toString().equals("true")){
                                    pays.setPagado("Pagado");
                                }else {
                                    pays.setPagado("Por Pagar");
                                }

                                if (fecha.equals(fechaActual.getText().toString())){
                                    total += Integer.parseInt(snapshot.child("costoTotal").getValue().toString());
                                    paymentUsersList.add(pays);
                                }


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
                    payfile = new File(Environment.getExternalStorageDirectory(), "Report/");

                    paymentUsersList = new ArrayList<>();
                    //check if they exist, if not create them(directory)
                    if (!payfile.exists()) {
                        payfile.mkdir();
                    }


                    Date currentTime;
                    nombrePdf = "PaymentUsers" + (currentTime = Calendar.getInstance().getTime()) + ".pdf";
                    //create files in charity care folder
                    pFile = new File(payfile, nombrePdf);


                    // Así va correctamente la dirección
                    String dir = Environment.getExternalStorageDirectory() + "/Report/" + nombrePdf;
                    File arch = new File(dir);
                    Uri photoURI = FileProvider.getUriForFile(ComprasNegocio.this, BuildConfig.APPLICATION_ID + ".provider", arch);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(photoURI, "application/pdf");
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        try {
                            startActivity(intent);
                        }catch (ActivityNotFoundException e){
                            Toast.makeText(getApplicationContext(), "No existe una aplicación para abrir el PDF", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(arch), "application/pdf");
                        intent = Intent.createChooser(intent, "Open File");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try {
                            startActivity(intent);
                        }catch (ActivityNotFoundException e){
                            Toast.makeText(getApplicationContext(), "No existe una aplicación para abrir el PDF", Toast.LENGTH_SHORT).show();
                        }
                    }

                    DisplayReport();


                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);

                }


            }
        });


        faq_nueva_compra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ComprasNegocio.this, RegistrarCompra.class));
            }
        });

        ic_sumar_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectorDeFecha(1);
                cargarDatosSegunFecha(calendar);
            }
        });

        ic_restar_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectorDeFecha(-1);
                cargarDatosSegunFecha(calendar);
            }
        });


    }

    private void selectorDeFecha(int i) {
        calendar.add(Calendar.MONTH, i);
        fechaActual.setText(sdf.format(calendar.getTime()));
    }

    private void createPaymentReport(  List<Constants> paymentUsersList) throws DocumentException, FileNotFoundException{
        BaseColor colorWhite = WebColors.getRGBColor("#ffffff");
        BaseColor colorBlue = WebColors.getRGBColor("#056FAA");
        BaseColor grayColor = WebColors.getRGBColor("#425066");


        Font white = new Font(Font.FontFamily.HELVETICA, 15.0f, Font.BOLD, colorWhite);
        FileOutputStream output = new FileOutputStream(pFile);
        Document document = new Document(PageSize.A2);
        PdfPTable table = new PdfPTable(new float[]{6, 10,20, 20, 10,15,15,10,15,10});
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

        Chunk fechaRegistro = new Chunk("Fecha Registro", white);
        PdfPCell fecha = new PdfPCell(new Phrase(fechaRegistro));
        fecha.setFixedHeight(50);
        fecha.setHorizontalAlignment(Element.ALIGN_CENTER);
        fecha.setVerticalAlignment(Element.ALIGN_CENTER);

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

        Chunk pagadoEstado = new Chunk("Estado", white);
        PdfPCell estadoPagado = new PdfPCell(new Phrase(pagadoEstado));
        estadoPagado.setFixedHeight(50);
        estadoPagado.setHorizontalAlignment(Element.ALIGN_CENTER);
        estadoPagado.setVerticalAlignment(Element.ALIGN_CENTER);


        Chunk footerText = new Chunk("Creado con taski - Copyright @ 2021");
        PdfPCell footCell = new PdfPCell(new Phrase(footerText));
        footCell.setFixedHeight(70);
        footCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        footCell.setVerticalAlignment(Element.ALIGN_CENTER);
        footCell.setColspan(4);


        table.addCell(noCell);
        table.addCell(fecha);
        table.addCell(nameCell);
        table.addCell(phoneCell);
        table.addCell(amountCell);
        table.addCell(costoproduc);
        table.addCell(costoproducto);
        table.addCell(utilidad_producto);
        table.addCell(total_costo);
        table.addCell(estadoPagado);
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
            String pago = pay.getPagado();
            String fecha_registro = pay.getFechaRegistro();


            table.addCell(id + ". ");
            table.addCell(fecha_registro);
            table.addCell(name);
            table.addCell(sname);
            table.addCell(phone);
            table.addCell(cost);
            table.addCell(pventa);
            table.addCell(utilidades + " %");
            table.addCell(total_c);
            table.addCell(pago);

        }

        PdfPTable footTable = new PdfPTable(new float[]{6, 25, 20, 20});
        footTable.setTotalWidth(PageSize.A2.getWidth());
        footTable.setWidthPercentage(100);
        footTable.addCell(footCell);

        PdfWriter.getInstance(document, output);
        document.open();
        Font g = new Font(Font.FontFamily.HELVETICA, 25.0f, Font.NORMAL, grayColor);
        document.add(new Paragraph(" Reporte Financiero\n" + " Total Invertido : " + "$ " + String.valueOf(nformat.format(total)) + "\n\n", g));
        document.add(table);
        document.add(footTable);

        document.close();
    }



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

   private void cargarDatosSegunFecha(Calendar fecha){
       LinearLayoutManager mLayaoutManager = new LinearLayoutManager(getApplicationContext());
       mLayaoutManager.setReverseLayout(true);
       mLayaoutManager.setStackFromEnd(true);

       listaCompras.setLayoutManager(mLayaoutManager);
       FirebaseRecyclerOptions<ModeloComprasNegocio> options =
               new FirebaseRecyclerOptions.Builder<ModeloComprasNegocio>()
                       .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Compras").
                               child("lista").orderByChild("month").equalTo(sdf.format(fecha.getTime())), ModeloComprasNegocio.class)
                       .build();
       adaptadorListaCompras=new AdaptadorListaComprasNegocio(options);
       listaCompras.setAdapter(adaptadorListaCompras);
       adaptadorListaCompras.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        cargarDatosSegunFecha(calendar);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adaptadorListaCompras.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        total = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (payfile.isDirectory()) {
            String[] children = payfile.list();
            for (int i = 0; i < children.length; i++) {
                new File(payfile, children[i]).delete();
            }
        }
    }
}