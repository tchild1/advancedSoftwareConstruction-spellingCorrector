package spell;

import java.io.IOException;
import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;


public class SpellCorrector implements ISpellCorrector {

    ITrie trie = new Trie();

    @Override
    public void useDictionary(String dictionaryFileName) throws IOException {
        File file = new File(dictionaryFileName);
        Scanner scanner = new Scanner(file);

        while(scanner.hasNext()) {
            String str = scanner.next();
            trie.add(str.toLowerCase());
        }
    }

    @Override
    public String suggestSimilarWord(String inputWord) {
        inputWord = inputWord.toLowerCase();
        if (trie.find(inputWord) != null) {
            return inputWord;
        }

        ArrayList deletionArray = deletionDistance(1, inputWord);
        ArrayList transpositionArray = transpositionDistance(1, inputWord);
        ArrayList alterationArray = alterationDistance(1, inputWord);
        ArrayList insertionArray = insertionDistance(1, inputWord);

        if (deletionArray.isEmpty() && transpositionArray.isEmpty() && alterationArray.isEmpty() && insertionArray.isEmpty()) {
            return null;
        }

        ArrayList allMatches = new ArrayList<String>();
        if (deletionArray.isEmpty()) {
            deletionArray.add(0, '3');
        }
        if (transpositionArray.isEmpty()) {
            transpositionArray.add(0, '3');
        }
        if (alterationArray.isEmpty()) {
            alterationArray.add(0, '3');
        }
        if (insertionArray.isEmpty()) {
            insertionArray.add(0, '3');
        }

        if (deletionArray.get(0).toString().charAt(0) == '1' || transpositionArray.get(0).toString().charAt(0) == '1' || alterationArray.get(0).toString().charAt(0) == '1' || insertionArray.get(0).toString().charAt(0) == '1') {

            if (deletionArray.get(0).toString().charAt(0) == '1') {
                deletionArray.remove(0);
                allMatches.addAll(deletionArray);
            }
            if (transpositionArray.get(0).toString().charAt(0) == '1') {
                transpositionArray.remove(0);
                allMatches.addAll(transpositionArray);
            }
            if (alterationArray.get(0).toString().charAt(0) == '1') {
                alterationArray.remove(0);
                allMatches.addAll(alterationArray);
            }
            if(insertionArray.get(0).toString().charAt(0) == '1') {
                insertionArray.remove(0);
                allMatches.addAll(insertionArray);
            }
        } else {
            if (deletionArray.get(0).toString().charAt(0) == '2') {
                deletionArray.remove(0);
                allMatches.addAll(deletionArray);
            }
            if (transpositionArray.get(0).toString().charAt(0) == '2') {
                transpositionArray.remove(0);
                allMatches.addAll(transpositionArray);
            }
            if (alterationArray.get(0).toString().charAt(0) == '2') {
                alterationArray.remove(0);
                allMatches.addAll(alterationArray);
            }
            if(insertionArray.get(0).toString().charAt(0) == '2') {
                allMatches.addAll(insertionArray);
                insertionArray.remove(0);
                allMatches.addAll(insertionArray);
            }
        }
        for (int match=0;match < allMatches.size(); match++) {
            if (allMatches.get(match) instanceof Integer) {
                allMatches.remove(match);
            }
        }

        ArrayList highFrequentMatches = new ArrayList<String>();
        int highFrequency = 0;
        for (int match=0; match<allMatches.size();match++) {
            int currFrequency = trie.find(allMatches.get(match).toString()).getValue();
            if (currFrequency > highFrequency) {
                highFrequentMatches.clear();
                highFrequentMatches.add(allMatches.get(match).toString());
                highFrequency = currFrequency;
            } else if (currFrequency == highFrequency) {
                highFrequentMatches.add(allMatches.get(match).toString());
            }
        }

        Collections.sort(highFrequentMatches);

        return highFrequentMatches.get(0).toString();
    }

    private ArrayList<String> deletionDistance(int editDistance, String inputWord) {
        ArrayList matchedWords = new ArrayList<String>();
        ArrayList wordsWithEDOne = new ArrayList<String>();
        for (int letter=0; letter<inputWord.length(); letter++) {
            StringBuilder word = new StringBuilder(inputWord);
            word = word.deleteCharAt(letter);
            wordsWithEDOne.add(word.toString());
            if (trie.find(word.toString()) != null) {
                if (matchedWords.isEmpty()) {
                    matchedWords.add(editDistance);
                }
                matchedWords.add(word.toString());
            }
        }
        if (matchedWords.isEmpty() && editDistance == 1) {
            ArrayList matchedWordsWithEDTwo = new ArrayList<String>();
            for (int word=0; word < wordsWithEDOne.size(); word++) {
                matchedWordsWithEDTwo = deletionDistance(2, wordsWithEDOne.get(word).toString());
                matchedWordsWithEDTwo.addAll(transpositionDistance(2, wordsWithEDOne.get(word).toString()));
                matchedWordsWithEDTwo.addAll(alterationDistance(2, wordsWithEDOne.get(word).toString()));
                matchedWordsWithEDTwo.addAll(insertionDistance(2, wordsWithEDOne.get(word).toString()));

                for (int matchedWord=0; matchedWord < matchedWordsWithEDTwo.size(); matchedWord++) {
                    matchedWords.add(matchedWordsWithEDTwo.get(matchedWord));
                }
            }
        }
        return matchedWords;
    }

