package parameters;

import java.io.File;
import java.io.FileNotFoundException;

import org.ho.yaml.Yaml;

public class Parameters {
	String langage;
	String folder;
		
	public String getLangage() {
		return langage;
	}

	public void setLangage(String langage) {
		this.langage = langage;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}
}
