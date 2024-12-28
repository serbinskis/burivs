package me.serbinskis.burvis.materials;

import me.serbinskis.burvis.materials.gasses.elements.Air;
import me.serbinskis.burvis.materials.solids.elements.Stone;
import me.serbinskis.burvis.materials.solids.elements.Sand;

import java.util.HashMap;
import java.util.function.Supplier;

public class MaterialRegistry {
    private static final HashMap<String, Supplier<Material>> materialRegistry = new HashMap<>();
    public static final Material AIR = register(Air::new);
    public static final Material SAND = register(Sand::new);
    public static final Material STONE = register(Stone::new);

    public static Material register(Supplier<Material> materialSupplier) {
        materialRegistry.put(materialSupplier.get().getName(), materialSupplier);
        return materialSupplier.get();
    }

    public static Material createMaterial(Material material) {
        Supplier<Material> materialSupplier = materialRegistry.get(material.getName());
        return materialSupplier.get();
    }
}
