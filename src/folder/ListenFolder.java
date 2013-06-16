package folder;

import java.io.File;
import java.util.ArrayList;

import util.GenericTreeNode;

public class ListenFolder {

	public void listeNameFile(GenericTreeNode<File, String> treeLink) {
		File[] tabFile = treeLink.getKey().listFiles();
		File[] tabSrtFile = treeLink.getKey().listFiles(new SrtFilter());
		ArrayList<String> listSrtName = new ArrayList<String>();
		for (File srtFile : tabSrtFile) {
			listSrtName.add(srtFile.getName().substring(0,srtFile.getName().length()-4));
		}
		
		
		for (File file : tabFile) {
			if(file.isDirectory()){
				GenericTreeNode<File, String> child = new GenericTreeNode<File, String>(file, " ");
				treeLink.addChild(child);
				listeNameFile(child);
			}
			else if(file.getName().endsWith(".avi")
					||file.getName().endsWith(".mkv")
					||file.getName().endsWith(".wmv")
					||file.getName().endsWith(".flv")){
				if(!listSrtName.contains(file.getName().substring(0,file.getName().length()-4))){
					treeLink.addChild(new GenericTreeNode<File, String>(file,file.getName()));
				}
			}
		}
		
	}
}
