package com.guma.desarrollo.salesumk.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;

import android.support.v7.app.AlertDialog;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.guma.desarrollo.salesumk.Activity.DetallesActivity;
import com.guma.desarrollo.salesumk.Adapters.SpecialAdapter;
import com.guma.desarrollo.salesumk.Lib.ClssURL;
import com.guma.desarrollo.salesumk.DataBase.DataBaseHelper;

import com.guma.desarrollo.salesumk.Activity.MainActivity;
import com.guma.desarrollo.salesumk.Lib.Variables;
import com.guma.desarrollo.salesumk.R;


import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InboxFragment extends Fragment
{

    DataBaseHelper myDB;
    Variables myVa;

    ListView lv;

    SearchView sv;

    ArrayAdapter<String> adapter;
    SpecialAdapter adapter2;
    TextView txt;
    TextView id;
    List<HashMap<String, String>> fillMaps;
    ProgressDialog pdialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_inv, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("INVENTARIO UNIMARK");
        myDB = new DataBaseHelper(getActivity());
        //lv = (ListView) view.findViewById(R.id.ListView1);
        sv = (SearchView) view.findViewById(R.id.searchView1);
        txt =(TextView) view.findViewById(R.id.tvLastUpdate);
        id =(TextView) view.findViewById(R.id.item1);

        lv = (ListView) view.findViewById(R.id.listview);

        sv.setOnQueryTextListener
        (new SearchView.OnQueryTextListener()
            {
                @Override
                public boolean onQueryTextSubmit(String text) { return false; }
                @Override
                public boolean onQueryTextChange(String text)
                {
                    adapter2.getFilter().filter(text);
                    return false;
                }
            }
        );
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener
        (new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    CallInve();
                    loadData();
                }
            }
        );

        loadData();

        lv.setOnItemClickListener
        (new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    String selectedFromList = String.valueOf(lv.getItemAtPosition(position));
                    String Cod = ClearString(selectedFromList);
                    myVa.setInv_Articulo(Cod);

                    if (Cod.equals("0"))
                    {
                        Toast.makeText(getActivity(), "Tiene que Sincronizar el Dispositivo", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent mint = new Intent(getActivity(),DetallesActivity.class);
                        getActivity().startActivity(mint);
                    }
                    //Toast.makeText(getActivity(), Cod, Toast.LENGTH_SHORT).show();
                }
            }
        );

        return view;
    }
    private String ClearString(String cadena){
        int c1 = cadena.indexOf("[")+1;
        int c2 = cadena.indexOf("]");
        cadena = cadena.substring(c1,c2);
        return cadena;
    }



    private void loadData(){
        ArrayList<String> datos = new ArrayList<String>();
        String Date="";

        String[] from = new String[] {"rowid", "art", "Cantidad"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3};
        fillMaps = new ArrayList<HashMap<String, String>>();



        Cursor res =  myDB.GetData("Articulo");
        if (res.getCount()!=0){
            if (res.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<String, String>();
                    datos.add(res.getString(1)+"\n["+res.getString(0)+"]");
                    map.put("rowid", "["+res.getString(0)+"]");
                    map.put("art", res.getString(1));
                    map.put("Cantidad",res.getString(3));
                    fillMaps.add(map);
                    Date = res.getString(4);
                } while(res.moveToNext());
            }
        }else {
            HashMap<String, String> map = new HashMap<String, String>();
            datos.add("");
            map.put("rowid", "[0]");
            map.put("art", "SIN DATOS");
            map.put("Cantidad","");
            fillMaps.add(map);
            Date = "00/00/0000";

        }



        txt.setText("Ultima Actualización: " + Date.toString());

        res.close();



        // fill in the grid_item layout
        adapter2 = new SpecialAdapter(getActivity(), fillMaps, R.layout.grid_item, from, to);
        lv.setAdapter(adapter2);
    }

    public void Error404(String TipoError){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(TipoError)
                .setNegativeButton("OK",null)
                .create()
                .show();
    }
    public ArrayList<String> obtDatosUS(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto;

            for (int i=0; i<jsonArray.length(); i++){
                texto = jsonArray.getJSONObject(i).getString("ARTICULO")+ "," +
                        jsonArray.getJSONObject(i).getString("DESCRIPCION")+ "," +
                        jsonArray.getJSONObject(i).getString("TOTAL_EXISTENCIA")+ "," +
                        jsonArray.getJSONObject(i).getString("PRECIO")+ "," +
                        jsonArray.getJSONObject(i).getString("PUNTOS")+ "," +
                        jsonArray.getJSONObject(i).getString("BODEGAS")+ "," +
                        jsonArray.getJSONObject(i).getString("REGLAS");
                listado.add(texto);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return listado;

    }
    public ArrayList<String> obtDatosExistenciaLote(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto;

            for (int i=0; i<jsonArray.length(); i++){
                texto = jsonArray.getJSONObject(i).getString("ARTICULO")+ "," +
                        jsonArray.getJSONObject(i).getString("LOTE")+ "," +
                        jsonArray.getJSONObject(i).getString("BODEGAS")+ "," +
                        jsonArray.getJSONObject(i).getString("CANT_DISPONIBLE")+ "," +
                        jsonArray.getJSONObject(i).getString("BODEGA");
                listado.add(texto);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return listado;

    }
    public ArrayList<String> obtDatosLiq12(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto;

            for (int i=0; i<jsonArray.length(); i++){
                texto = jsonArray.getJSONObject(i).getString("ARTICULO")+ "," +
                        jsonArray.getJSONObject(i).getString("DESCRIPCION")+ "," +
                        jsonArray.getJSONObject(i).getString("DIAS_VENCIMIENTO")+ "," +
                        jsonArray.getJSONObject(i).getString("CANT_DISPONIBLE")+ "," +
                        jsonArray.getJSONObject(i).getString("fecha_vencimientoR")+ "," +
                        jsonArray.getJSONObject(i).getString("LOTE");
                listado.add(texto);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return listado;

    }
    public void CallInve(){
        AsyncHttpClient Cnx = new AsyncHttpClient();
        RequestParams paramentros = new RequestParams();
        pdialog = ProgressDialog.show(getActivity(), "","Procesando. Porfavor Espere...", true);

        Cnx.post(ClssURL.getUrlInve(), paramentros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {

                boolean Inserted=false;
                if (statusCode==200){

                    ArrayList<String> MeEncontro = obtDatosUS(new String(responseBody));
                    SQLiteDatabase db = myDB.getWritableDatabase();
                    db.execSQL("DELETE FROM Articulo");


                    for (int n=0; n<MeEncontro.size();n++){
                        String[] items = MeEncontro.get(n).toString().split(",");
                        Inserted = myDB.insertDataArticulo(
                                items[0].toString(),
                                items[1].toString(),
                                items[2].toString(),
                                items[3].toString(),
                                items[4].toString(),
                                items[5].toString(),
                                items[6].toString()
                                );
                    }

                    if (Inserted == true){


                    }else{
                        adapter2.notifyDataSetChanged();
                        pdialog.dismiss();
                        Error404("Error de Actualizacion de datos");
                    }

                }else{
                    adapter2.notifyDataSetChanged();
                    pdialog.dismiss();
                    Error404("Sin Cobertura de datos.");
                }


            }
            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                adapter2.notifyDataSetChanged();
                pdialog.dismiss();
                Error404("Sin Cobertura de datos.");
            }
        });
        Cnx.post(ClssURL.getURL_liq12(), paramentros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {

                boolean Inserted=false;
                if (statusCode==200){

                    ArrayList<String> MeEncontro = obtDatosLiq12(new String(responseBody));
                    SQLiteDatabase db = myDB.getWritableDatabase();
                    db.execSQL("DELETE FROM LIQ12");


                    for (int n=0; n<MeEncontro.size();n++){
                        String[] items = MeEncontro.get(n).toString().split(",");
                        Inserted = myDB.insertDataLIQ12(
                                items[0].toString(),
                                items[1].toString(),
                                items[2].toString(),
                                items[3].toString(),
                                items[4].toString(),
                                items[5].toString()
                        );
                    }

                    if (Inserted == true){
                    }else{
                        adapter2.notifyDataSetChanged();
                        pdialog.dismiss();
                        Error404("Error de Actualizacion de datos");
                    }

                }else{
                    adapter2.notifyDataSetChanged();
                    pdialog.dismiss();
                    Error404("Sin Cobertura de datos.");
                }


            }
            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                Error404("Sin Cobertura de datos.");
            }
        });
        Cnx.post(ClssURL.getURL_ExistenciaLotes(), paramentros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                boolean Inserted=false;
                if (statusCode==200){

                    ArrayList<String> MeEncontro = obtDatosExistenciaLote(new String(responseBody));
                    SQLiteDatabase db = myDB.getWritableDatabase();
                    db.execSQL("DELETE FROM EXISTENCIA_LOTE");
                    for (int n=0; n<MeEncontro.size();n++){
                        String[] items = MeEncontro.get(n).split(",");
                        Inserted = myDB.insertExiLote(items[0],items[1],items[2],items[3],items[4]);
                    }

                    if (Inserted){
                        fillMaps.clear();
                        adapter2.notifyDataSetChanged();
                        pdialog.dismiss();
                        loadData();
                        Toast.makeText(getActivity(), "Actualización Completada",Toast.LENGTH_SHORT).show();

                    }else{
                        adapter2.notifyDataSetChanged();
                        pdialog.dismiss();
                        Error404("Error de Actualizacion de datos");
                    }

                }else{
                    adapter2.notifyDataSetChanged();
                    pdialog.dismiss();
                    Error404("Sin Cobertura de datos.");
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                adapter2.notifyDataSetChanged();
                pdialog.dismiss();
            }
        });
    }
}
