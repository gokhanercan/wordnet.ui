package isik.wordnet.ui;

import WordNet.WordNet;

class WordNetService
{
    private static WordNet _WordNetTurkish = null;
    private static WordNet _WordNetEnglish = null;

    public static WordNet getTurkishWordNet(){
        if (_WordNetTurkish == null){
            _WordNetTurkish = new WordNet();                        //lazy load.
        }
        return _WordNetTurkish;
    }

    public static WordNet getEnglishWordNet(){
        if (_WordNetEnglish == null){
            _WordNetEnglish = new WordNet("english_wordnet_version_31.xml");                       //lazy load.
        }
        return _WordNetEnglish;
    }


}