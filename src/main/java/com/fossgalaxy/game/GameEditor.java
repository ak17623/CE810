package com.fossgalaxy.game;

import com.fossgalaxy.games.tbs.editor.Editor;
import com.fossgalaxy.games.tbs.io.SettingsIO;

public class GameEditor {

    public static void main(String[] args) {
        Editor.run(SettingsIO.buildWithExtras("com.fossgalaxy.game"),"space-game.json", "Level_1.json" );
    }
}
