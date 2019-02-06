package isik.wordnet.ui;

import WordNet.*;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;

import java.util.ArrayList;
import java.util.Set;

import static isik.wordnet.ui.WordNetUI.english;
import static isik.wordnet.ui.WordNetUI.turkish;

class Utils {
    private static SemanticRelationType[] RELATIONS_TO_DISPLAY = {SemanticRelationType.HYPERNYM,
            SemanticRelationType.HYPONYM, SemanticRelationType.INSTANCE_HYPERNYM, SemanticRelationType.INSTANCE_HYPONYM,
            SemanticRelationType.ANTONYM, SemanticRelationType.ALSO_SEE,
            SemanticRelationType.MEMBER_HOLONYM, SemanticRelationType.SUBSTANCE_HOLONYM, SemanticRelationType.PART_HOLONYM,
            SemanticRelationType.MEMBER_MERONYM, SemanticRelationType.SUBSTANCE_MERONYM, SemanticRelationType.PART_MERONYM,
            SemanticRelationType.DOMAIN_TOPIC};

    private static void addRelationSynsetsToTree(ArrayList<SynSet> synsets, String relationName, SynSet result, TreeItem resultWithPosItem,
                                                 TreeData<TreeItem> treeData, Set<String> leaves) {
        if (synsets.size() > 0) {
            TreeItem resultItem = new TreeItem(relationName + " of " + result.representative(), result.getId());
            treeData.addItem(resultWithPosItem, resultItem);
            for (SynSet synset : synsets) {
                TreeItem synsetItem = new TreeItem(synset.representative(), synset.getId());
                treeData.addItem(resultItem, synsetItem);
                leaves.add(synsetItem.toString());
            }

        }
    }

    private static void searchRelation(SynSet result, SemanticRelationType relationType, TreeItem resultWithPosItem,
                                       TreeData<TreeItem> treeData, Set<String> leaves) {
        ArrayList<SynSet> synSets = new ArrayList<>();
        for (int i = 0; i < result.relationSize(); i++) {
            Relation relation = result.getRelation(i);
            System.out.println(relation.getName() + " " + relation);
            if (relation instanceof SemanticRelation) {
                if (((SemanticRelation) relation).getRelationType().equals(relationType)) {
                    synSets.add(turkish.getSynSetWithId(relation.getName()));
                }
            }
        }
        String relationName = relationType.toString().toLowerCase().replace("_", " ");
        relationName = relationName.substring(0, 1).toUpperCase() + relationName.substring(1) + "s";
        addRelationSynsetsToTree(synSets, relationName, result, resultWithPosItem, treeData, leaves);
    }

    static void searchInWordnet(String searchLiteral, TreeData<TreeItem> treeData, TreeDataProvider<TreeItem> inMemoryDataProvider,
                                Set<String> leaves) {
        treeData.clear();
        inMemoryDataProvider.refreshAll();
        ArrayList<SynSet> results = turkish.getSynSetsWithLiteral(searchLiteral);

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
            treeData.addItem(null, resultWithPosItem);

            System.out.println("Synonyms are " + result.getSynonym());
            Synonym synonyms = result.getSynonym();
            int synonymCount = synonyms.literalSize();
            if (synonymCount > 1) {
                TreeItem resultItem = new TreeItem("Synonyms" + " of " + result.representative(), result.getId());
                treeData.addItem(resultWithPosItem, resultItem);
                for (int i = 0; i < synonymCount; i++) {
                    String synonym = synonyms.getLiteral(i).getName();
                    if (!synonym.equals(result.representative())) {
                        TreeItem synsetItem = new TreeItem(synonym, result.getId());
                        treeData.addItem(resultItem, synsetItem);
                        leaves.add(synsetItem.toString());
                    }
                }
            }
            if (result.relationSize() > 0) {
                for (SemanticRelationType relationType : RELATIONS_TO_DISPLAY) {
                    searchRelation(result, relationType, resultWithPosItem, treeData, leaves);
                }
            }
            ArrayList<SynSet> interlingualResults = result.getInterlingual(english);
            addRelationSynsetsToTree(interlingualResults, "English", result, resultWithPosItem,
                    treeData, leaves);
        }
        inMemoryDataProvider.refreshAll();
    }
}
