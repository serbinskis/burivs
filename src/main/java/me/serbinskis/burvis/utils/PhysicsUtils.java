package me.serbinskis.burvis.utils;

public class PhysicsUtils {
    public static float GRAVITY = 9.81f;
    public static float COF = 0.85f; //Coeff of Restitution

    public static float calculateGameTerminalVelocity(float density, float gravity) {
        //For example, sand density is 1602 kg/m3 and gravity 9.8m/s2
        //Which will translate to 49.86 m/s terminal velocity, which is too fast
        return calculateRealTerminalVelocity(density, gravity) / 10f;
    }

    public static float calculateRealTerminalVelocity(float density, float gravity) {
        float mass = density * 0.001f; //Each pixel is 10cm -> 0.1^3 -> m (mass) = p (density) * V (volume m^3)
        float drag_coefficient = 1.05f; //Cube - 1.05
        float area = 0.01f; //10cm -> 0.1^2 => 0.01m2

        //https://www.omnicalculator.com/physics/terminal-velocity
        return (float) Math.sqrt((2f * mass * gravity) / (1.204f * area * drag_coefficient));
    }

    public static float calculateVerticalDrag(float density, float velocity) {
        float drag_coefficient = 1.05f; // Drag coefficient for a square
        float area = 0.01f; // Cross-sectional area in m² (10cm -> 0.1^2 = 0.01m²)

        // Drag force formula: F_drag = 0.5 * C_d * density * velocity^2 * area
        return 0.5f * drag_coefficient * density * velocity * area;
    }
}
