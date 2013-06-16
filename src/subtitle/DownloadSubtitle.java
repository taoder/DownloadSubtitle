package subtitle;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.ho.yaml.Yaml;

import parameters.Parameters;
import util.GenericTreeNode;






public class DownloadSubtitle {

	private HttpClient httpclient;
	private SearchSubtitle searchSubtitle;

	public DownloadSubtitle() {
		ClientConnectionManager connManager = new PoolingClientConnectionManager();
		httpclient = new DefaultHttpClient(connManager);
		httpclient.getParams().setParameter(
				ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY); 
		searchSubtitle = new SearchSubtitle();
	}

	private void downloadSubtitle(GenericTreeNode<File, String> treeLink) {
		for (int i = 0; i < treeLink.getNumberOfChildren() ; i++) {
			BufferedOutputStream dest = null;
			File compressedFile = null;
			try {
				GenericTreeNode<File, String> child = treeLink.getChildAt(i);

				if(child.getValue().equals(" ")){
					downloadSubtitle(child);
				}
				else{
					HttpGet get = new HttpGet("http://subscene.com"+child.getValue());

					HttpResponse response7 = httpclient.execute(get);
					System.out.println(response7);

					String extension = "";
					if(response7.getEntity().getContentType().getValue().contains("zip")){
						extension=".zip";
					}
					else {
						extension=".rar";
					}

					InputStream is = response7.getEntity().getContent();
					compressedFile = new File(child.getKey().getPath()+extension);
					OutputStream os = new FileOutputStream(compressedFile);

					byte[] b = new byte[2048];
					int length;

					while ((length = is.read(b)) != -1) {
						os.write(b, 0, length);
					}

					is.close();
					os.close();

					if(extension.equals(".zip")){
						int BUFFER = 2048;
						int count;
						byte data[] = new byte[BUFFER];
						
						FileInputStream fis = new FileInputStream(compressedFile);
						BufferedInputStream buffi = new BufferedInputStream(fis);
						ZipInputStream zis = new ZipInputStream(buffi);
						ZipEntry entry;
						while((entry = zis.getNextEntry()) != null) {
							System.out.println(entry.getName());
							String filename = child.getKey().getPath();
							String srtname = filename.substring(0,filename.length()-4) + ".srt";
							System.out.println(srtname);
							FileOutputStream fos = new FileOutputStream(srtname);
							dest = new BufferedOutputStream(fos, BUFFER);
							while ((count = zis.read(data, 0, BUFFER)) != -1) {
								dest.write(data, 0, count);
							}dest.flush();
							dest.close();
						}

					}
					else{
						// TODO
					}
					compressedFile.delete();
				}
			}
			
			catch(ZipException e) {
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				if(compressedFile != null) 	compressedFile.delete();
			}

		}
	}

	public void run() {
		try {
			Parameters parameters = Yaml.loadType(new File("parameters.yml"),Parameters.class);
			GenericTreeNode<File, String> treeLink = searchSubtitle.searchSubtitle(parameters);
			downloadSubtitle(treeLink);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	}


	public static void main(String[] args) {
		DownloadSubtitle downloadSubtitle = new DownloadSubtitle();
		downloadSubtitle.run();			
	}

}
