package ece;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.concurrent.Flow;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomCodeArea extends CodeArea {
    private String[] KEYWORDS1,KEYWORDS2,KEYWORDS3;
    private String KEYWORD1_PATTERN,KEYWORD2_PATTERN,KEYWORD3_PATTERN;
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
    private static final String INCLUDE_PATTERN = "#include([^\n])*\n";
    private MenuItem menuCut = new MenuItem("Cut");
    private MenuItem menuCopy = new MenuItem("Copy");
    private MenuItem menuPaste = new MenuItem("Paste");
    private MenuItem menuSelectAll = new MenuItem("Select All");
    private Pattern PATTERN;

    public CustomCodeArea(){
        super();
        this.setId("codeArea");
        ObjectMapper objectMapper = new ObjectMapper();
        KeywordClass keywords;
        try{
            keywords = objectMapper.readValue(new File(getClass().getResource("json/syntaxKeywords.json").toURI()), KeywordClass.class);
        }catch (Exception e){e.printStackTrace();return;}
        KEYWORDS1 = keywords.KEYWORD1;
        KEYWORDS2 = keywords.KEYWORD2;
        KEYWORDS3 = keywords.KEYWORD3;
        KEYWORD1_PATTERN = "\\b(" + String.join("|", KEYWORDS1) + ")\\b";
        KEYWORD2_PATTERN = "\\b(" + String.join("|", KEYWORDS2) + ")\\b";
        KEYWORD3_PATTERN = "\\b(" + String.join("|", KEYWORDS3) + ")\\b";
        PATTERN = Pattern.compile(
                "(?<KEYWORD1>" + KEYWORD1_PATTERN + ")"
                        + "|(?<KEYWORD2>" + KEYWORD2_PATTERN + ")"
                        + "|(?<KEYWORD3>" + KEYWORD3_PATTERN + ")"
                        + "|(?<PAREN>" + PAREN_PATTERN + ")"
                        + "|(?<BRACE>" + BRACE_PATTERN + ")"
                        + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                        + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                        + "|(?<STRING>" + STRING_PATTERN + ")"
                        + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                        + "|(?<INCLUDE>" + INCLUDE_PATTERN + ")"
        );
        this.setParagraphGraphicFactory(LineNumberFactory.get(this));
//        this.setEditable(false);
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setOnShowing(windowEvent -> {
            //disable if nothing selected
            menuCopy.setDisable(this.getSelectedText().length()<=0);
            menuCut.setDisable(this.getSelectedText().length()<=0);
        });
        MenuHandler menuHandler = new MenuHandler();
        menuCopy.setOnAction(menuHandler);
        menuCut.setOnAction(menuHandler);
        menuPaste.setOnAction(menuHandler);
        menuSelectAll.setOnAction(menuHandler);
        contextMenu.getItems().addAll(menuCopy,menuCut,menuPaste,menuSelectAll);

        this.setContextMenu(contextMenu);
        this.textProperty().addListener((observableValue, s, t1) -> refreshHighlight());
    }

    private void refreshHighlight(){
        this.setStyleSpans(0,computeHighlighting(this.getText()));
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD1") != null ? "keyword1" :
                            matcher.group("KEYWORD2") != null ? "keyword2" :
                                    matcher.group("KEYWORD3") != null ? "keyword3" :
                            matcher.group("PAREN") != null ? "paren" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                            matcher.group("INCLUDE") != null ? "include" :
                                                                            null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
    static class KeywordClass{
        public String[] KEYWORD1;
        public String[] KEYWORD2;
        public String[] KEYWORD3;
    }
    private class MenuHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent actionEvent) {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            String str = CustomCodeArea.this.getSelectedText();
            if(actionEvent.getSource() == menuCopy){
//                System.out.println(CustomCodeArea.this.getSelectedText());
                if(str.length()>0){
                    content.putString(str);
                    clipboard.setContent(content);
                }
            }else if(actionEvent.getSource() == menuCut){
                if(str.length()>0){
                    content.putString(str);
                    clipboard.setContent(content);
                    CustomCodeArea.this.replaceText(CustomCodeArea.this.getSelection(),"");
                }
            }else if(actionEvent.getSource() == menuPaste){
                CustomCodeArea.this.replaceSelection(clipboard.getString());
                refreshHighlight();
            }else if(actionEvent.getSource() == menuSelectAll){
                CustomCodeArea.this.selectAll();
            }
        }
    }
}