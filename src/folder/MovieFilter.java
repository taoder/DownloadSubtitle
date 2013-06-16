package folder;

import java.io.File;
import java.io.FilenameFilter;

public class MovieFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		return name.endsWith(".avi")
				||name.endsWith(".mkv")
				||name.endsWith(".wmv")
				||name.endsWith(".flv");	    
	}

}
