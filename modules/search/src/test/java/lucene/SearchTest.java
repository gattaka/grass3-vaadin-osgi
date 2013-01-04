package lucene;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class SearchTest {

	@Test
	public void getMatchFieldTest() {

		String explanation = "0.024737064 = (MATCH) product of:"
				+ "0.049474128 = (MATCH) sum of:"
				+ "0.049474128 = (MATCH) weight(Obsah:jav in 5), product of:"
				+ "0.43079406 = queryWeight(Obsah:jav), product of:"
				+ "1.4700036 = idf(docFreq=4, maxDocs=8)"
				+ "0.29305646 = queryNorm"
				+ "0.11484403 = (MATCH) fieldWeight(Obsah:jav in 5), product of:"
				+ "1.0 = tf(termFreq(Obsah:jav)=1)"
				+ "1.4700036 = idf(docFreq=4, maxDocs=8)"
				+ "0.078125 = fieldNorm(field=Obsah, doc=5)"
				+ "0.5 = coord(1/2)";

		final String PREFIX = "\\(MATCH\\) weight\\(";
		final int PREFIX_STR_LENGTH = PREFIX.length() - 3;

		Pattern pattern = Pattern.compile(PREFIX + "[^:]+:");
		Matcher matcher = pattern.matcher(explanation);

		while (matcher.find()) {
			String output = matcher.group();
			System.out.println(output);
			System.out.println(output.substring(PREFIX_STR_LENGTH,
					output.length() - 1));
		}

	}

}