    private ArrayList transpositionDistance(int editDistance, String inputWord) {
        ArrayList matchedWords = new ArrayList<String>();
        ArrayList wordsWithEDOne = new ArrayList<String>();
        for (int letter=0; letter<inputWord.length()-1; letter++) {
            StringBuilder word = new StringBuilder(inputWord);
            String swapLetter = word.substring(letter, letter+1);
            word = word.deleteCharAt(letter);
            word.insert(letter+1, swapLetter);
            wordsWithEDOne.add(word.toString());
            if (trie.find(word.toString()) != null) {
                if (matchedWords.isEmpty()) {
                    matchedWords.add(editDistance);
                }
                matchedWords.add(word.toString());
            }
        }
        if (matchedWords.isEmpty() && editDistance==1) {
            for (int edOneWord=0;edOneWord < wordsWithEDOne.size(); edOneWord++) {
                ArrayList wordsWithEDTwo = transpositionDistance(2, wordsWithEDOne.get(edOneWord).toString());
                wordsWithEDTwo.addAll(deletionDistance(2, wordsWithEDOne.get(edOneWord).toString()));
                wordsWithEDTwo.addAll(alterationDistance(2, wordsWithEDOne.get(edOneWord).toString()));
                wordsWithEDTwo.addAll(insertionDistance(2, wordsWithEDOne.get(edOneWord).toString()));
                for (int wordFound=0; wordFound < wordsWithEDTwo.size(); wordFound++) {
                    matchedWords.add(wordsWithEDTwo.get(wordFound));
                }
            }
        }
        return matchedWords;
    }

    private ArrayList alterationDistance(int editDistance, String inputWord) {
        ArrayList matchedWords = new ArrayList<String>();
        ArrayList wordsWithEDOne = new ArrayList<String>();
        for (int letter=0; letter<inputWord.length(); letter++) {
            for (int subLetter=0; subLetter<26; subLetter++) {
                StringBuilder word = new StringBuilder(inputWord);
                word.deleteCharAt(letter);
                word.insert(letter, (char) (subLetter + 'a'));
                wordsWithEDOne.add(word.toString());
                if (trie.find(word.toString()) != null) {
                    if (matchedWords.isEmpty()) {
                        matchedWords.add(editDistance);
                    }
                    matchedWords.add(word.toString());
                }
            }
        }
        if (matchedWords.isEmpty() && editDistance==1) {
            for (int edOneWord=0; edOneWord < wordsWithEDOne.size(); edOneWord++) {
                ArrayList wordsWithEDTwo = alterationDistance(2, wordsWithEDOne.get(edOneWord).toString());
                wordsWithEDTwo.addAll(deletionDistance(2, wordsWithEDOne.get(edOneWord).toString()));
                wordsWithEDTwo.addAll(transpositionDistance(2, wordsWithEDOne.get(edOneWord).toString()));
                wordsWithEDTwo.addAll(insertionDistance(2, wordsWithEDOne.get(edOneWord).toString()));
                matchedWords.addAll(wordsWithEDTwo);
            }
        }

        return matchedWords;
    }

    private ArrayList insertionDistance(int editDistance, String inputWord) {
        ArrayList matchedWords = new ArrayList<String>();
        ArrayList wordsWithEDOne = new ArrayList<String>();
        for (int position=0; position < inputWord.length()+1; position++) {
            for (int letter=0; letter<26; letter++) {
                StringBuilder word = new StringBuilder(inputWord);
                word.insert(position, (char) (letter + 'a'));
                wordsWithEDOne.add(word.toString());
                if (trie.find(word.toString()) != null) {
                    if (matchedWords.isEmpty()) {
                        matchedWords.add(editDistance);
                    }
                    matchedWords.add(word.toString());
                }
            }
        }
        if (matchedWords.isEmpty() && editDistance==1) {
            for (int wordWithEDOne=0; wordWithEDOne<wordsWithEDOne.size(); wordWithEDOne++) {
                ArrayList wordsWithEDTwo = insertionDistance(2,wordsWithEDOne.get(wordWithEDOne).toString());
                wordsWithEDTwo.addAll(deletionDistance(2, wordsWithEDOne.get(wordWithEDOne).toString()));
                wordsWithEDTwo.addAll(alterationDistance(2, wordsWithEDOne.get(wordWithEDOne).toString()));
                wordsWithEDTwo.addAll(transpositionDistance(2, wordsWithEDOne.get(wordWithEDOne).toString()));
                matchedWords.addAll(wordsWithEDTwo);
            }
        }
        return matchedWords;
    }
}
