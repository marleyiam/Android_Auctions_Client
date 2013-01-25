package br.com.android.webservice;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

@SuppressWarnings("unused")

public class JSONfunctions {

	public static JSONObject getJSONfromURL(String url) {

		InputStream is = null;
		String result = "";
		JSONObject Resultado = null;

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {
			Log.e("log_tag", "Erro na conecção http  " + e.toString());
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"));
			String json = reader.readLine();

			JSONTokener tokener = new JSONTokener(json);
			// JSONArray finalResult = new JSONArray(tokener);
			Resultado = new JSONObject(tokener);
		} catch (Exception e) {
			Log.e("log_tag", "Erro ao encapsular o resultado no objeto JSON " + e.toString());
		}

		return Resultado;
	}
}