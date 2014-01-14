package subtitle;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.ho.yaml.Yaml;

import parameters.Parameters;
import util.GenericTreeNode;

import folder.ListenFolder;

public class SearchSubtitle {
	
	private BufferedReader br = null;
	private HttpGet get  = null;
	private HttpPost post = null;
	private HttpClient httpclient;
	
	public SearchSubtitle() {
		
		ClientConnectionManager connManager = new PoolingClientConnectionManager();
		httpclient = new DefaultHttpClient(connManager);
		httpclient.getParams().setParameter(
				ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY); 
		
	}
	
	public void setting(String langage) throws ClientProtocolException, IOException{
		post = new HttpPost("http://subscene.com/filter");
		
		List<NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("SelectedIds", langage ));
		nvps.add(new BasicNameValuePair("HearingImpaired", "0" ));
		
		post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
		
		httpclient.execute(post);
	}

	public GenericTreeNode<File,String> searchSubtitle(Parameters parameters) {
		GenericTreeNode<File,String> treeLink = null;
		
		try {
			setting(parameters.getLangage());

			ListenFolder listenFolder = new ListenFolder();
			File f_folder = new File(parameters.getFolder());
			treeLink = new GenericTreeNode<File,String>(f_folder," ");
			listenFolder.listeNameFile(treeLink);

			retrieveLink(treeLink);

		}catch (Exception e) {
			e.printStackTrace();
		}
		return treeLink;
	}
	
	private void retrieveLink(GenericTreeNode<File, String> treeLink) throws IllegalStateException, IOException {
		
		for (int i = 0; i < treeLink.getNumberOfChildren() ; i++) {
			GenericTreeNode<File, String> child = treeLink.getChildAt(i);
			
			if(child.getValue().equals(" ")){
				retrieveLink(child);
			}
			else{
				String url = setUrl(child.getValue());
				
				String response = getResponse(url);
				
				int begin = response.indexOf("<a href=\"/subtitles/")+9;						                  
				int end = response.indexOf("\">",begin);
				String subtitleLink = response.substring(begin,end);
				response = getResponse("http://subscene.com"+subtitleLink);

				begin = 0;
				end = 0;
				begin = response.indexOf("href=\"/subtitle/download")+6;						                  
				end = response.indexOf("\"",begin);
				String downloadSubtitleLink = response.substring(begin,end);
				child.setValue(downloadSubtitleLink);
				
			}
			
		}
	}

	private String getResponse(String url) throws IOException,
			ClientProtocolException {
		get = new HttpGet(url);

		HttpResponse response7 = httpclient.execute(get);

		br = new BufferedReader(new InputStreamReader(response7.getEntity().getContent()));
		String readLine = null;
		String response = "";
		while(((readLine = br.readLine()) != null))
		{
			response += readLine + "\n";
		}
		return response;
	}

	private static String setUrl(String filename) {
		String[] partsFilename = filename.split("\\.");
		String url = "http://subscene.com/subtitles/release?q=";
		
		for (int i = 0; i < partsFilename.length - 2 ; i++) {
			url += partsFilename[i] + "+";
		}
		url += partsFilename[ partsFilename.length - 1]; 
		return url;
	}
	
	public static void main(String[] args) {
		SearchSubtitle searchSubtitle = new SearchSubtitle();
		Parameters parameters;
		try {
			parameters = Yaml.loadType(new File("parameters.yml"),Parameters.class);
			System.out.println(searchSubtitle.searchSubtitle(parameters).toStringVerbose());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	

}
