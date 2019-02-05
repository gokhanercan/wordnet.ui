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
    static void searchInWordnet(String searchLiteral, TreeData<TreeItem> treeData, TreeDataProvider<TreeItem> inMemoryDataProvider,
                                Set<String> leaves) {
        treeData.clear();
        inMemoryDataProvider.refreshAll();
//        debugTextField.setValue("I am clicked2");
        ArrayList<SynSet> results = turkish.getSynSetsWithLiteral(searchLiteral);
//        debugTextField.setValue(debugTextField.getValue() + " " + searchBox.getSearchLiteral() + " " + results.size());

        Set<String> posList = new HashSet<>();
        for (SynSet result : results) {
            String example = result.getExample();
            String pos = result.getPos().toString();
            String synsetString = "(" + pos + ") " + result.representative() + " (" + result.getDefinition() + ")";
            System.out.println("Synonyms" + result.getSynonym().toString());
            System.out.println("literal size" + result.getSynonym().literalSize());

            if (example != null) {
                synsetString += " \"" + example + "\"";
            }
            TreeItem resultWithPosItem = new TreeItem(synsetString, result.getId());
            System.out.println("posItem" + resultWithPosItem.toString());
            //if (!posList.contains(pos)) {
            treeData.addItem(null, resultWithPosItem);
                posList.add(pos);
            //}
            //TreeItem synsetItem = new TreeItem(synsetString, "");
            //treeData.addItem(resultWithPosItem, synsetItem);

            int relSize = result.relationSize();
            if (relSize > 0) {
                System.out.println("Relations:");
                ArrayList<SynSet> hypernyms = new ArrayList<>();
                ArrayList<SynSet> hyponyms = new ArrayList<>();
                ArrayList<SynSet> antonyms = new ArrayList<>();
                for (int j = 0; j < relSize; j++) {
                    Relation relation = result.getRelation(j);
                    System.out.println(relation.getName() + " " + relation);
                    if (relation instanceof SemanticRelation) {
                        if (((SemanticRelation) relation).getRelationType().equals(SemanticRelationType.HYPERNYM)) {
                            hypernyms.add(turkish.getSynSetWithId(relation.getName()));
                        } else if (((SemanticRelation) relation).getRelationType().
                                equals(SemanticRelationType.HYPONYM)) {
                            hyponyms.add(turkish.getSynSetWithId(relation.getName()));
                        } else if (((SemanticRelation) relation).getRelationType().
                                equals(SemanticRelationType.ANTONYM)) {
                            antonyms.add(turkish.getSynSetWithId(relation.getName()));
                        }
                    }
                }

                if (hypernyms.size() > 0) {
                    System.out.println("Hypernyms: " + synsetString);
                    System.out.println("Synset strıng ıs:" + synsetString);
                    System.out.println("Its child is:" + result.representative());
                    TreeItem resultItem = new TreeItem("Hypernyms of " + result.representative(), result.getId());
                    treeData.addItem(resultWithPosItem, resultItem);
                    for (SynSet hypernym : hypernyms) {
                        System.out.println(hypernym.representative());
                        TreeItem hypernymItem = new TreeItem(hypernym.representative(), hypernym.getId());
                        treeData.addItem(resultItem, hypernymItem);
                        leaves.add(hypernymItem.toString());
                    }

                }

                if (hyponyms.size() > 0) {
                    System.out.println("Hyponyms of : " + synsetString);
                    TreeItem resultItem = new TreeItem("Hyponyms of " + result.representative(),
                            result.getId());
                    treeData.addItem(resultWithPosItem, resultItem);
                    for (SynSet hyponym : hyponyms) {
                        System.out.println(hyponym.representative());
                        TreeItem hyponymItem = new TreeItem(hyponym.representative(), hyponym.getId());
                        treeData.addItem(resultItem, hyponymItem);
                        leaves.add(hyponymItem.toString());
                    }

                }

                if (antonyms.size() > 0) {
                    System.out.println("Antonyms of : " + synsetString);
                    TreeItem resultItem = new TreeItem("Antonyms of " + result.representative(), result.getId());
                    treeData.addItem(resultWithPosItem, resultItem);
                    for (SynSet antonym : antonyms) {
                        System.out.println(antonym.representative() + "-" + antonym.getId());
                        TreeItem antonymItem = new TreeItem(antonym.representative(), antonym.getId());
                        treeData.addItem(resultItem, antonymItem);
                        leaves.add(antonymItem.toString());
                    }
                }
            }
        }
        inMemoryDataProvider.refreshAll();
    }
}
