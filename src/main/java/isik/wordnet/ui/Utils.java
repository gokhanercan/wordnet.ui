package isik.wordnet.ui;

import WordNet.Relation;
import WordNet.SemanticRelation;
import WordNet.SemanticRelationType;
import WordNet.SynSet;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static isik.wordnet.ui.WordNetUI.turkish;

class Utils {
    static void searchInWordnet(String searchLiteral, TreeData<String> treeData, TreeDataProvider<String> inMemoryDataProvider, Set<String> leaves) {
        treeData.clear();
        inMemoryDataProvider.refreshAll();
//        debugTextField.setValue("I am clicked2");
        ArrayList<SynSet> results = turkish.getSynSetsWithLiteral(searchLiteral);
//        debugTextField.setValue(debugTextField.getValue() + " " + searchBox.getSearchLiteral() + " " + results.size());

        Set<String> posList = new HashSet<>();
        for (SynSet result : results) {
            String example = result.getExample();
            String pos = result.getPos().toString();
            String synset = "(" + pos + ") " + result.representative() + " (" + result.getDefinition() + ")";
            System.out.println("Synonyms" + result.getSynonym().toString());
            System.out.println("litetal size" + result.getSynonym().literalSize());

            if (example != null) {
                synset += " \"" + example + "\"";
            }
            if (!posList.contains(pos)) {
                treeData.addItem(null, pos);
                posList.add(pos);
            }
            treeData.addItem(result.getPos().toString(), synset);
            int relSize = result.relationSize();
            if (relSize > 0) {
                System.out.println("Relations:");
                ArrayList<SynSet> hypernyms = new ArrayList<>();
                ArrayList<SynSet> hyponyms = new ArrayList<>();
                for (int j = 0; j < relSize; j++) {
                    Relation relation = result.getRelation(j);
                    System.out.println(relation.getName() + " " + relation);
                    if (relation instanceof SemanticRelation) {
                        if (((SemanticRelation) relation).getRelationType().equals(SemanticRelationType.HYPERNYM)) {
                            hypernyms.add(turkish.getSynSetWithId(relation.getName()));
                        } else if (((SemanticRelation) relation).getRelationType().
                                equals(SemanticRelationType.HYPONYM)) {
                            hyponyms.add(turkish.getSynSetWithId(relation.getName()));
                        }
                    }
                }

                if (hypernyms.size() > 0) {
                    System.out.println("Hypernyms: " + synset);
                    treeData.addItem(synset, "hypernyms of " + result.representative());
                    for (SynSet hypernym : hypernyms) {
                        System.out.println(hypernym.representative());
                        treeData.addItem("hypernyms of " + result.representative(), hypernym.representative());
                        leaves.add(hypernym.representative());
                    }

                }

                if (hyponyms.size() > 0) {
                    System.out.println("Hyponyms of : " + synset);
                    treeData.addItem(synset, "hyponyms of " + result.representative());
                    for (SynSet hyponym : hyponyms) {
                        System.out.println(hyponym.representative());
                        treeData.addItem("hyponyms of " + result.representative(), hyponym.representative());
                        leaves.add(hyponym.representative());
                    }

                }
            }
        }
        inMemoryDataProvider.refreshAll();
    }
}
