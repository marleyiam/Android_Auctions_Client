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
   	//Variáveis usadas para a requisição POST
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
    
    
	/**Faz-se necessário recuperar o botão clickado, no caso o bjeto View v  
	*  e utilizá-lo como chave em um mapa de Objetos que retornará um valor int 
	*  que representará a posição do botão na ListView.
	*  Assim será possível associar a posição desse botão a respectiva posição do lance sugerido
	*  e do id dos produtos, valores esses recuperados na  lista do Adapter.
	*  Arrays guardaram os valores supracitados, os quais serão recuperados de acordo com seus índices.
	*  Será realizada uma iteração e dentro desta será avaliado se o índice da iteração for igual 
	*  ao valor inteiro "indice" asspcaodo ao botão pressionado,os valores lanceSugerido e id onde as posições
	*  em seus respectivos arrays forem iguais ao "indice", variáveis receberão os respectivos conteúdos.
	*  O id será passado a URI da requição POST ao servidor.
	*  No corpo da requisição serão enviados, o email do usuário(a ser passado e recuperado via bundle
	*  e o valor do lanceSugerido.
	* */
	@SuppressWarnings("unchecked")
	public void myClickHandler(View v)
    {   
    	   	
        HashMap<Object,Integer> hashBotoes = new HashMap<Object,Integer>();//Mapa de botões no ListaView
    	ListAdapter lA = getListAdapter(); //Recupera Instância do Adapatador
    	int count = lA.getCount(); //contador de objetos dentro do Adaptador
 
    	   
   	    //Recupera os parametros passados a intent, entre eles o "lanceSugerido" e o "id" 	
   	    Intent intent = getIntent();
   	    //Recupera email 
   	    email  = intent.getStringExtra("email");
   	   
   	    ArrayList<HashMap<String, String>> listaProdutos = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("listaDeProdutos");
   	    String [] sugerido = new String[listaProdutos.size()];//Array que armezena os valores dos lances sugeridos 
   	    String [] IDs = new String[listaProdutos.size()];////Array que armezena os IDs dos produtoss
   	 
    	
        //Popula o mapa de botões associando-os aos seus respectivos índices	
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
    	  	
    
    	int indice = hashBotoes.get(v); //retorna o valor int associado a chave object, no caso a posição do botão pressiondo
        
    	/**Laço que tem como intúito definir o índice associativo dos arrays de lanceSugerido e Ids de produtos
    	*Quando o contador da iteração for igual ao valor do índice do botão pressionado
    	*as variáveis,lanceSugeridoProduto e idProduto recuperarão os valores dos respectivos arrays.*/
    	for(int i=0;i<listaProdutos.size();i++){
   		   if(i==indice){
   			lanceSugeridoProduto = (sugerido[i]); 
   		    }
   		  if(i==indice){
   			idProduto = Integer.parseInt(IDs[i]);
   		    }

        }//fim do for
    	
    	/**Cobrir lance enviando requisição via HTTP POST, a variável idProduto será passada na URI para referenciar p Id do produto cujo lance será coberto.
    	 * As variáveis email, e lanceSugeridoProduto serão os dados enviados no corpo da requisição
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
    		exibirMensagem("Parabéns", "O lance foi dado, agora fique de olho e boa sorte!");
    		else
    		//resposta.toString();
    		exibirMensagem("Notificação do Servidor : ",resposta.toString()+"!!!");
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