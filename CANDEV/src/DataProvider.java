import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;

public class DataProvider {
	private static enum Trend{UP, DOWN, STEADY};

	public static void main(String[] args) {
		Trend trend = Trend.STEADY;
		String subject = "";
		String verb = "";
		
		Lexicon lexicon = Lexicon.getDefaultLexicon();
		NLGFactory nlgFactory = new NLGFactory(lexicon);
		Realiser realiser = new Realiser(lexicon);
		
		// trend verbs
		ArrayList<String> upVerb = new ArrayList<String>(Arrays.asList("surge", "soar", "leap", "climb", "rocket", "rise", "increase", "gain"));
		ArrayList<String> downVerb = new ArrayList<String>(Arrays.asList("drop", "plummet", "dip", "sink", "decline", "decrease", "fall"));
		ArrayList<String> neutralVerb = new ArrayList<String>(Arrays.asList("shift", "change"));
		ArrayList<String> steadyVerb = new ArrayList<String>(Arrays.asList("consistent", "steady", "constant"));
		
		// trend adjectives
		ArrayList<String> smallAdj = new ArrayList<String>(Arrays.asList("slight", "gradual", "marginal", "modest"));
		ArrayList<String> largeAdj = new ArrayList<String>(Arrays.asList("large", "YUUUGE"));
		ArrayList<String> degree = new ArrayList<String>(Arrays.asList("significant", "dramatic", "sudden", "substantial", "sharp"));
		
		String file = "C:\\Users\\Main\\Desktop\\CANDEV\\CANDEV\\final_data_set.csv";
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		ArrayList<String> columns = new ArrayList<String>();

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {

			Collections.addAll(columns, br.readLine().split(","));
			String line = "";

			while ((line = br.readLine()) != null) {
				ArrayList<String> splitRow = new ArrayList<String>();
				Collections.addAll(splitRow, line.split(","));
				data.add(splitRow);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/* Create a sentence */
		// placeholder for sentence
		SPhraseSpec sentence = nlgFactory.createClause();
		
		for(ArrayList<String> row : data) {
			Random rand = new Random();
			sentence = nlgFactory.createClause();
			subject = row.get(0) + " sales";
			double before = Double.parseDouble(row.get(1));
			double after = Double.parseDouble(row.get(2));
			
			trend = getTrend(before, after);
			
			if(trend == Trend.UP) {
				sentence.setVerb(upVerb.get(rand.nextInt(upVerb.size())));
			} else if (trend == Trend.DOWN) {
				sentence.setVerb(downVerb.get(rand.nextInt(downVerb.size())));
			} else {
				sentence.setVerb("is");
				sentence.addComplement(steadyVerb.get(rand.nextInt(steadyVerb.size())));
			}
		    sentence.setFeature(Feature.TENSE, Tense.PAST);
			String timePeriod = "from " + columns.get(1) + " to " + columns.get(2);
			
			sentence.setSubject(subject);
			sentence.addComplement(timePeriod);
			
			System.out.println(realiser.realiseSentence(sentence));
		}	
	}
	
	private static Trend getTrend(double before, double after) {
		double change = (after - before) / ((before + after) / 2);
		if(change > 0.01) {
			return Trend.UP;
		} else if (change < -0.01) {
			return Trend.DOWN;
		} else {
			return Trend.STEADY;
		}
	}

}
