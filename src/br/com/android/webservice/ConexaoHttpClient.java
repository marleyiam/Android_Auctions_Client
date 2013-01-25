package br.com.android.webservice;

//libs Java
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
//Libs http
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.HttpResponse; //Resposta do servidor
import org.apache.http.NameValuePair;
//Gerenciador de parâmetros de conexão
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;

public class ConexaoHttpClient {
	public static final int HTTP_TIMEOUT = 30 * 1000; // em milissegundos
	private static HttpClient httpClient;
    
	//Conexão via GET
	public static HttpClient getHttpClient(String url) {
		if (httpClient == null) {
			httpClient = new DefaultHttpClient();
			final HttpParams httpParams = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, HTTP_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, HTTP_TIMEOUT);
			ConnManagerParams.setTimeout(httpParams, HTTP_TIMEOUT);
		}
		return httpClient;
	}
	//Conexão via POST
	public static String postHttpClient(String url, ArrayList<NameValuePair> parametrosPost) throws Exception{
		BufferedReader bufferedReader = null;
		try{
			HttpClient client = getHttpClient(url);
			HttpPost httpPost = new HttpPost(url);
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parametrosPost);
			httpPost.setEntity(formEntity);
			HttpResponse httpResponse = client.execute(httpPost);
			bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
			StringBuffer stringBuffer = new StringBuffer("");
			String line = "";
			String LS = System.getProperty("Line.separator"); // \s
			while((line = bufferedReader.readLine()) != null){
				stringBuffer.append(line + LS);
			}//fim while
			String resultado = stringBuffer.toString();
			return resultado;
		}finally{
			if(bufferedReader!=null){
				try{
					bufferedReader.close();
				}catch (IOException e){
					e.printStackTrace();
				}//fim cacth
			}//fim if
			
		}//fim finally
		
	}//fim método post
}
