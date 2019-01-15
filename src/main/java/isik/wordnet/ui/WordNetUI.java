package isik.wordnet.ui;

import javax.servlet.annotation.WebServlet;

import WordNet.*;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class WordNetUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        WordNet turkish = new WordNet();
        VerticalLayout wrapper = new VerticalLayout();
        SearchBox searchBox = new SearchBox("Enter Your Word", VaadinIcons.SEARCH);
//        TextField searchBox = new TextField("Enter your word");
//        Button searchBtn = new Button("Search");
//        search.addComponents(searchBox, searchBtn);
        Button searchBtn = searchBox.getButton();

        TextField temp = new TextField();
        Tree<String> tree = new Tree<>();
        TreeData<String> treeData = new TreeData<>();
        TreeDataProvider<String> inMemoryDataProvider = new TreeDataProvider<>(treeData);
        Button btn = new Button();
        btn.addClickListener(e -> {
            temp.setValue("I am clicked1");
            ArrayList<SynSet> results = turkish.getSynSetsWithLiteral(searchBox.getSearchLiteral());
            System.out.println("Length of results " + results.size());
            for (SynSet result : results) {
                treeData.addItem(null, result.representative());
                treeData.addItem(result.representative(), result.getPos().toString());
                treeData.addItem(result.representative(), result.getDefinition());
                treeData.addItem(result.representative(), result.getLongDefinition());
                treeData.addItem(result.representative(), result.getExample());
                treeData.addItem(result.representative(), result.getSynonym().toString());
                //treeData.removeItem("Mercury");
                inMemoryDataProvider.refreshAll();
                temp.setValue(temp.getValue() + result.representative());
            }

        });

        searchBtn.addClickListener(e -> {
            treeData.clear();
            inMemoryDataProvider.refreshAll();
            temp.setValue("I am clicked2");
            ArrayList<SynSet> results = turkish.getSynSetsWithLiteral(searchBox.getSearchLiteral());
            temp.setValue(temp.getValue()  + " " + searchBox.getSearchLiteral() + " " + results.size());

            Set<String> posList = new HashSet<>();
            for (SynSet result : results) {
                String example = result.getExample();
                String pos = result.getPos().toString();
                String synset = "(" + pos + ") " + result.representative() + " (" + result.getDefinition() + ")";
                System.out.println("Synonyms" + result.getSynonym().toString());
                System.out.println("litetal size" + result.getSynonym().literalSize());

                if(example != null){
                    synset += " \"" + example + "\"";
                }
                if(!posList.contains(pos)){
                    treeData.addItem(null, pos);
                    posList.add(pos);
                }
                treeData.addItem(result.getPos().toString(), synset);
                int relSize = result.relationSize();
                if (relSize > 0) {
                    System.out.println("Relations:");
                    ArrayList<SynSet> hypernyms = new ArrayList<SynSet>();
                    ArrayList<SynSet> hyponyms = new ArrayList<SynSet>();
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
                        }

                    }

                    if (hyponyms.size() > 0) {
                        System.out.println("Hyponyms of : " + synset);
                        treeData.addItem(synset, "hyponyms of " + result.representative());
                        for (SynSet hyponym : hyponyms) {
                            System.out.println(hyponym.representative());
                            treeData.addItem("hyponyms of " + result.representative(), hyponym.representative());
                        }

                    }
                }
                /*
                treeData.addItem(null, result.representative());
                //treeData.addItem(result.representative(), result.getPos().toString());
                treeData.addItem(result.representative(), result.getDefinition());
                //treeData.addItem(result.representative(), result.getLongDefinition());
                treeData.addItem(result.representative(), result.getExample());
                treeData.addItem(result.representative(), result.getSynonym().toString());
                */

                //treeData.removeItem("Mercury");
                //treeData.removeItem("Mercury");
                inMemoryDataProvider.refreshAll();
            }
        });
        /*
        // Couple of childless root items
        treeData.addItem(null, "Mercury");
        treeData.addItem(null, "Venus");

        // Items with hierarchy
        treeData.addItem(null, "Earth");
        treeData.addItem("Earth", "The Moon");
        */
        tree.setDataProvider(inMemoryDataProvider);
        //tree.expand("Earth");

        wrapper.addComponents(searchBox, tree, btn, temp);
        setContent(wrapper);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = WordNetUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
