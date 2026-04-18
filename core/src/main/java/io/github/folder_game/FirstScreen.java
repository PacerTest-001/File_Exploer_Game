package io.github.folder_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.github.tommyettinger.digital.Base;
import io.github.folder_game.base.BaseFile;
import io.github.folder_game.base.Explorer;
import io.github.folder_game.base.Folder;
import io.github.folder_game.base.TextFile;
import org.apache.tools.ant.util.UnicodeUtil;
import org.w3c.dom.Text;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    // Texture Set up.
    boolean paused = false;
    Texture txt_Texture;
    Texture folder_Texture;
    Texture bg_Texture;
    Texture back_Arrow;

    Texture properties_Texture;

    SpriteBatch spriteBatch;
    FitViewport viewport;

    int amountInRow = 5;
    float scaleAmount = 100;

    Vector2 touchPos;
    boolean barVisible = false;
    boolean wipeBar = false;

    Stage stage;
    Skin skin;

    // Logic Set Up.
    Explorer explorer;
    Random random;

    Rectangle mouseClick;
    Rectangle backArrowBox;

    Rectangle selectionRange;

    Rectangle addNew;
    Rectangle deleteItem;

    BitmapFont font;

    // Debug usage
    Texture hitBoxShow = new Texture("ui/Block.png");
    boolean debugMode = false;

    @Override
    public void show() {
        // Prepare your screen here.
        txt_Texture = new Texture("ui/txt_Texture.png");
        folder_Texture = new Texture("ui/folder_Texture.png");
        bg_Texture = new Texture("ui/BG_Image.png");
        back_Arrow = new Texture("ui/Back_Arrow.png");
        properties_Texture = new Texture("ui/Properties_Image.png");

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8 * scaleAmount, 5 * scaleAmount);

        touchPos = new Vector2();

        // Logic shit. 👍👍👍👍
        explorer = new Explorer();

        random = new Random();

        mouseClick = new Rectangle();
        backArrowBox = new Rectangle();

        selectionRange = new Rectangle();

        addNew =  new Rectangle();
        deleteItem = new Rectangle();

        // Font stuff (Low key got stuck here because of text being huge)

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/GalaferaMedium-V4xze.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = (int)(Gdx.graphics.getHeight() * (scaleAmount * 0.0005)); // 5% of screen height

        // Loading true font
        font = generator.generateFont(parameter);
        font.setColor(Color.BLACK);
        font.getData().setScale(0.5f);
    }

    @Override
    public void render(float delta) {
        // Draw your screen here. "delta" is the time since last render in seconds.
        // System.out.println(delta);
        input();

        if (paused) return;

        logic();
        draw();
    }

    private void input() {
        // Game Input

        // These will only fire when pressed.
        if (Gdx.input.isButtonJustPressed(0)) {
            // Left Click

            // This tells the logic method to stop rendering the bar after the logic for it is played out.
            if (barVisible) {
                wipeBar = true;
            }

            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            mouseClick.set(touchPos.x, touchPos.y, 0.01f * scaleAmount, 0.01f * scaleAmount);

        } else if (Gdx.input.isButtonJustPressed(1)) {
            // Right Click

            // toggles the bars visiblity.
            if (barVisible) {
                barVisible = false;
            } else {
                barVisible = true;

                touchPos.set(Gdx.input.getX(), Gdx.input.getY());
                viewport.unproject(touchPos);

                selectionRange.set(touchPos.x, touchPos.y, 0.01f * scaleAmount, 0.01f * scaleAmount);

                // Adding rectangles for collision with player mouse.
                addNew.set(touchPos.x, touchPos.y - 0.425f * scaleAmount, 3.5f * scaleAmount, 0.4f * scaleAmount);
                deleteItem.set(touchPos.x, touchPos.y - 0.9f * scaleAmount, 3.5f * scaleAmount, 0.4f * scaleAmount);
            }
        }
    }

    // This is the logic that runs every render
    private void logic() {
        // Most of this is handling the location of where the player clicked and how the game responds
        if (barVisible) {
            // wipe bar means that the 1st mouse button was clicked and at the end will get rid of the bar.
            if (wipeBar) {
                System.out.println("WIPE");

                // handles what happens when you create something.
                if (mouseClick.overlaps(addNew)) {
                    System.out.println("Adding folder.");
                    Folder f = new Folder("LOL_" + random.nextInt(9999));
                    f.parent = explorer.currentFolder;
                    explorer.allFiles.put(f.name, f);
                }

                if (mouseClick.overlaps(deleteItem)) {
                    for (BaseFile file : explorer.allFiles.values()) {
                        if (file.parent == explorer.currentFolder && selectionRange.overlaps(file.hitBox)) {
                            file.parent = null;
                        }
                    }
                }

                mouseClick.set(0, 0, 0, 0);
                barVisible = false;
                wipeBar = false;
            }

        }

        BaseFile fileToOpen = null;
        for (BaseFile baseFile : explorer.allFiles.values()) {
            if (baseFile.parent == explorer.currentFolder) {
//                System.out.println(mouseClick.getX() + ", " + mouseClick.getY() + "|" + baseFile.hitBox.getX() + "," + baseFile.hitBox.getY());
                if (mouseClick.overlaps(baseFile.hitBox)) {
                    System.out.println(baseFile.name);
                    mouseClick.set(-1 * scaleAmount, -1 * scaleAmount, 0.01f * scaleAmount, 0.01f * scaleAmount);
                    fileToOpen = baseFile;
                    break;
                }
            }
        }

        if (mouseClick.overlaps(backArrowBox)) {
            if (explorer.currentFolder.parent != null) {
                explorer.currentFolder = explorer.currentFolder.parent;
            }
        }

        mouseClick.set(-1 * scaleAmount, -1 * scaleAmount, 0.1f * scaleAmount, 0.1f * scaleAmount);

        if (fileToOpen != null) {
            switch (fileToOpen.type) {
                case "Folder": {
                    explorer.currentFolder = (io.github.folder_game.base.Folder) fileToOpen;
                    break;
                }
                case "TextFile": {
                    System.out.println("Something is here type shit"); // TODO: make the text/document system forr saving and editing.
                    break;
                }
            }
        }
    }

    // Wipes the orginal screen then draws all the objects on the screen every frame.
    private void draw() {
        // Clears the screen
        ScreenUtils.clear(Color.BLACK);
//        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        // Start drawing.
        spriteBatch.begin();

        float width = viewport.getWorldWidth();
        float height = viewport.getWorldHeight();

        spriteBatch.draw(bg_Texture, 0, 0, width, height);
        spriteBatch.draw(back_Arrow, 7.25f * scaleAmount, 4.25f * scaleAmount, 0.5f * scaleAmount, 0.5f * scaleAmount);
        backArrowBox.set(7.25f * scaleAmount, 4.25f * scaleAmount, 0.5f * scaleAmount, 0.5f * scaleAmount);
        if (debugMode) {
            spriteBatch.draw(hitBoxShow, 7.25f * scaleAmount, 4.25f * scaleAmount, 0.5f * scaleAmount, 0.5f * scaleAmount);
        }

        float startingX = 2.075f * scaleAmount;
        float startingY = 3.075f * scaleAmount;

        int amountX = 0;
        int amountY = 0;

        /*
        This loops through all the possible base files.
        If the file is inside/is a "child" of the current folder that is being viewed,
        then we visualize it.
         */
        int amountFound = 0;
        for (BaseFile file : explorer.allFiles.values()) {

            // Makes sure that the current file is not the head/top of the explorer.
            if (file.parent != null && file.parent == explorer.currentFolder) {

                Texture usedTexture = getTextureForType(file.type);

                if (usedTexture != null) {

                    spriteBatch.draw(usedTexture, amountX + startingX, startingY - amountY, 0.75f * scaleAmount, 0.75f * scaleAmount);
                    file.hitBox.set(amountX + startingX, startingY - amountY, 0.75f * scaleAmount, 0.75f * scaleAmount);
                    if (debugMode) {
                        spriteBatch.draw(hitBoxShow, amountX + startingX, startingY - amountY, 0.75f * scaleAmount, 0.75f * scaleAmount);
                    }
                    font.draw(spriteBatch, file.name, amountX + startingX, startingY - amountY);

                    // Sets the textures to be wrapped.
                    amountFound++;
                    amountX += 1 * scaleAmount;
                    if (amountX > amountInRow * scaleAmount) {
                        amountX = 0;
                        amountY += 1 * scaleAmount;
                    }

                }

            }
        }
//        System.out.println(amountFound);

        if (barVisible) {
            spriteBatch.draw(properties_Texture, touchPos.x, touchPos.y - (5 * scaleAmount), 3.5f * scaleAmount, 5f * scaleAmount);
            if (debugMode) {
                spriteBatch.draw(hitBoxShow, addNew.x, addNew.y, addNew.width, addNew.height);
                spriteBatch.draw(hitBoxShow, deleteItem.x, deleteItem.y, deleteItem.width, deleteItem.height);
            }
        }

        // End drawing.
        spriteBatch.end();
    }

    /*
    Grabs the texture for the matching type.
     */
    public Texture getTextureForType(String type) {
        Texture usedTexture = null;
        switch (type) {
            case "Folder": {
                usedTexture = folder_Texture;
                break;
            }
            case "TextFile": {
                usedTexture = txt_Texture;
                break;
            }

            default: {
                System.out.println("The valid file type texture not found: " + type);
                break;
            }
        }
        return usedTexture;
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your screen here. The parameters represent the new window size.
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
    }
}
