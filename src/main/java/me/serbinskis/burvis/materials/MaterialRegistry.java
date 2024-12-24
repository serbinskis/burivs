package me.serbinskis.burvis.materials;

import me.serbinskis.burvis.materials.solids.elements.Sand;

import java.awt.*;
import java.util.HashMap;
import java.util.function.Supplier;

public class MaterialRegistry {
    private static final HashMap<String, Supplier<Material>> materialRegistry = new HashMap<>();
    public static final Material EMPTY = register(() -> new Material("empty", Color.BLACK, 0));
    public static final Material SAND = register(Sand::new);

    public static Material register(Supplier<Material> materialSupplier) {
        materialRegistry.put(materialSupplier.get().getName(), materialSupplier);
        return materialSupplier.get();
    }

    public static Material createMaterial(Material material) {
        Supplier<Material> materialSupplier = materialRegistry.get(material.getName());
        return materialSupplier.get();
    }
}
