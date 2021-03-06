package me.flux.fluxme.Business;


import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.flux.fluxme.Data.API_Access;
import me.flux.fluxme.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProgramacionAdminFragment extends Fragment {

    private ArrayList<CancionTendencia> tendencias = new ArrayList<CancionTendencia>();

    private  ArrayList<EditText> nombresCanciones = new ArrayList<EditText>();
    private  ArrayList<EditText> nombresArtistas = new ArrayList<EditText>();
    private  ArrayList<EditText> linksImgsCanciones = new ArrayList<EditText>();

    private RelativeLayout rlProgSemAdmin;
    private RelativeLayout rlTendAdmin;
    ListView lvTendAdmin;
    Button btnSaveTendencias;
    RadioButton rbProgramacion;
    RadioButton rbTendencias;
    RadioGroup rgrp_Opcion;



    public static String[] listDias = {"Domingo","Lunes","Martes","Miercoles","Jueves","Viernes","Sabado"};
    public static String[] listHoras = {"0:00-1:00","1:00-2:00","2:00-3:00", "3:00-4:00", "4:00-5:00",
                            "5:00-6:00","6:00-7:00","7:00-8:00", "8:00-9:00", "9:00-10:00",
                            "10:00-11:00","11:00-12:00","12:00-13:00", "13:00-14:00", "14:00-15:00",
                            "15:00-16:00","16:00-17:00","17:00-18:00", "18:00-19:00", "19:00-20:00",
                            "20:00-21:00","21:00-22:00","22:00-23:00", "23:00-23:00", "23:00-24:00"};

    String diaSelect = listDias[0];
    String horaSelect = listHoras [0].replaceAll(" ","");

    Streaming streaming;
    Usuario_Singleton user;
    Spinner spDia;
    Spinner spHora;
    EditText edtTitulo;
    ListView lvProgramacion;

    private RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            if(rbProgramacion.isChecked()){
                rlProgSemAdmin.setVisibility(View.VISIBLE);
                rlTendAdmin.setVisibility(View.INVISIBLE);
            }else if(rbTendencias.isChecked()){
                ExecuteAddTrending trend = new ExecuteAddTrending();
                trend.execute();

                rlTendAdmin.setVisibility(View.VISIBLE);
                rlProgSemAdmin.setVisibility(View.INVISIBLE);
            }
        }
    };

    public ProgramacionAdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_programacion_admin, container, false);
        rlProgSemAdmin = view.findViewById(R.id.rlProgSemAdmin);
        rlTendAdmin = view.findViewById(R.id.rlTendAdmin);

        streaming = Streaming.getInstance();
        user = Usuario_Singleton.getInstance();

        spDia = view.findViewById(R.id.sp_dia);
        spHora = view.findViewById(R.id.sp_horas);

        Button btnAceptar = view.findViewById(R.id.btn_aceptar);

        lvProgramacion = view.findViewById(R.id.lv_programacion);
        edtTitulo = view.findViewById(R.id.edt_titulo);

        ArrayAdapter adapterDia = new ArrayAdapter(getActivity().getApplicationContext(),android.R.layout.simple_spinner_item,listDias);
        ArrayAdapter adapterHora = new ArrayAdapter(getActivity().getApplicationContext(),android.R.layout.simple_spinner_item,listHoras);

        adapterDia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spDia.setAdapter(adapterDia);

        adapterHora.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spHora.setAdapter(adapterHora);

        spDia.setSelection(0, true);
        View vi = spDia.getSelectedView();
        ((TextView)vi).setTextColor(getResources().getColor(R.color.colorPrimary));

        spHora.setSelection(0, true);
        View vi2 = spHora.getSelectedView();
        ((TextView)vi2).setTextColor(getResources().getColor(R.color.colorPrimary));
        /*TextView spinnerText = (TextView) spDia.getChildAt(0);

        spinnerText.setTextColor(getResources().getColor(R.color.colorPrimary));*/
        //Set the listener for when each option is clicked.

        spDia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                //Change the selected item's text color
                ((TextView) view).setTextColor(getResources().getColor(R.color.colorPrimary));
                diaSelect = listDias[spDia.getSelectedItemPosition()];
                //Toast.makeText(getActivity(), diaSelect, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });

        spHora.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                //Change the selected item's text color
                ((TextView) view).setTextColor(getResources().getColor(R.color.colorPrimary));
                horaSelect = listHoras[spHora.getSelectedItemPosition()].replaceAll(" ","");
                //Toast.makeText(getActivity(), horaSelect, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });

        btnSaveTendencias = view.findViewById(R.id.btnSaveTendencias);
        btnSaveTendencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarClicked();
            }
        });

        rbProgramacion = view.findViewById(R.id.rbProgramacion);
        rbTendencias = view.findViewById(R.id.rbTendencias);
        rgrp_Opcion = view.findViewById(R.id.rgrp_Opcion);
        rgrp_Opcion.setOnCheckedChangeListener(checkedChangeListener);

        ExecuteGetProgramacion executeGetProgramacion = new ExecuteGetProgramacion();
        executeGetProgramacion.execute();

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(edtTitulo.getText()))
                {
                    ExecuteProgramacion executeProgramacion = new ExecuteProgramacion(user.getId(),user.getAuth_token(),streaming.getIdEmisora()
                            ,diaSelect, horaSelect, edtTitulo.getText().toString());
                    executeProgramacion.execute();
                }

                ExecuteGetProgramacion executeGetProgramacion2 = new ExecuteGetProgramacion();
                executeGetProgramacion2.execute();
            }
        });
        /*final ProgramacionAdapter arrayAdapter = new ProgramacionAdapter();
        lvProgramacion.setAdapter(arrayAdapter);*/
        lvProgramacion.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final int programacionEliminar = i;

                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Está seguro?")
                        .setMessage("Desea eliminar la programacion?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Programacion p = ProgramacionFragment.listaProgramacion.get(programacionEliminar);
                                ExecuteDeleteProgramacion executeDeleteProgramacion = new ExecuteDeleteProgramacion(p.getDia(),p.getHora().replace(" ",""));
                                executeDeleteProgramacion.execute();

                                ExecuteGetProgramacion executeGetProgramacion3 = new ExecuteGetProgramacion();
                                executeGetProgramacion3.execute();

                                //arrayAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();


                return true;
            }
        });


        initTextBoxes(view);



        return view;
    }

    public void guardarClicked(){
        String jsonObject = "{\"Lista_Canciones\":" + listaCancionesToJsonString() + "}";

        ExecuteAddTrending trend = new ExecuteAddTrending(jsonObject);
        trend.execute();
    }

    private String listaCancionesToJsonString(){
        String jsonArray = "[";
        for (int i = 0; i < 10; i++){
            try {
                jsonArray+= new JSONObject()
                        .put("posicion", i+1)
                        .put("cancion", nombresCanciones.get(i).getText())
                        .put("artista", nombresArtistas.get(i).getText())
                        .put("imagen", linksImgsCanciones.get(i).getText()).toString();
                if(i<9){
                    jsonArray+= ",";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        jsonArray+= "]";
        return jsonArray;
    }

   private void initTextBoxes(View view){
       nombresCanciones.add((EditText) view.findViewById(R.id.edtxtCancion1));
       nombresArtistas.add((EditText) view.findViewById(R.id.edtxtArtista1));
       linksImgsCanciones.add((EditText) view.findViewById(R.id.edtxtLinkImgCancion1));
       nombresCanciones.add((EditText) view.findViewById(R.id.edtxtCancion2));
       nombresArtistas.add((EditText) view.findViewById(R.id.edtxtArtista2));
       linksImgsCanciones.add((EditText) view.findViewById(R.id.edtxtLinkImgCancion2));
       nombresCanciones.add((EditText) view.findViewById(R.id.edtxtCancion3));
       nombresArtistas.add((EditText) view.findViewById(R.id.edtxtArtista3));
       linksImgsCanciones.add((EditText) view.findViewById(R.id.edtxtLinkImgCancion3));
       nombresCanciones.add((EditText) view.findViewById(R.id.edtxtCancion4));
       nombresArtistas.add((EditText) view.findViewById(R.id.edtxtArtista4));
       linksImgsCanciones.add((EditText) view.findViewById(R.id.edtxtLinkImgCancion4));
       nombresCanciones.add((EditText) view.findViewById(R.id.edtxtCancion5));
       nombresArtistas.add((EditText) view.findViewById(R.id.edtxtArtista5));
       linksImgsCanciones.add((EditText) view.findViewById(R.id.edtxtLinkImgCancion5));
       nombresCanciones.add((EditText) view.findViewById(R.id.edtxtCancion6));
       nombresArtistas.add((EditText) view.findViewById(R.id.edtxtArtista6));
       linksImgsCanciones.add((EditText) view.findViewById(R.id.edtxtLinkImgCancion6));
       nombresCanciones.add((EditText) view.findViewById(R.id.edtxtCancion7));
       nombresArtistas.add((EditText) view.findViewById(R.id.edtxtArtista7));
       linksImgsCanciones.add((EditText) view.findViewById(R.id.edtxtLinkImgCancion7));
       nombresCanciones.add((EditText) view.findViewById(R.id.edtxtCancion8));
       nombresArtistas.add((EditText) view.findViewById(R.id.edtxtArtista8));
       linksImgsCanciones.add((EditText) view.findViewById(R.id.edtxtLinkImgCancion8));
       nombresCanciones.add((EditText) view.findViewById(R.id.edtxtCancion9));
       nombresArtistas.add((EditText) view.findViewById(R.id.edtxtArtista9));
       linksImgsCanciones.add((EditText) view.findViewById(R.id.edtxtLinkImgCancion9));
       nombresCanciones.add((EditText) view.findViewById(R.id.edtxtCancion10));
       nombresArtistas.add((EditText) view.findViewById(R.id.edtxtArtista10));
       linksImgsCanciones.add((EditText) view.findViewById(R.id.edtxtLinkImgCancion10));
   }

   private void llenarTextBoxex(){
       for (int i = 0; i < 10; i++){
           CancionTendencia tend = getTendeciaByPosicion(i+1);
           nombresCanciones.get(i).setText(tend.getCancion());
           nombresArtistas.get(i).setText(tend.getArtista());
           linksImgsCanciones.get(i).setText(tend.getLink());
       }
   }

    private void ordenarProgramacion(){
        ArrayList<Programacion> progAux = new ArrayList<>();
        for(String d:listDias){
            for(String h:listHoras){
                for(Programacion p: ProgramacionFragment.listaProgramacion){
                    if(p.getDia().equals(d)&&p.getHora().equals(h))
                        progAux.add(p);
                }
            }
        }
        ProgramacionFragment.listaProgramacion.clear();
        ProgramacionFragment.listaProgramacion = progAux;
    }

   private CancionTendencia getTendeciaByPosicion(int pos){
       for(CancionTendencia t:tendencias){
           if(t.getPosicion() == pos){
               return t;
           }
       }
       return new CancionTendencia(pos, "", "", "");
   }

    private void cargarTendencias(JSONObject jsonResult){

        try {
            tendencias.clear();

            JSONArray jsonTendencias = jsonResult.getJSONArray("trending");
            for(int i = 0; i < jsonTendencias.length(); i++) {
                JSONObject tendencia = (JSONObject) jsonTendencias.get(i);
                tendencias.add(new CancionTendencia(tendencia.getInt("posicion"), tendencia.getString("cancion"), tendencia.getString("artista"), tendencia.getString("imagen")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        llenarTextBoxex();
    }
    private void cargarProgramaciones(JSONObject jsonResult){
        try {
            ProgramacionFragment.listaProgramacion.clear();

            JSONArray jsonProgramacion = jsonResult.getJSONArray("programacion");
            //for(int i = 0; i < jsonTendencias.length(); i++) {
            for(int i = 0; i < jsonProgramacion.length(); i++) {
                if(jsonProgramacion.length() > 0){
                    JSONObject prog = (JSONObject) jsonProgramacion.get(i);
                    ProgramacionFragment.listaProgramacion.add(new Programacion(prog.getString("dia"), prog.getString("hora"), prog.getString("titulo")));
                }else{
                    ProgramacionFragment.listaProgramacion.add(new Programacion("", "", ""));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ordenarProgramacion();
        lvProgramacion.setAdapter(new ProgramacionAdapter());
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteAddTrending extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        String tendencias = "";
        int tipo = 0;

        public ExecuteAddTrending() {
            this.tipo = 1;
        }

        public ExecuteAddTrending(String tendencias) {
            this.tendencias = tendencias;
            this.tipo = 0;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();
            Usuario_Singleton user = Usuario_Singleton.getInstance();

            if(tipo == 0){
                isOk = api.addTrending(user.getId(), user.getAuth_token(), Streaming.getIdEmisora(), tendencias);
            }else{
                isOk = api.getTrending(user.getId(), user.getAuth_token(), Streaming.getIdEmisora());
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(isOk){
                String token = null;
                try {
                    JSONObject result = API_Access.getInstance().getJsonObjectResponse();
                    token = (result).getString("authentication_token");
                    Usuario_Singleton.getInstance().setAuth_token(token);
                    LoginActivity.actualizarAuth_Token(token, getActivity().getApplicationContext());

                    if(tipo == 1){
                        cargarTendencias(result);
                        Toast.makeText(getActivity(), "Lista obtenida", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getActivity(), "Lista agregada", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public class ExecuteProgramacion extends AsyncTask<String,Void,String> {
        private String idEmisora;
        private String id_user;
        private String authToken;
        private String dia;
        private String hora;
        private String titulo;

        private boolean isDone = false;

        public ExecuteProgramacion(String id_user,String authToken,String idEmisora,String dia, String hora, String titulo){
            this.dia = dia;
            this.hora = hora;
            this.idEmisora=idEmisora;
            this.id_user=id_user;
            this.authToken=authToken;
            this.titulo = titulo;

        }

        @Override
        protected String doInBackground(String... strings) {


            API_Access api = API_Access.getInstance();
            isDone = api.setProgramacion(id_user,authToken,idEmisora,dia,hora,titulo);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(isDone) {
                String token = null;
                try {
                    token = (API_Access.getInstance().getJsonObjectResponse()).getString("authentication_token");
                    Usuario_Singleton.getInstance().setAuth_token(token);
                    LoginActivity.actualizarAuth_Token(token, getActivity().getApplicationContext());
                    //Si es Activity
                    //LoginActivity.actualizarAuth_Token(token, this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getActivity().getApplicationContext(), "Programacion agregada", Toast.LENGTH_SHORT).show();
            }


        }


    }
    public class ProgramacionAdapter extends BaseAdapter {

        public ProgramacionAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return ProgramacionFragment.listaProgramacion.size();
        }

        @Override
        public Object getItem(int i) {
            return ProgramacionFragment.listaProgramacion.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            if (view == null) {
                view = inflater.inflate(R.layout.custom_programacion_list_item, null);
            }


            TextView txtProgTitulo = view.findViewById(R.id.txtTituloProgramacion);
            TextView txtProgDiaHora = view.findViewById(R.id.txtDiaHora);



            Programacion prog = ProgramacionFragment.listaProgramacion.get(i);

            txtProgTitulo.setText(prog.getTitulo());
            String diaHora = prog.getDia() + ": " + prog.getHora();
            txtProgDiaHora.setText(diaHora);
            return view;
        }
    }

    public class ExecuteGetProgramacion extends AsyncTask<String, Void, String> {
        boolean isOk = false;

        public ExecuteGetProgramacion() {

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();
            Usuario_Singleton user = Usuario_Singleton.getInstance();

            isOk = api.getProgramacion(user.getId(), user.getAuth_token(), Streaming.getIdEmisora());

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(isOk){
                String token = null;
                try {
                    JSONObject result = API_Access.getInstance().getJsonObjectResponse();
                    token = (result).getString("authentication_token");
                    Usuario_Singleton.getInstance().setAuth_token(token);
                    LoginActivity.actualizarAuth_Token(token, getActivity().getApplicationContext());

                    cargarProgramaciones(result);

                    //Toast.makeText(getActivity(), "Lista obtenida", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    public class ExecuteDeleteProgramacion extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        String hora;
        String dia;

        public ExecuteDeleteProgramacion(String dia, String hora) {
            this.dia=dia;
            this.hora=hora;

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();
            //Usuario_Singleton user = Usuario_Singleton.getInstance();

            isOk = api.deleteProgramacion(user.getId(),user.getAuth_token(),Streaming.getIdEmisora(),dia,hora);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(isOk){

                    Toast.makeText(getActivity(), "Programacion borrada", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
