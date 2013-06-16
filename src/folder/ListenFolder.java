package folder;

import java.io.File;

import util.GenericTreeNode;

public class ListenFolder {

	public void listeNameFile(GenericTreeNode<File, String> treeLink) {
		File[] tabFile = treeLink.getKey().listFiles();
		
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
				
				treeLink.addChild(new GenericTreeNode<File, String>(file,file.getName()));
			}
		}
		
	}
}
