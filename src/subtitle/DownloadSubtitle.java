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
import java.util.zip.ZipInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ho.yaml.Yaml;

import parameters.Parameters;
import util.GenericTreeNode;






public class DownloadSubtitle {

	private HttpClient httpclient;
	private SearchSubtitle searchSubtitle;

	public DownloadSubtitle() {
		httpclient = new DefaultHttpClient();
		searchSubtitle = new SearchSubtitle();
	}

	private void downloadSubtitle(GenericTreeNode<File, String> treeLink) throws ClientProtocolException, IOException {
		for (int i = 0; i < treeLink.getNumberOfChildren() ; i++) {
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
				File compressedFile = new File(child.getKey().getPath()+extension);
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
					BufferedOutputStream dest = null;
					FileInputStream fis = new FileInputStream(compressedFile);
					BufferedInputStream buffi = new BufferedInputStream(fis);
					ZipInputStream zis = new ZipInputStream(buffi);
					ZipEntry entry;
					while((entry = zis.getNextEntry()) != null) {
						System.out.println(entry.getName());
						FileOutputStream fos = new FileOutputStream(child.getKey().getAbsolutePath()+entry.getName());
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
	}

	public void run() {
		try {
			Parameters parameters = Yaml.loadType(new File("parameters.yml"),Parameters.class);
			GenericTreeNode<File, String> treeLink = searchSubtitle.searchSubtitle(parameters);
			downloadSubtitle(treeLink);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		DownloadSubtitle downloadSubtitle = new DownloadSubtitle();
		downloadSubtitle.run();			
	}

}
