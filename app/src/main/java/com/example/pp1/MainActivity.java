package com.example.pp1;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {
private EditText et_codigo, et_descripcion, et_precio;
private TextView total;
private Button scaner, reset, sumar;




    //Botón Scanner
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_codigo = (EditText)findViewById(R.id.codigo);
        et_descripcion = (EditText)findViewById(R.id.descripcion);
        et_precio = (EditText)findViewById(R.id.precio);
        total = (TextView)findViewById(R.id.total);
        scaner = (Button) findViewById(R.id.scaner);
        reset = (Button) findViewById(R.id.btnReset);

        scaner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Lector código de barras");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setTorchEnabled(false);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
            }
        });

    }

    // Método para registar producto
    public void Registrar (View view){
        Administrador admin = new Administrador(this, "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String codigo = et_codigo.getText().toString();
        String descripcion = et_descripcion.getText().toString();
        String precio = et_precio.getText().toString();

        if(!codigo.isEmpty() && !descripcion.isEmpty() && !precio.isEmpty()){
            ContentValues registro = new ContentValues();
            registro.put("codigo", codigo);
            registro.put("descripcion", descripcion);
            registro.put("precio", precio);

            BaseDeDatos.insert("articulos", null, registro);

            BaseDeDatos.close();
            et_codigo.setText("");
            et_descripcion.setText("");
            et_precio.setText("");

            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();

        } else{
            Toast.makeText(this, "Debes completar todos los campos", Toast.LENGTH_SHORT).show();

        }
            closeTecladoMovil();


    }

    // Método para ocultar teclado
    public void closeTecladoMovil() {

        View view = this.getCurrentFocus();
        if(view != null) {
            InputMethodManager imn = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imn.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }
    }

    // Método para consultar artículo
    public void Buscar(View view){
        Administrador admin = new Administrador(this, "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String codigo = et_codigo.getText().toString();

        if(!codigo.isEmpty()){

            Cursor fila = BaseDeDatos.rawQuery
                    ("select descripcion, precio from articulos where codigo =" + codigo, null);

            if(fila.moveToFirst()){
                et_descripcion.setText(fila.getString(0));
                et_precio.setText(fila.getString(1));
                BaseDeDatos.close();

                } else {
                Toast.makeText(this, "El artículo NO EXISTE", Toast.LENGTH_SHORT).show();
            }
fila.close();
        } else {
            Toast.makeText(this, "Debes introducir el código del articulo", Toast.LENGTH_SHORT).show();
            BaseDeDatos.close();
        }
        closeTecladoMovil();
    }

    // Método para eliminar artículo
    public void Elinimar(View view){

        Administrador admin = new Administrador(this, "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String codigo = et_codigo.getText().toString();

        if(!codigo.isEmpty()){

        int cantidad = BaseDeDatos.delete("articulos", "codigo=" + codigo, null);
        BaseDeDatos.close();

            et_codigo.setText("");
            et_descripcion.setText("");
            et_precio.setText("");
            if(cantidad == 1){
                Toast.makeText(this, "Artículo eliminado", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(this, "El artículo NO EXISTE", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Debes introducir el código del articulo", Toast.LENGTH_SHORT).show();
        }
        closeTecladoMovil();
    }

    // Método para modificar artículo
    public void Modificar(View view){
        Administrador admin = new Administrador(this, "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String codigo = et_codigo.getText().toString();
        String descripcion = et_descripcion.getText().toString();
        String precio = et_precio.getText().toString();

        if(!codigo.isEmpty() && !descripcion.isEmpty() && !precio.isEmpty()){

            ContentValues registro = new ContentValues();
            registro.put("codigo", codigo);
            registro.put("descripcion", descripcion);
            registro.put("precio", precio);

            int cantidad = BaseDeDatos.update("articulos", registro, "codigo=" + codigo, null);
            BaseDeDatos.close();

            if(cantidad == 1){
                Toast.makeText(this, "Artículo modificado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "El artículo NO EXISTE", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Debes completar todos los campos", Toast.LENGTH_SHORT).show();
        }
        closeTecladoMovil();

    }

    // Método para sumar
    public void Operacion (View view){

        if (et_precio.getText().toString().equals("")) {

           Toast.makeText(this, "El artículo no tiene precio", Toast.LENGTH_SHORT).show();

        } else {

            int n1;
            int n2;
            int suma;
            n1 = Integer.parseInt(et_precio.getText().toString());
            n2 = Integer.parseInt(total.getText().toString());
            suma = n1 + n2;
            String auxiliar = "" + suma;

            total.setText(auxiliar);

        }

        closeTecladoMovil();
    }

    // Método para Scanner
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null) {
            if(result.getContents() == null){
                Toast.makeText(this, "Lector cancelado", Toast.LENGTH_LONG).show();

            }else {
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                et_codigo.setText( result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);

        }
        closeTecladoMovil();
    }

    // Método para Reset
    public void Reset (View view){
        total = (TextView)findViewById(R.id.total);
        et_codigo = (EditText)findViewById(R.id.codigo);
        et_descripcion = (EditText)findViewById(R.id.descripcion);
        et_precio = (EditText)findViewById(R.id.precio);

        total.setText("0");
        et_codigo.setText("");
        et_descripcion.setText("");
        et_precio.setText("");

        closeTecladoMovil();
    }



}