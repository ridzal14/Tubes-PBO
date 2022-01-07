package com.cheeselevel.game.desktop;

import com.badlogic.gdx.Game;
import com.cheeselevel.game.CheeseMenu;

public class CheeseGame extends Game {
    @Override
    public void create() {
        CheeseMenu cm = new CheeseMenu(this);
        setScreen(cm);
    }
}
