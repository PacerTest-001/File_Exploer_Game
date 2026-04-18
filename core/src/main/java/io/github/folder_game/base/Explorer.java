package io.github.folder_game.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Explorer {

    public Folder head;
    public Folder currentFolder;
    public Map<String, BaseFile> allFiles;

    public Explorer() {
        head = new Folder("Base");
        currentFolder = head;

        allFiles = new HashMap<String, BaseFile>();
        allFiles.put(head.name, head);

        Folder f = new Folder("Something");
        f.parent = head;
        allFiles.put(f.name, f);

        Folder f2 = new Folder("Something2");
        f2.parent = head;
        allFiles.put(f2.name, f2);
    }
}
