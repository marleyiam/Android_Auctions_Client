package br.com.android.webservice;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import br.com.android.webservice.xmltest.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("unused")
public class LeilaoProdutos extends ListActivity {
	Button btnVoltar;
	Button btnCobrir;
	private int position;
   	//Vari�veis usadas para a requisi��o POST
	public int idProduto;
	public String email, lanceSugeridoProduto;

   
    @SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listplaceholder);
            
         
        //Recupera da intentAnterior e carrega os dados no adapter
        Intent intent = getIntent();
        ArrayList<HashMap<String, String>> listaProdutos = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("listaDeProdutos");

        
        //Usando o adaptador para associar os dados da listaProdutos a tela do ListView
        ListAdapter adapter = new SimpleAdapter(this,listaProdutos, R.layout.main, 
                        new String[] { "nome","descricao","maiorLance"}, 
                        new int[] { R.id.item_nome, R.id.item_descricao, R.id.item_maiorLance });
        

        setListAdapter(adapter);
        
  
        //Voltar pra Tela de Login
        btnVoltar = (Button) findViewById(R.id.btnVoltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
			    Intent voltaInicio = new Intent(LeilaoProdutos.this,LoginActivity.class); 
			    startActivity(voltaInicio);		
			}//fim do onClickView			
		});//fim OnClickListener
    }// fim do onCreate
        
               
    
    //Exibe mensagens
    public void exibirMensagem(String titulo, String texto) {
	AlertDialog.Builder mensagem = new AlertDialog.Builder(LeilaoProdutos.this);
	mensagem.setTitle(titulo);
	mensagem.setMessage(texto);
	mensagem.setNeutralButton("OK", null);
	mensagem.show();
    }
    
    
	/**Faz-se necess�rio recuperar o bot�o clickado, no caso o bjeto View v  
	*  e utiliz�-lo como chave em um mapa de Objetos que retornar� um valor int 
	*  que representar� a posi��o do bot�o na ListView.
	*  Assim ser� poss�vel associar a posi��o desse bot�o a respectiva posi��o do lance sugerido
	*  e do id dos produtos, valores esses recuperados na  lista do Adapter.
	*  Arrays guardaram os valores supracitados, os quais ser�o recuperados de acordo com seus �ndices.
	*  Ser� realizada uma itera��o e dentro desta ser� avaliado se o �ndice da itera��o for igual 
	*  ao valor inteiro "indice" asspcaodo ao bot�o pressionado,os valores lanceSugerido e id onde as posi��es
	*  em seus respectivos arrays forem iguais ao "indice", vari�veis receber�o os respectivos conte�dos.
	*  O id ser� passado a URI da requi��o POST ao servidor.
	*  No corpo da requisi��o ser�o enviados, o email do usu�rio(a ser passado e recuperado via bundle
	*  e o valor do lanceSugerido.
	* */
	@SuppressWarnings("unchecked")
	public void myClickHandler(View v)
    {   
    	   	
        HashMap<Object,Integer> hashBotoes = new HashMap<Object,Integer>();//Mapa de bot�es no ListaView
    	ListAdapter lA = getListAdapter(); //Recupera Inst�ncia do Adapatador
    	int count = lA.getCount(); //contador de objetos dentro do Adaptador
 
    	   
   	    //Recupera os parametros passados a intent, entre eles o "lanceSugerido" e o "id" 	
   	    Intent intent = getIntent();
   	    //Recupera email 
   	    email  = intent.getStringExtra("email");
   	   
   	    ArrayList<HashMap<String, String>> listaProdutos = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("listaDeProdutos");
   	    String [] sugerido = new String[listaProdutos.size()];//Array que armezena os valores dos lances sugeridos 
   	    String [] IDs = new String[listaProdutos.size()];////Array que armezena os IDs dos produtoss
   	 
    	
        //Popula o mapa de bot�es associando-os aos seus respectivos �ndices	
    	for(int i=0;i<count;i++){  	
    	Object ob =  getListView().getChildAt(i).findViewById(R.id.BtnCobrirLance);
    	hashBotoes.put(ob,i);  	
    	}//fim dor

    	//Popula o array que armazena os valores dos lances sugeridos  	 
    	for(int i=0;i<listaProdutos.size();i++){
    		 sugerido[i] = listaProdutos.get(i).get("lanceSugerido").toString().trim();

            }//fim do for
    	
    	//Popula o array que armazena os valores dos ids dos produtos	 
    	for(int i=0;i<listaProdutos.size();i++){
    		 IDs[i] = listaProdutos.get(i).get("id").toString().trim();

            }//fim do for
    	  	
    
    	int indice = hashBotoes.get(v); //retorna o valor int associado a chave object, no caso a posi��o do bot�o pressiondo
        
    	/**La�o que tem como int�ito definir o �ndice associativo dos arrays de lanceSugerido e Ids de produtos
    	*Quando o contador da itera��o for igual ao valor do �ndice do bot�o pressionado
    	*as vari�veis,lanceSugeridoProduto e idProduto recuperar�o os valores dos respectivos arrays.*/
    	for(int i=0;i<listaProdutos.size();i++){
   		   if(i==indice){
   			lanceSugeridoProduto = (sugerido[i]); 
   		    }
   		  if(i==indice){
   			idProduto = Integer.parseInt(IDs[i]);
   		    }

        }//fim do for
    	
    	/**Cobrir lance enviando requisi��o via HTTP POST, a vari�vel idProduto ser� passada na URI para referenciar p Id do produto cujo lance ser� coberto.
    	 * As vari�veis email, e lanceSugeridoProduto ser�o os dados enviados no corpo da requisi��o
    	 */
    	String uriPOST ="http://leilao-servidor.herokuapp.com/produtos/"+idProduto+"/lances";
    	ArrayList<NameValuePair> parametrosPost = new ArrayList<NameValuePair>();
    	parametrosPost.add(new BasicNameValuePair("lance.email",email));
    	parametrosPost.add(new BasicNameValuePair("lance.valor",lanceSugeridoProduto));
    	String respostaRetornada = null;
    	try{
    		respostaRetornada = ConexaoHttpClient.postHttpClient(uriPOST, parametrosPost);
    		String resposta =  respostaRetornada.toString();
    		resposta.replaceAll("\\s+", "");
    		if(resposta!=null)
    		exibirMensagem("Parab�ns", "O lance foi dado, agora fique de olho e boa sorte!");
    		else
    		//resposta.toString();
    		exibirMensagem("Notifica��o do Servidor : ",resposta.toString()+"!!!");
    	}catch(Exception e)
    	{
    		Toast.makeText(LeilaoProdutos.this, "Erro.:", Toast.LENGTH_LONG);
    		
    	}//fim catch
    
    	TextView tvAtualizado = null;
        //Atualiza o campo do valorAtual
    	for(int i=0;i<listaProdutos.size();i++){
    		  if(i==indice){
    		  tvAtualizado =  (TextView) getListView().getChildAt(i).findViewById(R.id.item_maiorLance);
    		  tvAtualizado.setText("LanceAtual.: "+lanceSugeridoProduto+".0");
    		  }
    		 
           }//fim do for
	
    }//fim do myClickHandler
  
  
}// fim da Classe 