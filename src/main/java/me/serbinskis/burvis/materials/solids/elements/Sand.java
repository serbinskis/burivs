package me.serbinskis.burvis.materials.solids.elements;

import me.serbinskis.burvis.materials.solids.MovableSolid;

import java.awt.*;

public class Sand extends MovableSolid {
    public Sand() {
        super("sand", Color.YELLOW, 0.9f, 0.1f, 1602f);
        //super("sand", new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()), 0.9f, 0.1f, 150);
    }
}
