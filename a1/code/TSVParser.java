import java.io.*;
import java.util.*;

public class TSVParser {
	private BufferedReader br;
	
    public TSVParser(String filename) throws IOException {
    	br = new BufferedReader(new FileReader(filename));
    }
    
    public String[] parseLine() throws IOException {
    	List<String> values = new ArrayList<>();
    	String line = br.readLine();
    	return (line == null) ? null : line.split("\\t");
    }
    
    public void close() throws IOException {
    	br.close();
    }
}
