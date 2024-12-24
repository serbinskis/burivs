package me.serbinskis.burvis.utils;

public class PhysicsUtils {
    public static float calculateTerminalVelocity(float density, float gravity) {
        float mass = density; //m (mass) = p (density) * V (volume 1m^2)
        float drag_coefficient = 1.05f; //Cube - 1.05
        float area = 1;

        //https://www.omnicalculator.com/physics/terminal-velocity
        return (float) Math.sqrt((2 * mass * gravity) / (density * area * drag_coefficient));
    }
}
