package com.nitsha.binds.utils;

import java.util.ArrayList;
import java.util.List;

public class SearchUtil {

    public static boolean matches(String id, String query) {
        if (query == null || query.isEmpty()) return true;

        String idLow = id.toLowerCase();
        String qLow = query.toLowerCase().trim();

        if (idLow.contains(qLow)) return true;

        String[] queryWords = qLow.split("\\s+");
        String[] idWords = idLow.replace(":", " ").replace(".", " ").replace("_", " ").split("\\s+");

        if (allWordsMatch(queryWords, idLow)) return true;

        if (allWordsPrefixMatch(queryWords, idWords)) return true;

        if (qLow.length() < 3) return false;

        for (String qWord : queryWords) {
            boolean foundFuzzy = false;
            for (String idWord : idWords) {
                if (idWord.startsWith(qWord)) { foundFuzzy = true; break; }
                if (qWord.length() >= 3 && bigramSimilarity(qWord, idWord) >= 0.55) { foundFuzzy = true; break; }
            }
            if (!foundFuzzy) return false;
        }

        return true;
    }

    private static boolean allWordsMatch(String[] queryWords, String id) {
        for (String word : queryWords) {
            if (!id.contains(word)) return false;
        }
        return true;
    }

    private static boolean allWordsPrefixMatch(String[] queryWords, String[] idWords) {
        for (String qWord : queryWords) {
            boolean found = false;
            for (String idWord : idWords) {
                if (idWord.startsWith(qWord)) { found = true; break; }
            }
            if (!found) return false;
        }
        return true;
    }

    public static double bigramSimilarity(String a, String b) {
        if (a.length() < 2 || b.length() < 2) return a.equals(b) ? 1.0 : 0.0;
        List<String> bigramsA = bigrams(a);
        List<String> bigramsB = new ArrayList<>(bigrams(b));
        int matches = 0;
        for (String bg : bigramsA) {
            if (bigramsB.remove(bg)) matches++;
        }
        return (2.0 * matches) / (bigramsA.size() + bigrams(b).size());
    }

    public static List<String> bigrams(String s) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < s.length() - 1; i++) result.add(s.substring(i, i + 2));
        return result;
    }
}