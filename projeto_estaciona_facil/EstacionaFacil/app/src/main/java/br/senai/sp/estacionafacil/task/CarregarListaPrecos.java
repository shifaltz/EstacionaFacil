package br.senai.sp.estacionafacil.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.senai.sp.estacionafacil.Estacionados;
import br.senai.sp.estacionafacil.Ip;
import br.senai.sp.estacionafacil.ListaPreco;
import br.senai.sp.estacionafacil.model.Preco;


public class CarregarListaPrecos extends AsyncTask {

    private String dados="";
    private Context context;
    private ProgressDialog progressDialog;
    private List<Preco> precos;
    private ArrayAdapter<Preco> adapter;


    public CarregarListaPrecos(Context context){
        this.context = context;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("...");
        progressDialog.setMessage("Carregando...");
        progressDialog.show();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            URL url = new URL("http://" + Ip.getIp() + ":8080/preco");

            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
            InputStream dadosStream = conexao.getInputStream();
            InputStreamReader leitorStream = new InputStreamReader(dadosStream);
            BufferedReader bufferedReader = new BufferedReader(leitorStream);
            String registro = "";

            while (registro != null){
                registro = bufferedReader.readLine();
                dados = dados + registro;
            }

            Preco preco;
            precos = new ArrayList<>();

            JSONArray dadosArray = new JSONArray(dados);
            for(int i=0; i<dadosArray.length(); i++ ){

                JSONObject jo = (JSONObject) dadosArray.get(i);
                preco = new Preco();
                preco.setId(jo.getInt("id"));
                preco.setValorPrimeiraHora(jo.getDouble("valorPrimeiraHora"));
                preco.setValorDemaisHoras(jo.getDouble("valorDemaisHoras"));
                preco.setFlag(jo.getString("flag"));
               // preco.setTolerancia(jo.getInt("tolerancia"));
                precos.add(preco);

            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        adapter = new ArrayAdapter<Preco>(context, android.R.layout.simple_list_item_1, precos);
        ListaPreco.listaPrecos.setAdapter(adapter);

        progressDialog.dismiss();

    }
}
