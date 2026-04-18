package io.github.folder_game.base;


public class TextFile extends BaseFile {
    private String data;

    public TextFile(String name) {
        super(name, "TextFile");
        data = "";
    }

    public void upDateText(String newText) {
        this.data = newText;
    }
    public String getData() {
        return this.data;
    }
}
