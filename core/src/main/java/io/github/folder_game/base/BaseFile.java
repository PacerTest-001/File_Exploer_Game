package io.github.folder_game.base;

import com.badlogic.gdx.math.Rectangle;
import com.github.tommyettinger.digital.Base;

public class BaseFile {
    public String name;
    public String type;
    public Folder parent;
    public Rectangle hitBox;

    public BaseFile(String name, String type) {
        this.name = name;
        this.type = type;
        this.hitBox = new Rectangle();
    }
    public BaseFile(String name, String type, Folder parent) {
        this.name = name;
        this.type = type;
        this.hitBox = new Rectangle();
        this.parent = parent;
    }

    public void changeParent(Folder newParent) {
        this.parent = newParent;
    }
}
