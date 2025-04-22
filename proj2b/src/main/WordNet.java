package main;

import edu.princeton.cs.algs4.In;
import ngrams.TimeSeries;

import java.sql.Array;
import java.util.*;
import java.util.regex.Pattern;

public class WordNet {
    private HashMap<Integer, String> synSetMap;
    private HashMap<String, Integer> reverseSynSetMap;
    private Graph hyponymGraph;
    Set<String> reverseSynSetKeys;

    public WordNet(String hyponymsFileName, String synSetsFileName) {
        synSetMap = new HashMap<>();
        reverseSynSetMap = new HashMap<>();
        hyponymGraph = new Graph();
        In inSynSets = new In(synSetsFileName);
        In inHyponyms = new In(hyponymsFileName);

        while (!inSynSets.isEmpty()) {
            String nextLine = inSynSets.readLine();
            String[] splitLine = nextLine.split(",");
            Integer synSetID = Integer.parseInt(splitLine[0]);
            String synSet = splitLine[1];
            reverseSynSetMap.putIfAbsent(synSet, synSetID);
            synSetMap.putIfAbsent(synSetID, synSet);
        }

        while (!inHyponyms.isEmpty()) {
            String nextLine = inHyponyms.readLine();
            String[] splitLine = nextLine.split(",");
            int synSetID = Integer.parseInt(splitLine[0]);
            for (int i = 1; i < splitLine.length; i++) {
                int hyponymID = Integer.parseInt(splitLine[i]);
                hyponymGraph.addEdge(synSetID, hyponymID);
            }
        }

        reverseSynSetKeys = reverseSynSetMap.keySet();
    }

    private String getSynSet(int synSetID) {
        return synSetMap.get(synSetID);
    }

    private Set<Integer> getSynSetID(String word) {
        Set<Integer> wordSynSetIDs = new HashSet<>();
        int i = 0;
        String regex = "\\b" + Pattern.quote(word) + "\\b";

        Pattern pattern = Pattern.compile(regex);

        for (String key : reverseSynSetKeys){
            if (pattern.matcher(key).find()){
                wordSynSetIDs.add(reverseSynSetMap.get(key));
            }
        }

        return wordSynSetIDs;
    }

    private Set<Integer> getSynSetHyponyms(int synSetID, Set<Integer> hyponymIDSet, Set<Integer> visitedNodes) {
        hyponymIDSet.add(synSetID);

        if (!hyponymGraph.containsKey(synSetID) || visitedNodes.contains(synSetID)) {
            return hyponymIDSet;
        }

        visitedNodes.add(synSetID);

        for (int neighbor : hyponymGraph.getNeighbors(synSetID)){
            hyponymIDSet.addAll(getSynSetHyponyms(neighbor, hyponymIDSet, visitedNodes));
        }

        return hyponymIDSet;
    }

    private Set<Integer> getSynSetHyponyms(int synSetID) {
        Set<Integer> hyponymIDSet = new HashSet<>();
        Set<Integer> visitedNodes = new HashSet<>();
        getSynSetHyponyms(synSetID, hyponymIDSet, visitedNodes);
        return hyponymIDSet;
    }

    public Set<String> getAllHyponyms(String word) {
        Set<String> hyponymSet =  new TreeSet<>();
        Set<Integer> wordSynSetIDs = getSynSetID(word);
        Set<Integer> hyponymIDSet = new HashSet<>();

        if (wordSynSetIDs.isEmpty()){
            return hyponymSet;
        }

        for (int synSetID : wordSynSetIDs) {
            hyponymIDSet.addAll(getSynSetHyponyms(synSetID));
        }

        wordSynSetIDs.addAll(hyponymIDSet);

        for (int synSetID : wordSynSetIDs) {
            hyponymSet.addAll(List.of(getSynSet(synSetID).split(" ")));
        }

        return hyponymSet;
    }
}
