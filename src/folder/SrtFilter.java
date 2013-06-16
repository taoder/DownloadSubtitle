package folder;

import java.io.File;
import java.io.FilenameFilter;

public class SrtFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		return name.endsWith(".srt");	    
	}

}
