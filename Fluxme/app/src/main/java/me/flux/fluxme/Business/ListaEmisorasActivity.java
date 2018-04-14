package me.flux.fluxme.Business;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.flux.fluxme.Data.API_Access;
import me.flux.fluxme.R;

public class ListaEmisorasActivity extends BaseActivity {

    private ArrayList<Emisora> emisoras = new ArrayList<Emisora>();
    ListView lvEmisoras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_emisoras);

        ExecuteGetEmisoras getEmisoras = new ExecuteGetEmisoras();
        getEmisoras.execute();

        initToolbar();

        lvEmisoras = findViewById(R.id.listaEmisoras);
        //lvEmisoras.setAdapter(new EmisorasAdapter());
        lvEmisoras.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setEmisoraToStreaming(position);
            }
        });
    }

    private void setEmisoraToStreaming(int position) {
        Emisora seleccionada = emisoras.get(position);
        Streaming.pause();
        main_menu.getItem(1).setIcon(R.drawable.play_button_fluxme);
        Streaming.setIdEmisora(Integer.toString(seleccionada.getId()));
        Streaming.setEmisora_name(seleccionada.getNombre());
        Streaming.setStream(seleccionada.getLink());

        boolean admin = false;
        if(seleccionada.getId_admin() == Integer.parseInt(Usuario_Singleton.getInstance().getId())){
            admin = true;
        }
        Usuario_Singleton.getInstance().setAdmin(admin);

        main_menu.getItem(0).setTitle(Streaming.getEmisora_name());
        main_menu.getItem(0).setEnabled(true);
        if (Streaming.isPrepared()) {
            Streaming.play();
            if (Streaming.isIsPlaying()) {
                main_menu.getItem(1).setIcon(R.drawable.stop_button_fluxme);
            }
        }
    }

    private void cargarEmisoras(JSONObject jsonResult){

        try {
            Usuario_Singleton.getInstance().setAuth_token(jsonResult.getString("authentication_token"));
            JSONArray jsonEmisoras = jsonResult.getJSONArray("emisoras");
            for(int i = 0; i < jsonEmisoras.length(); i++) {
                JSONObject emisora = (JSONObject) jsonEmisoras.get(i);
                emisoras.add(new Emisora(emisora.getInt("id"), emisora.getString("nombre"), emisora.getString("link"), emisora.getInt("id_admin")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        lvEmisoras.setAdapter(new EmisorasAdapter());
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class EmisorasAdapter extends BaseAdapter {

        public EmisorasAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return emisoras.size();
        }

        @Override
        public Object getItem(int i) {
            return emisoras.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            if (view == null) {
                view = inflater.inflate(R.layout.custom_list_item, null);
            }

            ImageView imgEmisora = view.findViewById(R.id.iV_ListItem);
            TextView txtTitulo = view.findViewById(R.id.textItemTitle);

            /*HttpGetBitmap request = new HttpGetBitmap();
            Bitmap coverImage = null;
            try {
                coverImage = request.execute(i).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            imgMovie.setImageBitmap(coverImage);
            */
            txtTitulo.setText(emisoras.get(i).getNombre());
            return view;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteGetEmisoras extends AsyncTask<String, Void, String> {
        boolean isOk = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /*rlLoader.setVisibility(View.VISIBLE);
            rlLogin.setVisibility(View.INVISIBLE);*/
        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();
            Usuario_Singleton user = Usuario_Singleton.getInstance();
            isOk = api.getEmisoras(user.getId(), user.getAuth_token());

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                cargarEmisoras(API_Access.getInstance().getJsonObjectResponse());
            }else{
                String mensaje = "Error getEmisoras()";

                Toast.makeText(ListaEmisorasActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                //rlLoader.setVisibility(View.INVISIBLE);
                //rlLogin.setVisibility(View.VISIBLE);
            }
        }
    }
}