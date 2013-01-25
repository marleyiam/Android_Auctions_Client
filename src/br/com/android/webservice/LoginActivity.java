package br.com.android.webservice;

//Libs Java
import java.util.ArrayList;
import java.util.HashMap;

//Libs JSON
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//Libs Android
import br.com.android.webservice.xmltest.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class LoginActivity extends Activity {
	EditText etEmail;
	Button btnlogar;
	static ProgressDialog progressDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginlayout);
        	
		etEmail = (EditText) findViewById(R.id.et_email);
		btnlogar = (Button) findViewById(R.id.login_button);

		btnlogar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				
				  ArrayList<HashMap<String, String>> listaProdutos = new ArrayList<HashMap<String, String>>();
			      
			       
			        JSONObject json = JSONfunctions.getJSONfromURL("http://leilao-servidor.herokuapp.com/produtos.json?email="+etEmail.getText().toString());
			                
			        try{
			        	 progressDialog = ProgressDialog.show(LoginActivity.this, "", "Aguarde, sincronizando !...");
			    	        	
			        	JSONArray  produtos = json.getJSONArray("produtos");
			        	
				        for(int i=0;i<produtos.length();i++){						
							HashMap<String, String> map = new HashMap<String, String>();	
							JSONObject e = produtos.getJSONObject(i);
							
							map.put("indice",  String.valueOf(i));
							map.put("nome", "Produto.:" + e.getString("nome"));
							map.put("descricao", "Descrição.:" + e.getString("descricao"));
				        	map.put("id", "" + e.getInt("id"));
				        	map.put("lanceSugerido", "" + e.getInt("lanceSugerido"));
				        	map.put("maiorLance", "LanceAtual.: " +  e.getJSONObject("maiorLance").getString("valor"));
				       				        					        	
				        	listaProdutos.add(map);						        	
						}// fim do for		
			        }catch(JSONException e){
			        	 Log.e("log_tag", "Error parsing data "+e.toString());
			        }//fim catch
			      			        
			        //Intent para lista de produtos + passagem do mapa de parâmetros a serem exibidos na proxima
			        // tela usando o adaptador			        
			        Intent itentListaDeProdutos = new Intent(LoginActivity.this,LeilaoProdutos.class);
			        //Passa também o email do usuário 
			        itentListaDeProdutos.putExtra("email", etEmail.getText().toString());
			        itentListaDeProdutos.putExtra("listaDeProdutos", listaProdutos);
			        startActivity(itentListaDeProdutos);
			                	
			}//fim do onClickView			
		});//fim OnClickListener

	}// Fim do onCreate	
	
}//fim da classe