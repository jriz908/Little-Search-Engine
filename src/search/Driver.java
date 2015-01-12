	package search;

	
	import java.io.*;
	import java.util.*;

	public class Driver
	{

		static BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

		public static void main(String args[]) throws IOException
		{
			String docsFile = "docs.txt";
			String noiseWords = "noisewords.txt";

			LittleSearchEngine google = new LittleSearchEngine();	

			google.makeIndex(docsFile, noiseWords);

			String kw1 = "Deep";
			String kw2 = "World";
			
			int size = google.keywordsIndex.size();
			System.out.println(size);
			System.out.println(google.keywordsIndex.toString());

			google.top5search(kw1, kw2);



		}

	}

