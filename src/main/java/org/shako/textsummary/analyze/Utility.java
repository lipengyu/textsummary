package org.shako.textsummary.analyze;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.shako.textsummary.data.Token;

abstract class Utility {
	
	private static Logger log = Logger.getLogger(Utility.class);
	
	public static Cluster[] cluster(int level, WrapToken[] freqTokens) {
		log.debug("cluster begin in " + new Date().getTime());
		List<Cluster> clus = new ArrayList<Cluster>();
		List<WrapToken> preList = Arrays.asList(freqTokens);		
		do{
			Cluster c = new Cluster();
			WrapToken t = preList.get(0);
			List<WrapToken> result = c.process(t, level);
			preList.removeAll(result);
			clus.add(c);
		}while(!preList.isEmpty());		
		log.debug("cluster end in " + new Date().getTime());
		return clus.toArray(new Cluster[0]);
	}
	
	public static int calcRelLevel(int[] relates, int n) {
		//冒泡排序找出最高的n条边，最小的即为阀值，返回出去
		
		for(int j = 0; j < n; j ++) {
			int i = relates.length - 1;
			int k;
			for(; i > 0; i--) {
				k = i - 1;
				if(relates[i] > relates[k]) {
					int temp = relates[i];
					relates[i] = relates[k];
					relates[k] = temp;				
				}
			}
		}
		log.debug("the level relate coefficient is :" + relates[n-1]);
		return relates[n-1];
	}
	
	public static int calcRelate(WrapToken first, WrapToken second) {
		Token fdata = first.getData();
		Token sdata = second.getData();
		Set<Integer> fsens = fdata.getSentences();
		int frel = 0;
		int srel = 0;
		for(Integer index : fsens) {
			if(sdata.isInSentence(index)) {
				frel += fdata.getNumInSentence(index);
				srel += sdata.getNumInSentence(index);
			}
		}
		int relate = (frel / fdata.getFrequency()) + (srel / sdata.getFrequency());
		first.setCoefficient(second, relate);
		second.setCoefficient(first, relate);
		return relate;
	}

	public static WrapToken[] wrapTokens(Collection<Token> tokens, int size) {
		log.debug("wrap the tokens from the article in the operation.");
		WrapToken[] wraped = new WrapToken[size];
		Iterator<Token> iter = tokens.iterator();
		int i = 0;
		while(iter.hasNext()) {
			wraped[i] = new WrapToken(iter.next());
			i++;
		}
		log.debug("wrapping the tokens is done");
		return wraped;
	}
	
	public static WrapToken[] minusTokens(Collection<WrapToken> parent, Collection<WrapToken> child) {
		List<WrapToken> ret = new ArrayList<WrapToken>();
		for(WrapToken token : parent) {
			if(!child.contains(token)){
				ret.add(token);
			}
		}
		log.debug("parent has " + parent.size() + " tokens.");
		log.debug("child has " + child.size() + " tokens.");
		log.debug("return " + ret.size() + " tokens.");
		return ret.toArray(new WrapToken[0]);
	}
	
	public static int calcContribution(Cluster cluster, List<WrapToken> tokens){
		int score = 0;
		for(WrapToken c : cluster.getTokens()) {
			for(WrapToken t : tokens){
				if(!cluster.contains(t))
					score += c.getCoefficient(t);
			}
		}
		return score;
	}
	
	public static int calcContribution(WrapToken token, Cluster cluster) {
		return 0;		
	}
}
